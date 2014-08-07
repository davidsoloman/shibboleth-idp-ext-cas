package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.Ticket;
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
 *     <li>{@link Events#ServiceTicketValidated serviceTicketValidated}</li>
 *     <li>{@link Events#ProxyTicketValidated proxyTicketValidated}</li>
 *     <li>{@link ProtocolError#InvalidTicketFormat invalidTicketFormat}</li>
 *     <li>{@link ProtocolError#ServiceMismatch serviceMismatch}</li>
 *     <li>{@link ProtocolError#TicketExpired ticketExpired}</li>
 *     <li>{@link ProtocolError#TicketRetrievalError ticketRetrievalError}</li>
 * </ul>
 *
 * <p>
 * In the success case a {@link net.shibboleth.idp.cas.protocol.TicketValidationResponse} message is created and stored
 * as request scope parameter under the key {@value FlowStateSupport#TICKET_VALIDATION_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class ValidateTicketAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ValidateTicketAction.class);

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

        final TicketValidationRequest request = FlowStateSupport.getTicketValidationRequest(springRequestContext);
        if (request == null) {
            log.info("TicketValidationRequest not found in flow state.");
            return ActionSupport.buildEvent(this, EventIds.INVALID_PROFILE_CTX);
        }

        final Ticket ticket;
        try {
            final String ticketId = request.getTicket();
            log.debug("Attempting to validate {}", ticketId);
            if (ticketId.startsWith("ST-")) {
                ticket = ticketService.removeServiceTicket(request.getTicket());
            } else if (ticketId.startsWith("PT-")) {
                ticket = ticketService.removeProxyTicket(ticketId);
            } else {
                return ProtocolError.InvalidTicketFormat.event(this);
            }
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

        if (!ticket.getService().equalsIgnoreCase(request.getService())) {
            log.debug("Service issued for {} does not match {}", ticket.getService(), request.getService());
            return ProtocolError.ServiceMismatch.event(this);
        }

        profileRequestContext.addSubcontext(new TicketContext(ticket));
        FlowStateSupport.setTicketValidationResponse(springRequestContext, new TicketValidationResponse());
        log.info("Successfully validated {} for {}", request.getTicket(), request.getService());
        if (ticket instanceof ServiceTicket) {
            return Events.ServiceTicketValidated.event(this);
        }
        return Events.ProxyTicketValidated.event(this);
    }
}
