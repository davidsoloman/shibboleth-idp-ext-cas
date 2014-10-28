/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProxyTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.service.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.test.MockRequestContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BuildRelyingPartyContextActionTest extends AbstractProfileActionTest {

    @Autowired
    private BuildRelyingPartyContextAction action;

    @Test
    public void testExecuteFromServiceTicketRequest() {
        final String serviceURL = "https://serviceA.example.org:8443/landing";
        MockRequestContext requestContext = (MockRequestContext) createProfileContext();
        FlowStateSupport.setServiceTicketRequest(requestContext, new ServiceTicketRequest(serviceURL));
        action.execute(requestContext);
        final ServiceContext sc = getProfileContext(requestContext).getSubcontext(ServiceContext.class);
        assertNotNull(sc);
        assertNotNull(sc.getService());
        assertEquals(serviceURL, sc.getService().getName());
        assertEquals("allowedToProxy", sc.getService().getGroup());
        assertTrue(sc.getService().isAuthorizedToProxy());
    }

    @Test
    public void testExecuteFromTicketValidationRequest() {
        final String serviceURL = "http://serviceB.example.org/";
        MockRequestContext requestContext = (MockRequestContext) createProfileContext();
        FlowStateSupport.setTicketValidationRequest(requestContext, new TicketValidationRequest(serviceURL, "ST-123"));
        action.execute(requestContext);
        final ServiceContext sc = getProfileContext(requestContext).getSubcontext(ServiceContext.class);
        assertNotNull(sc);
        assertNotNull(sc.getService());
        assertEquals(serviceURL, sc.getService().getName());
        assertEquals("notAllowedToProxy", sc.getService().getGroup());
        assertFalse(sc.getService().isAuthorizedToProxy());
    }

    @Test
    public void testExecuteFromProxyTicketRequest() {
        final String serviceURL = "http://mallory.untrusted.org/";
        MockRequestContext requestContext = (MockRequestContext) createProfileContext();
        FlowStateSupport.setProxyTicketRequest(requestContext, new ProxyTicketRequest("PGT-123", serviceURL));
        action.execute(requestContext);
        final ServiceContext sc = getProfileContext(requestContext).getSubcontext(ServiceContext.class);
        assertNotNull(sc);
        assertNotNull(sc.getService());
        assertEquals(serviceURL, sc.getService().getName());
        assertEquals(BuildRelyingPartyContextAction.UNVERIFIED_GROUP, sc.getService().getGroup());
        assertFalse(sc.getService().isAuthorizedToProxy());
    }
}