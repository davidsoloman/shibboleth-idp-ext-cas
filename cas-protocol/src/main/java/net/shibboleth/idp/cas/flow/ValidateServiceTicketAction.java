package net.shibboleth.idp.cas.flow;

import java.net.URI;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.authn.Authenticator;
import net.shibboleth.idp.cas.authn.ProxyIdentifiers;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ServiceTicketValidationResponse;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.SessionResolver;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.idp.session.criterion.HttpServletRequestCriterion;
import net.shibboleth.idp.session.criterion.SessionIdCriterion;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * CAS protocol service ticket validation action emits one of the following events based on validation result:
 *
 * <ul>
 *     <li>{@link ProtocolError#ServiceMismatch serviceMismatch}</li>
 *     <li>{@link ProtocolError#SessionExpired sessionExpired}</li>
 *     <li>{@link ProtocolError#SessionRetrievalError sessionRetrievalError}</li>
 *     <li>{@link ProtocolError#TicketExpired ticketExpired}</li>
 *     <li>{@link ProtocolError#TicketNotFromRenew ticketNotFromRenew}</li>
 *     <li>{@link ProtocolError#TicketRetrievalError ticketRetrievalError}</li>
 *     <li>{@link Events#Success success}</li>
 * </ul>
 *
 * <p>
 * In the success case a {@link ServiceTicketValidationResponse} message is created and stored
 * as request scope parameter under the key {@value FlowStateSupport#SERVICE_TICKET_VALIDATION_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class ValidateServiceTicketAction
        extends AbstractProfileAction<TicketValidationRequest, ServiceTicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ValidateServiceTicketAction.class);

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;

    /** Looks up IdP sessions. */
    @Nonnull private SessionResolver sessionResolver;

    /** Performs proxy authentication. */
    @Nonnull private Authenticator<URI, ProxyIdentifiers> proxyAuthenticator;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }

    public void setSessionResolver(@Nonnull final SessionResolver resolver) {
        this.sessionResolver = Constraint.isNotNull(resolver, "Session resolver cannot be null.");
    }

    public void setProxyAuthenticator(@Nonnull final Authenticator<URI, ProxyIdentifiers> proxyAuthenticator) {
        this.proxyAuthenticator = Constraint.isNotNull(proxyAuthenticator, "Proxy authenticator cannot be null.");
    }

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) throws ProfileException {

        final TicketValidationRequest request = FlowStateSupport.getTicketValidationRequest(springRequestContext);
        if (request == null) {
            throw new ProfileException("TicketValidationRequest not found in flow state.");
        }

        final ServiceTicket ticket;
        try {
            log.debug("Attempting to remove ticket {}", request.getTicket());
            ticket = ticketService.removeServiceTicket(request.getTicket());
            if (ticket != null) {
                log.debug("Removed {}", ticket);
            }
        } catch (RuntimeException e) {
            log.debug("CAS ticket retrieval failed with error: {}", e);
            return ProtocolError.TicketRetrievalError.event(this);
        }
        if (ticket == null || ticket.getExpirationInstant().isBeforeNow()) {
            return ProtocolError.TicketExpired.event(this);
        }

        final IdPSession session;
        try {
            session = sessionResolver.resolveSingle(new CriteriaSet(new SessionIdCriterion(ticket.getSessionId())));
        } catch (ResolverException e) {
            log.debug("IdP session retrieval failed with error: {}", e);
            return ProtocolError.SessionRetrievalError.event(this);
        }
        boolean expired = (session == null);
        if (session != null) {
            try {
                expired = !session.checkTimeout();
                log.debug("IdP session expired={}", expired);
            } catch (SessionException e) {
                log.debug("Error performing session timeout check. Assuming session has expired.", e);
                expired = true;
            }
        }
        if (expired) {
            return ProtocolError.SessionExpired.event(this);
        }
        if (!ticket.getService().equalsIgnoreCase(request.getService())) {
            log.debug("Service issued for {} does not match {}", ticket.getService(), request.getService());
            return ProtocolError.ServiceMismatch.event(this);
        }
        if (request.isRenew() != ticket.isRenew()) {
            log.debug("Renew=true requested at validation time but ticket not issued with renew=true.");
            return ProtocolError.TicketNotFromRenew.event(this);
        }
        final ServiceTicketValidationResponse response = new ServiceTicketValidationResponse(session.getPrincipalName());
        FlowStateSupport.setServiceTicketValidationResponse(springRequestContext, response);
        if (request.getPgtUrl() != null) {
            try {
                log.debug("Attempting proxy authentication to {}", request.getPgtUrl());
                final ProxyIdentifiers proxyIds = proxyAuthenticator.authenticate(URI.create(request.getPgtUrl()));
                ticketService.createProxyGrantingTicket(ticket, proxyIds.getPgtId());
                response.setPgtIou(proxyIds.getPgtIou());
            } catch (Exception e) {
                log.info("Proxy authentication failed for " + request.getPgtUrl() + ": " + e);
            }
        }
        log.info("Successfully validated {} for {}", request.getTicket(), request.getService());
        return new Event(this, Events.Success.id());
    }
}
