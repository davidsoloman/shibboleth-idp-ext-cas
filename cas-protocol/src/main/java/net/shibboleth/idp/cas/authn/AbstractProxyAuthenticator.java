/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.authn;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.security.auth.login.FailedLoginException;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Abstract base class for proxy callback authentication by means of validating an HTTPS endpoint.
 *
 * @author Marvin S. Addison
 */
public abstract class AbstractProxyAuthenticator implements Authenticator<URI, Void> {

    /** Required https scheme for proxy callbacks. */
    protected static final String HTTPS_SCHEME = "https";

    /** List of HTTP response codes permitted for successful proxy callback. */
    @NotEmpty
    @NonnullElements
    private Set<Integer> allowedResponseCodes = Collections.singleton(200);

    /**
     * Sets the HTTP response codes permitted for successful authentication of the proxy callback URL.
     *
     * @param responseCodes One or more HTTP response codes.
     */
    public void setAllowedResponseCodes(@NotEmpty @NonnullElements final Set<Integer> responseCodes) {
        Constraint.isNotEmpty(responseCodes, "Response codes cannot be null or empty.");
        Constraint.noNullItems(responseCodes.toArray(), "Response codes cannot contain null elements.");
        this.allowedResponseCodes = responseCodes;
    }

    @Override
    public Void authenticate(@Nonnull final URI credential) throws GeneralSecurityException {
        Constraint.isNotNull(credential, "URI to authenticate cannot be null.");
        if (!HTTPS_SCHEME.equalsIgnoreCase(credential.getScheme())) {
            throw new GeneralSecurityException(credential + " is not an https URI as required.");
        }
        final int status = authenticateProxyCallback(credential);
        if (!allowedResponseCodes.contains(status)) {
            throw new FailedLoginException(credential + " returned unacceptable HTTP status code " + status);
        }
        return null;
    }

    /**
     * Authenticates the proxy callback URI by making an HTTP GET request and returning the HTTP response code.
     *
     * @param callbackUri Proxy callback URI containing requisite CAS protocol parameters, <code>pgtId</code> and
     *                    <code>pgtIou</code>.
     *
     * @return Status code from HTTP GET request.
     *
     * @throws GeneralSecurityException On a failure related to establishing the HTTP connection due to SSL/TLS errors.
     * @throws RuntimeException On networking errors (IO, HTTP protocol).
     */
    protected abstract int authenticateProxyCallback(URI callbackUri) throws GeneralSecurityException;

}
