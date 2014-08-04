package net.shibboleth.idp.cas.flow;

import java.net.URI;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.authn.Authenticator;
import net.shibboleth.idp.cas.authn.ProxyIdentifiers;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ServiceTicketValidationResponse;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * CAS protocol service ticket validation action emits one of the following events based on validation result:
 *
 * <ul>
 *     <li>{@link Events#Success success}</li>
 *     <li>{@link ProtocolError#ServiceMismatch serviceMismatch}</li>
 *     <li>{@link ProtocolError#TicketExpired ticketExpired}</li>
 *     <li>{@link ProtocolError#TicketNotFromRenew ticketNotFromRenew}</li>
 *     <li>{@link ProtocolError#TicketRetrievalError ticketRetrievalError}</li>
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

    /** Performs proxy authentication. */
    @Nonnull private Authenticator<URI, ProxyIdentifiers> proxyAuthenticator;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }


    public void setProxyAuthenticator(@Nonnull final Authenticator<URI, ProxyIdentifiers> proxyAuthenticator) {
        this.proxyAuthenticator = Constraint.isNotNull(proxyAuthenticator, "Proxy authenticator cannot be null.");
    }

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) {

        final TicketValidationRequest request = FlowStateSupport.getTicketValidationRequest(springRequestContext);
        if (request == null) {
            log.info("TicketValidationRequest not found in flow state.");
            return ActionSupport.buildEvent(this, EventIds.INVALID_PROFILE_CTX);
        }

        final ServiceTicket ticket;
        try {
            log.debug("Attempting to validate {}", request.getTicket());
            ticket = ticketService.removeServiceTicket(request.getTicket());
            if (ticket != null) {
                log.debug("Found and removed {}/{} from ticket store", ticket, ticket.getSessionId());
            }
        } catch (RuntimeException e) {
            log.debug("CAS ticket retrieval failed with error: {}", e);
            return ProtocolError.TicketRetrievalError.event(this);
        }
        if (ticket == null || ticket.getExpirationInstant().isBeforeNow()) {
            return ProtocolError.TicketExpired.event(this);
        }
        profileRequestContext.addSubcontext(new TicketContext(ticket));

        if (!ticket.getService().equalsIgnoreCase(request.getService())) {
            log.debug("Service issued for {} does not match {}", ticket.getService(), request.getService());
            return ProtocolError.ServiceMismatch.event(this);
        }
        if (request.isRenew() != ticket.isRenew()) {
            log.debug("Renew=true requested at validation time but ticket not issued with renew=true.");
            return ProtocolError.TicketNotFromRenew.event(this);
        }
        final ServiceTicketValidationResponse response = new ServiceTicketValidationResponse();
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
        return Events.Success.event(this);
    }
}
