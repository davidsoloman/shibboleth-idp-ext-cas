package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ProtocolParam;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Initializes the CAS protocol interaction at the <code>/login</code> URI and returns one of the following events:
 *
 * <ul>
 *     <li>{@link ProtocolError#ServiceNotSpecified serviceNotSpecified}</li>
 *     <li>{@link ProtocolError#TicketNotSpecified ticketNotSpecified}</li>
 *     <li><code>proceed</code></li>
 * </ul>
 *
 * Places a {@link TicketValidationRequest} object in request scope under the key
 * {@value FlowStateSupport#TICKET_VALIDATION_REQUEST_KEY} in the case of a <code>proceed</code> event.
 *
 * @author Marvin S. Addison
 */
public class InitializeValidateAction extends AbstractProfileAction {
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) {

        final ParameterMap params = springRequestContext.getRequestParameters();
        final String service = params.get(ProtocolParam.Service.id());
        if (service == null) {
            return ProtocolError.ServiceNotSpecified.event(this);
        }
        final String ticket = params.get(ProtocolParam.Ticket.id());
        if (ticket == null) {
            return ProtocolError.TicketNotSpecified.event(this);
        }
        final TicketValidationRequest ticketValidationRequest = new TicketValidationRequest(service, ticket);

        final String renew = params.get(ProtocolParam.Renew.id());
        if (renew != null) {
            ticketValidationRequest.setRenew(true);
        }
        ticketValidationRequest.setPgtUrl(params.get(ProtocolParam.PgtUrl.id()));

        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(ticketValidationRequest);
        profileRequestContext.setInboundMessageContext(messageContext);
        FlowStateSupport.setTicketValidationRequest(springRequestContext, ticketValidationRequest);
        return ActionSupport.buildProceedEvent(profileRequestContext);
    }
}
