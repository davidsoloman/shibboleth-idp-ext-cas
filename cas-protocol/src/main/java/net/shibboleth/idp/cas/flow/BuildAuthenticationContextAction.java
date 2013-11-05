package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Builds an authentication context message from an incoming {@link ServiceTicketRequest} message.
 *
 * @author Marvin S. Addison
 */
public class BuildAuthenticationContextAction extends AbstractProfileAction<ServiceTicketRequest, Object> {
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ServiceTicketRequest, Object> profileRequestContext)
            throws ProfileException {

        final AuthenticationContext ac = new AuthenticationContext();

        final ServiceTicketRequest request = profileRequestContext.getInboundMessageContext().getMessage();
        ac.setForceAuthn(request.isRenew());
        ac.setIsPassive(false);

        profileRequestContext.addSubcontext(ac, true);
        return ActionSupport.buildProceedEvent(this);
    }
}
