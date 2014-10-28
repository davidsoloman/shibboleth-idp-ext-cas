/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.ProtocolParam;
import net.shibboleth.idp.cas.protocol.SamlParam;
import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Initializes the CAS protocol interaction at the <code>/login</code> URI. Possible outcomes:
 * <ul>
 *     <li>{@link net.shibboleth.idp.cas.flow.Events#Proceed proceed}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#ServiceNotSpecified serviceNotSpecified}</li>
 * </ul>
 * On success places a {@link ServiceTicketRequest} object in request scope under the key
 * {@value FlowStateSupport#SERVICE_TICKET_REQUEST_KEY}.
 *
 * @author Marvin S. Addison
 */
public class InitializeLoginAction extends AbstractProfileAction<ServiceTicketRequest, Object> {

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ServiceTicketRequest, Object> profileRequestContext) {

        final ParameterMap params = springRequestContext.getRequestParameters();
        String service = params.get(ProtocolParam.Service.id());
        boolean isSaml = false;
        if (service == null) {
            service = params.get(SamlParam.TARGET.name());
            if (service == null) {
                return ProtocolError.ServiceNotSpecified.event(this);
            }
            isSaml = true;
        }
        final ServiceTicketRequest serviceTicketRequest = new ServiceTicketRequest(service);
        serviceTicketRequest.setSaml(isSaml);

        final String renew = params.get(ProtocolParam.Renew.id());
        if (renew != null) {
            serviceTicketRequest.setRenew(true);
        }

        // http://www.jasig.org/cas/protocol, section 2.1.1
        // It is RECOMMENDED that CAS implementations ignore the "gateway" parameter if "renew" is set.
        final String gateway = params.get(ProtocolParam.Gateway.id());
        if (gateway != null && renew == null) {
            serviceTicketRequest.setGateway(true);
        }

        final MessageContext<ServiceTicketRequest> messageContext = new MessageContext<>();
        messageContext.setMessage(serviceTicketRequest);
        profileRequestContext.setInboundMessageContext(messageContext);
        FlowStateSupport.setServiceTicketRequest(springRequestContext, serviceTicketRequest);
        return ActionSupport.buildProceedEvent(this);
    }
}
