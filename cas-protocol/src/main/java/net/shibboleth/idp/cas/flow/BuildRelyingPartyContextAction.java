package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.attribute.context.AttributeContext;
import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import net.shibboleth.idp.session.context.SessionContext;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Creates the following contexts needed for attribute resolution:
 * <ul>
 *     <li>{@link RelyingPartyContext} - Relying party ID is the CAS protocol <code>service</code> parameter.</li>
 *     <li>{@link AttributeContext} - Child of {@link RelyingPartyContext} will hold resolved attributes.</li>
 *     <li>{@link SubjectContext} - Contains IdP session principal name needed for attribute resolution.</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class BuildRelyingPartyContextAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BuildRelyingPartyContextAction.class);

    @Nonnull
    @Override
    protected Event doExecute(
        final @Nonnull RequestContext springRequestContext,
        final @Nonnull ProfileRequestContext<TicketValidationRequest, TicketValidationResponse> profileRequestContext) {

        final TicketValidationRequest request = FlowStateSupport.getTicketValidationRequest(springRequestContext);
        if (request == null) {
            log.info("TicketValidationRequest not found in request scope.");
            return ActionSupport.buildEvent(this, EventIds.INVALID_PROFILE_CTX);
        }
        final SessionContext sessionContext = profileRequestContext.getSubcontext(SessionContext.class);
        if (sessionContext == null || sessionContext.getIdPSession() == null) {
            log.info("Cannot locate IdP session");
            return ActionSupport.buildEvent(this, EventIds.INVALID_PROFILE_CTX);
        }
        final RelyingPartyContext rpc = new RelyingPartyContext();
        rpc.setAnonymous(false);
        rpc.setRelyingPartyId(request.getService());
        rpc.addSubcontext(new AttributeContext());
        profileRequestContext.addSubcontext(rpc);
        final SubjectContext sc = new SubjectContext();
        sc.setPrincipalName(sessionContext.getIdPSession().getPrincipalName());
        profileRequestContext.addSubcontext(sc);
        return Events.Proceed.event(this);
    }
}
