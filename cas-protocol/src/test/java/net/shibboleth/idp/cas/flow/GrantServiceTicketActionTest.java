/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for {@link GrantServiceTicketAction}.
 *
 * @author Marvin S. Addison
 */
public class GrantServiceTicketActionTest extends AbstractProfileActionTest {

    @Autowired
    private GrantServiceTicketAction action;

    @Autowired
    private TicketService ticketService;

    @DataProvider(name = "messages")
    public Object[][] provideMessages() {
        final ServiceTicketRequest renewedRequest = new ServiceTicketRequest("https://www.example.com/beta");
        renewedRequest.setRenew(true);
        return new Object[][] {
                { new ServiceTicketRequest("https://www.example.com/alpha") },
                { renewedRequest },
        };
    }

    @Test(dataProvider = "messages")
    public void testExecute(final ServiceTicketRequest message) throws Exception {
        final RequestContext context = createSessionContext("1234567890");
        FlowStateSupport.setServiceTicketRequest(context, message);
        final Event result = action.execute(context);
        assertEquals(result.getId(), Events.Success.id());
        final ServiceTicketResponse response = FlowStateSupport.getServiceTicketResponse(context);
        final ServiceTicket ticket = ticketService.removeServiceTicket(response.getTicket());
        assertNotNull(ticket);
        assertEquals(ticket.isRenew(), message.isRenew());
        assertEquals(ticket.getId(), response.getTicket());
        assertEquals(ticket.getService(), response.getService());
    }
}
