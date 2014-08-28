/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.authn;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.security.auth.login.FailedLoginException;

import net.shibboleth.idp.cas.protocol.ProtocolParam;
import net.shibboleth.idp.cas.ticket.TicketIdGenerator;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.apache.http.client.utils.URIBuilder;

/**
 * Abstract base class for proxy callback authentication.
 *
 * @author Marvin S. Addison
 */
public abstract class AbstractProxyAuthenticator implements Authenticator<URI, ProxyIdentifiers> {

    /** Required https scheme for proxy callbacks. */
    protected static final String HTTPS_SCHEME = "https";

    /** Generates proxy-granting ticket identifiers. */
    @Nonnull private TicketIdGenerator proxyGrantingTicketIdGenerator;

    /** Generates proxy-granting ticket IOU identifiers. */
    @Nonnull private TicketIdGenerator proxyGrantingTicketIouGenerator;

    @NotEmpty private Set<Integer> allowedResultCodes = Collections.singleton(200);

    protected AbstractProxyAuthenticator(
            @Nonnull final TicketIdGenerator pgtIdGenerator, @Nonnull final TicketIdGenerator pgtIouGenerator) {
        this.proxyGrantingTicketIdGenerator = Constraint.isNotNull(pgtIdGenerator, "PGT ID generator cannot be null");
        this.proxyGrantingTicketIouGenerator = Constraint.isNotNull(pgtIouGenerator, "PGTIOU generator cannot be null");
    }

    public void setAllowedResultCodes(@NotEmpty final Set<Integer> allowedResultCodes) {
        this.allowedResultCodes = (Set<Integer>) Constraint.isNotEmpty(
                allowedResultCodes, "Allowed result codes cannot be null or empty.");
    }

    @Override
    public ProxyIdentifiers authenticate(@Nonnull final URI credential) throws GeneralSecurityException {
        Constraint.isNotNull(credential, "URI to authenticate cannot be null.");
        if (!HTTPS_SCHEME.equalsIgnoreCase(credential.getScheme())) {
            throw new GeneralSecurityException(credential + " is not an https URI as required.");
        }
        final ProxyIdentifiers proxyIds = new ProxyIdentifiers(
                proxyGrantingTicketIdGenerator.generate(),
                proxyGrantingTicketIouGenerator.generate());
        final URI proxyCallbackUri;
        try {
            proxyCallbackUri = new URIBuilder(credential)
                .addParameter(ProtocolParam.PgtId.id(), proxyIds.getPgtId())
                .addParameter(ProtocolParam.PgtIou.id(), proxyIds.getPgtIou())
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating proxy callback URL", e);
        }
        final int status = authenticateProxyCallback(proxyCallbackUri);
        if (!allowedResultCodes.contains(status)) {
            throw new FailedLoginException(credential + " returned unacceptable status code " + status);
        }
        return proxyIds;
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
