/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.service.Service;
import net.shibboleth.idp.cas.service.ServiceContext;
import net.shibboleth.idp.cas.service.ServiceRegistry;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Creates the {@link RelyingPartyContext} as a child of the {@link org.opensaml.profile.context.ProfileRequestContext}.
 *
 * @author Marvin S. Addison
 */
public class BuildRelyingPartyContextAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Name of group to which unverified services belong. */
    public static final String UNVERIFIED_GROUP = "unverified";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BuildRelyingPartyContextAction.class);

    /** Repository for verified CAS services (relying parties). */
    @Nonnull
    private final ServiceRegistry serviceRegistry;

    /**
     * Creates a new instance.
     *
     * @param registry Service registry.
     */
    public BuildRelyingPartyContextAction(@Nonnull final ServiceRegistry registry) {
        this.serviceRegistry = Constraint.isNotNull(registry, "Service registry cannot be null");
    }

    @Nonnull
    @Override
    protected Event doExecute(
        final @Nonnull RequestContext springRequestContext,
        final @Nonnull ProfileRequestContext<TicketValidationRequest, TicketValidationResponse> profileRequestContext) {

        final String serviceURL;
        if (FlowStateSupport.getServiceTicketRequest(springRequestContext) != null) {
            serviceURL = FlowStateSupport.getServiceTicketRequest(springRequestContext).getService();
        } else if (FlowStateSupport.getProxyTicketRequest(springRequestContext) != null) {
            serviceURL = FlowStateSupport.getProxyTicketRequest(springRequestContext).getTargetService();
        } else if (FlowStateSupport.getTicketValidationRequest(springRequestContext) != null) {
            serviceURL = FlowStateSupport.getTicketValidationRequest(springRequestContext).getService();
        } else {
            log.info("Service URL not found in flow state");
            return ProtocolError.IllegalState.event(this);
        }
        Service service = serviceRegistry.lookup(serviceURL);
        final RelyingPartyContext rpc = new RelyingPartyContext();
        rpc.setVerified(service != null);
        rpc.setRelyingPartyId(serviceURL);
        if (service == null) {
            service = new Service(serviceURL, UNVERIFIED_GROUP, false);
        }
        profileRequestContext.addSubcontext(rpc);
        profileRequestContext.addSubcontext(new ServiceContext(service));
        return ActionSupport.buildProceedEvent(this);
    }
}
