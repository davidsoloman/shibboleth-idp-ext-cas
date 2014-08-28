/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Ensures that a service ticket validation request that specifies renew=true matches the renew flag on the ticket
 * that is presented for validation. Possible outcomes:
 * <ul>
 *     <li>{@link Events#Success success}</li>
 *     <li>{@link ProtocolError#TicketNotFromRenew ticketNotFromRenew}</li>
 *     <li>{@link ProtocolError#RenewIncompatibleWithProxy renewIncompatibleWithProxy}</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class ValidateRenewAction extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ValidateRenewAction.class);


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
        final TicketContext ticketContext = profileRequestContext.getSubcontext(TicketContext.class);
        if (ticketContext == null) {
            log.info("TicketContext not found in profile request context.");
            return ProtocolError.IllegalState.event(this);
        }
        final Ticket ticket = ticketContext.getTicket();
        if (ticket instanceof ServiceTicket) {
            if (request.isRenew() != ((ServiceTicket) ticket).isRenew()) {
                log.debug("Renew=true requested at validation time but ticket not issued with renew=true.");
                return ProtocolError.TicketNotFromRenew.event(this);
            }
        } else {
            // Proxy ticket validation
            if (request.isRenew()) {
                return ProtocolError.RenewIncompatibleWithProxy.event(this);
            }
        }
        return Events.Success.event(this);
    }
}
