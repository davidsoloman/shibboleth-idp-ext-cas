/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.*;
import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.idp.cas.ticket.ProxyTicket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Generates and stores a CAS protocol proxy ticket. Possible outcomes:
 * <ul>
 *     <li>{@link net.shibboleth.idp.cas.flow.Events#Success success}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketCreationError ticketCreationError}</li>
 * </ul>
 * In the success case a {@link net.shibboleth.idp.cas.protocol.ProxyTicketResponse} message is created and stored
 * as request scope parameter under the key {@value net.shibboleth.idp.cas.flow.FlowStateSupport#PROXY_TICKET_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class GrantProxyTicketAction extends AbstractProfileAction<ProxyTicketRequest, ProxyTicketResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GrantProxyTicketAction.class);

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ProxyTicketRequest, ProxyTicketResponse> profileRequestContext) {

        final ProxyTicketRequest request = FlowStateSupport.getProxyTicketRequest(springRequestContext);
        final TicketContext ticketContext = profileRequestContext.getSubcontext(TicketContext.class);
        if (ticketContext == null) {
            log.info("TicketContext not found in profile request context.");
            return ProtocolError.IllegalState.event(this);
        }
        final ProxyGrantingTicket pgt = (ProxyGrantingTicket) ticketContext.getTicket();
        final ProxyTicket pt;
        try {
            log.debug("Granting proxy ticket for {}", request.getTargetService());
            pt = ticketService.createProxyTicket(pgt, request.getTargetService());
        } catch (RuntimeException e) {
            log.error("Failed granting proxy ticket due to error.", e);
            return ProtocolError.TicketCreationError.event(this);
        }
        log.info("Granted proxy ticket for {}", request.getTargetService());
        FlowStateSupport.setProxyTicketResponse(springRequestContext, new ProxyTicketResponse(pt.getId()));
        return Events.Success.event(this);
    }
}
