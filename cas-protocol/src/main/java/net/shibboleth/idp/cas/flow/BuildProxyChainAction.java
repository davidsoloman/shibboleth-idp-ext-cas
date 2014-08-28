/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.idp.cas.ticket.ProxyTicket;
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
 * Action that builds the chain of visited proxies for a successful proxy ticket validation event. Possible outcomes:
 *
 * <ul>
 *     <li>{@link Events#Proceed proceed}</li>
 *     <li>{@link ProtocolError#InvalidTicketType invalidTicketTypew}</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class BuildProxyChainAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BuildProxyChainAction.class);

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }

    @Nonnull
    @Override
    protected Event doExecute(
    final @Nonnull RequestContext springRequestContext,
    final @Nonnull ProfileRequestContext profileRequestContext) {

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
        if (!(ticketContext.getTicket() instanceof ProxyTicket)) {
            return ProtocolError.InvalidTicketType.event(this);
        }
        final ProxyTicket pt = (ProxyTicket) ticketContext.getTicket();
        ProxyGrantingTicket pgt;
        String pgtId = pt.getPgtId();
        do {
            pgt = ticketService.fetchProxyGrantingTicket(pgtId);
            response.addProxy(pgt.getService());
            pgtId = pgt.getParentId();
        } while (pgtId != null);

        return Events.Proceed.event(this);
    }
}
