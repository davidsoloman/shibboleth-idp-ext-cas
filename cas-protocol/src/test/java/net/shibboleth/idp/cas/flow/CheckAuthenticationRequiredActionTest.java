package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link CheckAuthenticationRequiredAction} class.
 *
 * @author Marvin S. Addison
 */
public class CheckAuthenticationRequiredActionTest
        extends AbstractProfileActionTest<CheckAuthenticationRequiredAction> {

    @Autowired
    private CheckAuthenticationRequiredAction action;

    @Test
    public void testGatewayRequested() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicketRequest request = new ServiceTicketRequest("a");
        request.setGateway(true);
        FlowStateSupport.setServiceTicketRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.GatewayRequested.id());
    }

    @Test
    public void testSessionNotFound() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicketRequest request = new ServiceTicketRequest("b");
        FlowStateSupport.setServiceTicketRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.SessionNotFound.id());
    }

    @Test
    public void testSessionExpired() throws Exception {
        final RequestContext context = createSessionContext("ABCDE", false);
        final ServiceTicketRequest request = new ServiceTicketRequest("b");
        FlowStateSupport.setServiceTicketRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.SessionNotFound.id());
    }

    @Test
    public void testSessionFound() throws Exception {
        final RequestContext context = createSessionContext("12345");
        final ServiceTicketRequest request = new ServiceTicketRequest("c");
        FlowStateSupport.setServiceTicketRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.SessionFound.id());
    }

    @Test
    public void testRenewRequested() throws Exception {
        final RequestContext context = createSessionContext("98765");
        final ServiceTicketRequest request = new ServiceTicketRequest("d");
        request.setRenew(true);
        FlowStateSupport.setServiceTicketRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.RenewRequested.id());
    }
}
