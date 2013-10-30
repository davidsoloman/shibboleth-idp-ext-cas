package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.context.SessionContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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
@ContextConfiguration({
        "/conf/global-beans.xml",
        "/conf/test-beans.xml",
        "/flows/cas-protocol-beans.xml"
})
public abstract class AbstractProfileActionTest<T extends AbstractProfileAction>
        extends AbstractTestNGSpringContextTests {

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

    protected static RequestContext createSessionContext(final String sessionId) {
        return createSessionContext(sessionId, true);
    }

    protected static RequestContext createSessionContext(final String sessionId, final boolean expiredFlag) {
        final RequestContext requestContext = createProfileContext();
        final ProfileRequestContext profileRequestContext =
                (ProfileRequestContext) requestContext.getConversationScope().get(ProfileRequestContext.BINDING_KEY);
        final IdPSession mockSession = mock(IdPSession.class);
        when(mockSession.getId()).thenReturn(sessionId);
        when(mockSession.getPrincipalName()).thenReturn(TEST_PRINCIPAL_NAME);
        try {
            when(mockSession.checkTimeout()).thenReturn(expiredFlag);
        } catch (SessionException e) {
            throw new RuntimeException("Session exception", e);
        }
        final SessionContext sessionContext = new SessionContext();
        sessionContext.setIdPSession(mockSession);
        profileRequestContext.addSubcontext(sessionContext);
        return requestContext;
    }
}
