/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import java.net.URI;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.authn.Authenticator;
import net.shibboleth.idp.cas.authn.ProxyIdentifiers;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.ticket.ProxyTicket;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Validates the proxy callback URL provided in the service ticket validation request and creates a PGT when
 * the proxy callback is successfully authenticated. Possible outcomes:
 *
 * <ul>
 *     <li>{@link Events#Success success}</li>
 *     <li>{@link Events#Failure failure}</li>
 * </ul>
 *
 * On success, the PGTIOU is placed in the {@link TicketValidationResponse#getPgtIou()}.
 *
 * @author Marvin S. Addison
 */
public class ValidateProxyCallbackAction
    extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ValidateProxyCallbackAction.class);

    /** Performs proxy authentication. */
    @Nonnull private Authenticator<URI, ProxyIdentifiers> proxyAuthenticator;

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;


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
            return ProtocolError.IllegalState.event(this);
        }
        final TicketValidationResponse response =
                FlowStateSupport.getTicketValidationResponse(springRequestContext);
        if (response == null) {
            log.info("TicketValidationResponse not found in flow state.");
            return ProtocolError.IllegalState.event(this);
        }
        final TicketContext ticketContext = profileRequestContext.getSubcontext(TicketContext.class);
        if (ticketContext == null) {
            log.info("TicketContext not found in profile request context.");
            return ProtocolError.IllegalState.event(this);
        }
        final Ticket ticket = ticketContext.getTicket();
        try {
            log.debug("Attempting proxy authentication to {}", request.getPgtUrl());
            final ProxyIdentifiers proxyIds = proxyAuthenticator.authenticate(URI.create(request.getPgtUrl()));
            if (ticket instanceof ServiceTicket) {
                ticketService.createProxyGrantingTicket((ServiceTicket) ticket, proxyIds.getPgtId());
            } else {
                ticketService.createProxyGrantingTicket((ProxyTicket) ticket, proxyIds.getPgtId());
            }
            response.setPgtIou(proxyIds.getPgtIou());
        } catch (Exception e) {
            log.info("Proxy authentication failed for " + request.getPgtUrl() + ": " + e);
            return Events.Failure.event(this);
        }
        return Events.Success.event(this);
    }
}
