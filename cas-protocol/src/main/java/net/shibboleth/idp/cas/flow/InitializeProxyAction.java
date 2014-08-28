/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ProtocolParam;
import net.shibboleth.idp.cas.protocol.ProxyTicketRequest;
import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Initializes the CAS protocol interaction at the <code>/proxy</code> URI and returns one of the following events:
 *
 * <ul>
 *     <li>{@link net.shibboleth.idp.cas.flow.Events#Proceed proceed}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#ServiceNotSpecified serviceNotSpecified}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketNotSpecified ticketNotSpecified}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketRetrievalError ticketRetrievalError}</li>
 * </ul>
 *
 * On proceed places a {@link net.shibboleth.idp.cas.protocol.ProxyTicketRequest} object in request scope under the key
 * {@value net.shibboleth.idp.cas.flow.FlowStateSupport#PROXY_TICKET_REQUEST_KEY}. Also creates a {@link TicketContext}
 * containing the PGT provided to the <code>/proxy</code> endpoint and places it under the
 * {@link org.opensaml.profile.context.ProfileRequestContext}.
 *
 * @author Marvin S. Addison
 */
public class InitializeProxyAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(InitializeProxyAction.class);

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

        final ParameterMap params = springRequestContext.getRequestParameters();
        final String service = params.get(ProtocolParam.TargetService.id());
        if (service == null) {
            return ProtocolError.ServiceNotSpecified.event(this);
        }
        final String ticket = params.get(ProtocolParam.Pgt.id());
        if (ticket == null) {
            return ProtocolError.TicketNotSpecified.event(this);
        }
        final ProxyTicketRequest proxyTicketRequest = new ProxyTicketRequest(ticket, service);
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(proxyTicketRequest);
        profileRequestContext.setInboundMessageContext(messageContext);
        FlowStateSupport.setProxyTicketRequest(springRequestContext, proxyTicketRequest);
        try {
            log.debug("Fetching proxy-granting ticket {}", proxyTicketRequest.getPgt());
            profileRequestContext.addSubcontext(
                    new TicketContext(ticketService.fetchProxyGrantingTicket(proxyTicketRequest.getPgt())));
        } catch (RuntimeException e) {
            log.error("Failed looking up " + proxyTicketRequest.getPgt(), e);
            return ProtocolError.TicketRetrievalError.event(this);
        }
        return Events.Proceed.event(this);
    }
}
