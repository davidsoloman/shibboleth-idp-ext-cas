/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for {@link ValidateTicketAction} class.
 *
 * @author Marvin S. Addison
 */
public class ValidateTicketActionTest extends AbstractProfileActionTest {

    private static final String TEST_SERVICE = "https://example.com/widget";

    @Autowired
    private TicketService ticketService;


    @Test
    public void testInvalidTicketFormat() throws Exception {
        final RequestContext context = createProfileContext();
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, "AB-1234-012346abcdef");
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(newAction(ticketService).execute(context).getId(), ProtocolError.InvalidTicketFormat.id());
    }

    @Test
    public void testServiceMismatch() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest("mismatch", ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(newAction(ticketService).execute(context).getId(), ProtocolError.ServiceMismatch.id());
    }

    @Test
    public void testTicketExpired() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        // Remove the ticket prior to validation to simulate expiration
        ticketService.removeServiceTicket(ticket.getId());
        assertEquals(newAction(ticketService).execute(context).getId(), ProtocolError.TicketExpired.id());
    }

    @Test
    public void testTicketRetrievalError() throws Exception {
        final RequestContext context = createProfileContext();
        final TicketService throwingTicketService = mock(TicketService.class);
        when(throwingTicketService.removeServiceTicket(any(String.class))).thenThrow(new RuntimeException("Broken"));
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, "ST-12345");
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(
                newAction(throwingTicketService).execute(context).getId(),
                ProtocolError.TicketRetrievalError.id());
    }

    @Test
    public void testSuccess() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(newAction(ticketService).execute(context).getId(), Events.ServiceTicketValidated.id());
        assertNotNull(FlowStateSupport.getTicketValidationResponse(context));
    }

    private static ValidateTicketAction newAction(final TicketService service) {
        final ValidateTicketAction action = new ValidateTicketAction(service);
        try {
            action.initialize();
        } catch (ComponentInitializationException e) {
            throw new RuntimeException("Initialization error", e);
        }
        return action;
    }
}
