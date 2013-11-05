package net.shibboleth.idp.cas.demo.authn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;

import net.shibboleth.idp.authn.AbstractValidationAction;
import net.shibboleth.idp.authn.AuthenticationException;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.UsernamePrincipal;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.UsernamePasswordContext;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticates username password credentials from a form using a simple test where authentication succeeds for
 * any credential pair where the password equals the username.
 *
 * @author Marvin S. Addison
 */
public class DemoAuthenticationAction extends AbstractValidationAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(DemoAuthenticationAction.class);

    /** UsernamePasswordContext containing the credentials to validate. */
    @Nullable
    private UsernamePasswordContext upContext;

    /** {@inheritDoc} */
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext,
                                   @Nonnull final AuthenticationContext authenticationContext) throws AuthenticationException {
        if (authenticationContext.getAttemptedFlow() == null) {
            log.debug("{} no attempted flow within authentication context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }

        upContext = authenticationContext.getSubcontext(UsernamePasswordContext.class, false);
        if (upContext == null) {
            log.debug("{} no UsernameContext available within authentication context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return false;
        }

        if (upContext.getUsername() == null || upContext.getPassword() == null) {
            log.debug("{} no username or password available within UsernamePasswordContext", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return false;
        }

        return super.doPreExecute(profileRequestContext, authenticationContext);
    }

    /** {@inheritDoc} */
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
                             @Nonnull final AuthenticationContext authenticationContext) throws AuthenticationException {

        try {
            log.debug("{} attempting to authenticate user {}", getLogPrefix(), upContext.getUsername());
            if (upContext.getUsername().equals(upContext.getPassword())) {
                buildAuthenticationResult(profileRequestContext, authenticationContext);
            } else {
                throw new FailedLoginException();
            }
            log.debug("{} login by '{}' succeeded", getLogPrefix(), upContext.getUsername());
            buildAuthenticationResult(profileRequestContext, authenticationContext);
        } catch (Exception e) {
            log.debug(getLogPrefix() + " login by '" + upContext.getUsername() + "' failed", e);
            handleError(profileRequestContext, authenticationContext, e, AuthnEventIds.INVALID_CREDENTIALS);
        }
    }

    @Nonnull
    @Override
    protected Subject populateSubject(final @Nonnull Subject subject) throws AuthenticationException {
        subject.getPrincipals().add(new UsernamePrincipal(upContext.getUsername()));
        return subject;
    }
}
