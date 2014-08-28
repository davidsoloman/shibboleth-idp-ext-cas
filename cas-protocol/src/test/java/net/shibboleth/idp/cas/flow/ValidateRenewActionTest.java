/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.idp.cas.ticket.ProxyTicket;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link net.shibboleth.idp.cas.flow.ValidateRenewAction}.
 *
 * @author Marvin S. Addison
 */
public class ValidateRenewActionTest extends AbstractProfileActionTest {

    private static final String TEST_SERVICE = "https://example.com/widget";

    @Autowired
    private ValidateRenewAction action;

    @Autowired
    private TicketService ticketService;

    @Test
    public void testTicketNotFromRenew() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), ProtocolError.TicketNotFromRenew.id());
    }

    @Test
    public void testRenewIncompatibleWithProxy() throws Exception {
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final ProxyGrantingTicket pgt = ticketService.createProxyGrantingTicket(st, "PGT-12345");
        final ProxyTicket pt = ticketService.createProxyTicket(pgt, "https://foo.example.org");
        final RequestContext context = createTicketContext(pt);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, pt.getId());
        request.setRenew(true);
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), ProtocolError.RenewIncompatibleWithProxy.id());
    }

    @Test
    public void testSuccessWithRenewAndServiceTicket() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        request.setRenew(true);
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.Success.id());
    }

    @Test
    public void testSuccessWithoutRenewAndProxyTicket() throws Exception {
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final ProxyGrantingTicket pgt = ticketService.createProxyGrantingTicket(st, "PGT-98765");
        final ProxyTicket pt = ticketService.createProxyTicket(pgt, "https://foo.example.org");
        final RequestContext context = createTicketContext(pt);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, pt.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.Success.id());
    }
}