/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.config;

import net.shibboleth.idp.cas.authn.Authenticator;
import net.shibboleth.idp.cas.ticket.TicketIdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import javax.annotation.Nonnull;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * CAS proxy-granting ticket configuration modeled as an IdP profile.
 *
 * @author Marvin S. Addison
 */
public class ProxyGrantingTicketConfiguration extends AbstractTicketConfiguration {
    /** Proxy ticket profile URI. */
    public static final String PROFILE_ID = PROTOCOL_URI + "/pgt";

    /** Hostname verification strategy used in validating proxy callback. */
    @Nonnull
    private Authenticator<URI, Void> proxyAuthenticator;


    /** PGTIOU ticket ID generator. */
    @Nonnull
    private IdentifierGenerationStrategy pgtIOUGenerator = new TicketIdentifierGenerationStrategy(50, "PGTIOU");


    /** Creates a new instance. */
    public ProxyGrantingTicketConfiguration() {
        super(PROFILE_ID);
    }

    @Override
    public void initialize() throws ComponentInitializationException {
        Constraint.isNotNull(getSecurityConfiguration().getClientTLSValidationConfiguration(),
                "TLS validation configuration cannot be null");
        super.initialize();
    }

    /**
     * @return PGTIOU ticket ID generator.
     */
    @Nonnull
    public IdentifierGenerationStrategy getPGTIOUGenerator() {
        return pgtIOUGenerator;
    }

    /**
     * Sets the PGTIOU ticket ID generator.
     *
     * @param generator ID generator.
     */
    public void setPGTIOUGenerator(@Nonnull IdentifierGenerationStrategy generator) {
        this.pgtIOUGenerator = Constraint.isNotNull(generator, "PGTIOU generator cannot be null");
    }
}
