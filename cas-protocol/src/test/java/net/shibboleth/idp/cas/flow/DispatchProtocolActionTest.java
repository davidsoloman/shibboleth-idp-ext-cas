package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolUri;
import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link DispatchProtocolAction}.
 *
 * @author Marvin S. Addison
 */
public class DispatchProtocolActionTest {

    private final DispatchProtocolAction dispatchProtocolAction = new DispatchProtocolAction();

    @DataProvider(name = "requests")
    public Object[][] provideRequests() {
        return new Object[][] {
                { "/idp/cas/login", "login" },
                { "/idp/cas/serviceValidate", "serviceValidate" },
                { "/idp/cas/validate", "validate" },
                { "/idp/cas/proxy", "proxy" },
                { "/idp/cas/proxyValidate", "proxyValidate" },
                { "/idp/cas/unknown", "unknownProtocolUri" },
                { "/idp/cas/login/unknown", "unknownProtocolUri" },
        };
    }

    @Test(dataProvider = "requests")
    public void testExecute(final String uri, final String expectedEventId) throws Exception {
        final MockRequestContext requestContext = new MockRequestContext();
        final MockExternalContext externalContext = new MockExternalContext();
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI(uri);
        externalContext.setNativeRequest(mockRequest);
        externalContext.setNativeResponse(new MockHttpServletResponse());
        requestContext.setExternalContext(externalContext);

        final Event result = dispatchProtocolAction.execute(requestContext);
        Assert.assertEquals(result.getId(), expectedEventId);
    }
}
