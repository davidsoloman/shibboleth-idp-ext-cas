/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.authn;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import net.shibboleth.idp.cas.ticket.TicketIdGenerator;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticates a proxy callback URL over SSL/TLS by performing a PKIX trust check using Apache HttpComponents.
 *
 * @author Marvin S. Addison
 */
public class PkixProxyAuthenticator extends AbstractProxyAuthenticator {

    /** Default connection and socket timeout. */
    private static final int DEFAULT_TIMEOUT = 3000;

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PkixProxyAuthenticator.class);

    private final SSLConnectionSocketFactory socketFactory;

    /** Connection and socket timeout. */
    @Positive private int timeout = DEFAULT_TIMEOUT;

    /**
     * Creates a new instance that uses strict hostname verification.
     *
     * @param pgtIdGenerator Generator of PGT identifiers.
     * @param pgtIouGenerator Generator of PGTIOU identifiers.
     * @param trustStore Trust store used for remote peer certificate verification.
     */
    public PkixProxyAuthenticator(
            @Nonnull final TicketIdGenerator pgtIdGenerator,
            @Nonnull final TicketIdGenerator pgtIouGenerator,
            @Nonnull final KeyStore trustStore) {
        this(pgtIdGenerator, pgtIouGenerator, trustStore, SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
    }

    /**
     * Creates a new instance that uses the given hostname verification strategy.
     *
     * @param pgtIdGenerator Generator of PGT identifiers.
     * @param pgtIouGenerator Generator of PGTIOU identifiers.
     * @param trustStore Trust store used for remote peer certificate verification.
     * @param hostnameVerifier Apache HttpComponents hostname verifier instance.
     */
    public PkixProxyAuthenticator(
            @Nonnull final TicketIdGenerator pgtIdGenerator,
            @Nonnull final TicketIdGenerator pgtIouGenerator,
            @Nonnull final KeyStore trustStore,
            @Nonnull final X509HostnameVerifier hostnameVerifier) {
        super(pgtIdGenerator, pgtIouGenerator);
        Constraint.isNotNull(trustStore, "Trust store cannot be null.");
        Constraint.isNotNull(hostnameVerifier, "Hostname verifier cannot be null.");
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .useTLS()
                    .loadTrustMaterial(trustStore)
                    .build();
            socketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        } catch (Exception e) {
            throw new RuntimeException("SSL initialization error", e);
        }
    }

    /**
     * Sets a connection and socket timeout used for making a connection to validate the proxy callback URL.
     *
     * @param timeout Non-zero timeout used for both connection and socket timeouts.
     */
    public void setTimeout(@Positive final int timeout) {
        this.timeout = (int) Constraint.isGreaterThan(timeout, 0, "Timeout must be positive");
    }

    @Override
    protected int authenticateProxyCallback(final URI callbackUri) throws GeneralSecurityException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createHttpClient();
            log.debug("Attempting to connect to {}", callbackUri);
            final HttpGet request = new HttpGet(callbackUri);
            request.setConfig(
                    RequestConfig.custom()
                            .setConnectionRequestTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setSocketTimeout(timeout)
                            .build());
            response = httpClient.execute(request);
            return response.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            throw new RuntimeException("HTTP protocol error", e);
        } catch (SSLException e) {
            if (e.getCause() instanceof CertificateException) {
                throw (CertificateException) e.getCause();
            }
            throw new GeneralSecurityException("SSL connection error", e);
        } catch (IOException e) {
            throw new RuntimeException("IO error", e);
        } finally {
            close(response);
            close(httpClient);
        }
    }

    private CloseableHttpClient createHttpClient() {
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTPS_SCHEME, socketFactory).build();
        final BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(registry);
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                log.warn("Error closing " + resource, e);
            }
        }
    }
}
