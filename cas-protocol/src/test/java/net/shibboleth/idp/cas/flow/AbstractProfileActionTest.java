/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.idp.spring.IdPPropertiesApplicationContextInitializer;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Abstract base class for all tests that of actions that extend {@link AbstractProfileAction}.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration(
        locations = {
            "/system/conf/global-system.xml",
            "/system/conf/mvc-beans.xml",
            "/system/conf/relying-party-system.xml",
            "/test/test-service-registry.xml",
            "/test/test-webflow-config.xml",
            "/flows/cas/login/login-beans.xml",
            "/flows/cas/serviceValidate/serviceValidate-beans.xml",
           "/flows/cas/proxyValidate/proxyValidate-beans.xml"},
        initializers = IdPPropertiesApplicationContextInitializer.class)
@WebAppConfiguration
public abstract class AbstractProfileActionTest extends AbstractTestNGSpringContextTests {

    protected static final String TEST_SESSION_ID = "+TkSGIRofZyue/p8F4M7TA==";

    protected static final String TEST_PRINCIPAL_NAME = "omega";

    protected static RequestContext createProfileContext() {
        final MockRequestContext requestContext = new MockRequestContext();
        final MockExternalContext externalContext = new MockExternalContext();
        externalContext.setNativeRequest(new MockHttpServletRequest());
        externalContext.setNativeResponse(new MockHttpServletResponse());
        requestContext.setExternalContext(externalContext);
        final ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        requestContext.getConversationScope().put(ProfileRequestContext.BINDING_KEY, profileRequestContext);
        return requestContext;
    }

    protected static ProfileRequestContext getProfileContext(final RequestContext context) {
        return (ProfileRequestContext) context.getConversationScope().get(ProfileRequestContext.BINDING_KEY);
    }

    protected static RequestContext createSessionContext(final String sessionId) {
        return createSessionContext(sessionId, true);
    }

    protected static RequestContext createSessionContext(final String sessionId, final boolean expiredFlag) {
        final RequestContext requestContext = createProfileContext();
        final ProfileRequestContext profileRequestContext =
                (ProfileRequestContext) requestContext.getConversationScope().get(ProfileRequestContext.BINDING_KEY);
        final SessionContext sessionContext = new SessionContext();
        sessionContext.setIdPSession(createSession(sessionId, expiredFlag));
        profileRequestContext.addSubcontext(sessionContext);
        return requestContext;
    }

    protected static IdPSession createSession(final String sessionId, final boolean expiredFlag) {
        final IdPSession mockSession = mock(IdPSession.class);
        when(mockSession.getId()).thenReturn(sessionId);
        when(mockSession.getPrincipalName()).thenReturn(TEST_PRINCIPAL_NAME);
        try {
            when(mockSession.checkTimeout()).thenReturn(expiredFlag);
        } catch (SessionException e) {
            throw new RuntimeException("Session exception", e);
        }
        return mockSession;
    }

    protected static RequestContext createTicketContext(final Ticket ticket) {
        final RequestContext requestContext = createProfileContext();
        final ProfileRequestContext profileRequestContext =
                (ProfileRequestContext) requestContext.getConversationScope().get(ProfileRequestContext.BINDING_KEY);
        profileRequestContext.addSubcontext(new TicketContext(ticket));
        return requestContext;
    }
}
