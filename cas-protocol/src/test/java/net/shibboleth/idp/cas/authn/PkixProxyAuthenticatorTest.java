package net.shibboleth.idp.cas.authn;

import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.idp.spring.IdPPropertiesApplicationContextInitializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * Unit test for {@link PkixProxyAuthenticator} class.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration(
        locations = {
                "/system/conf/global-system.xml",
                "/conf/global.xml",
                "/system/conf/mvc-beans.xml",
                "/conf/cas-protocol-beans.xml",
                "/test/test-beans.xml",
                "/test/test-cas-beans.xml",
                "/test/test-webflow-config.xml" },
        initializers = IdPPropertiesApplicationContextInitializer.class)
@WebAppConfiguration
public class PkixProxyAuthenticatorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PkixProxyAuthenticator proxyAuthenticator;

    @DataProvider(name = "data")
    public Object[][] buildTestData() {
        return new Object[][] {
                new Object[] { "src/test/resources/certs/nobody-1.p12", 200, new CertificateException() },
                new Object[] { "src/test/resources/certs/nobody-2.p12", 200, null },
                new Object[] { "src/test/resources/certs/nobody-2.p12", 404, new FailedLoginException() },
        };
    }

    @Test(dataProvider = "data")
    public void testAuthenticate(final String pkcs12Path, final int status, final Exception expected) throws Exception {
        Server server = null;
        try {
            server = startServer(pkcs12Path, new ConfigurableStatusHandler(status));
            final ProxyIdentifiers ids = proxyAuthenticator.authenticate(new URI("https://localhost:8443/"));
            if (expected != null) {
                fail("Proxy authentication should have failed with " + expected);
            }
            assertNotNull(ids);
            assertNotNull(ids.getPgtId());
            assertNotNull(ids.getPgtIou());
        } catch (Exception e) {
            if (expected == null) {
                throw e;
            }
            assertTrue(expected.getClass().isAssignableFrom(e.getClass()));
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    private Server startServer(final String pkcs12Path, final Handler handler) {
        final Server server = new Server();

        final SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setKeyStorePath(pkcs12Path);
        sslContextFactory.setKeyStorePassword("changeit");
        final ServerConnector connector = new ServerConnector(server, sslContextFactory);
        connector.setHost("127.0.0.1");
        connector.setPort(8443);
        server.setConnectors(new Connector[] { connector });
        server.setHandler(handler);
        try {
            server.start();
        } catch (Exception e) {
            try {
                server.stop();
            } catch (Exception e2) {}
            throw new RuntimeException("Jetty startup failed", e);
        }
        final Thread serverRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.join();
                } catch (InterruptedException e) {}
            }
        });
        serverRunner.start();
        return server;
    }

    private static class ConfigurableStatusHandler extends AbstractHandler {

        final int status;

        public ConfigurableStatusHandler(final int status) {
            this.status = status;
        }

        @Override
        public void handle(
                final String target,
                final Request request,
                final HttpServletRequest servletRequest,
                final HttpServletResponse servletResponse) throws IOException, ServletException {

            servletResponse.setContentType("text/plain;charset=utf-8");
            servletResponse.setStatus(status);
            request.setHandled(true);
            servletResponse.getWriter().println("OK");
        }
    }
}
