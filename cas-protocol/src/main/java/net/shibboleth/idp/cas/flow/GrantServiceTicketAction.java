/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Generates and stores a CAS protocol service ticket. Possible outcomes:
 * <ul>
 *     <li>{@link net.shibboleth.idp.cas.flow.Events#Success success}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketCreationError ticketCreationError}</li>
 * </ul>
 * In the success case a {@link ServiceTicketResponse} message is created and stored
 * as request scope parameter under the key {@value FlowStateSupport#SERVICE_TICKET_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class GrantServiceTicketAction extends AbstractProfileAction<ServiceTicketRequest, ServiceTicketRequest> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GrantServiceTicketAction.class);

    /** Manages CAS tickets. */
    @Nonnull private final TicketService ticketService;


    /**
     * Creates a new instance.
     *
     * @param ticketService Ticket service component.
     */
    public GrantServiceTicketAction(@Nonnull TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "TicketService cannot be null");
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ServiceTicketRequest, ServiceTicketRequest> profileRequestContext) {

        final ServiceTicketRequest request = FlowStateSupport.getServiceTicketRequest(springRequestContext);
        final SessionContext sessionCtx = profileRequestContext.getSubcontext(SessionContext.class, false);
        if (sessionCtx == null || sessionCtx.getIdPSession() == null) {
            log.info("Cannot locate IdP session");
            return ProtocolError.IllegalState.event(this);
        }
        final ServiceTicket ticket;
        try {
            log.debug("Granting service ticket for {}", request.getService());
            ticket = ticketService.createServiceTicket(
                    sessionCtx.getIdPSession().getId(), request.getService(), request.isRenew());
        } catch (RuntimeException e) {
            log.error("Failed granting service ticket due to error.", e);
            return ProtocolError.TicketCreationError.event(this);
        }
        log.info("Granted service ticket for {}", request.getService());
        final ServiceTicketResponse response = new ServiceTicketResponse(request.getService(), ticket.getId());
        if (request.isSaml()) {
            response.setSaml(true);
        }
        FlowStateSupport.setServiceTicketResponse(springRequestContext, response);
        return Events.Success.event(this);
    }
}
