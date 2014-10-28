/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionResolver;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class ValidateIdpSessionActionTest extends AbstractProfileActionTest {

    private static final String TEST_SERVICE = "https://example.com/widget";

    private ValidateIdpSessionAction action;

    @Autowired
    private TicketService ticketService;

    @BeforeTest
    public void setUp() throws Exception {
        final SessionResolver mockSessionResolver = mock(SessionResolver.class);
        when(mockSessionResolver.resolveSingle(any(CriteriaSet.class))).thenThrow(new ResolverException("Broken"));
    }

    @Test
    public void testSuccess() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action = new ValidateIdpSessionAction(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), Events.Success.id());
    }

    @Test
    public void testSessionExpired() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action = new ValidateIdpSessionAction(mockResolver(createSession(TEST_SESSION_ID, false)));
        assertEquals(action.execute(context).getId(), ProtocolError.SessionExpired.id());
    }

    @Test
    public void testSessionRetrievalError() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        final SessionResolver throwingSessionResolver = mock(SessionResolver.class);
        when(throwingSessionResolver.resolveSingle(any(CriteriaSet.class))).thenThrow(new ResolverException("Broken"));
        action = new ValidateIdpSessionAction(throwingSessionResolver);
    }

    private SessionResolver mockResolver(final IdPSession session) {
        final SessionResolver mockSessionResolver = mock(SessionResolver.class);
        try {
            when(mockSessionResolver.resolveSingle(any(CriteriaSet.class))).thenReturn(session);
        } catch (ResolverException e) {
            throw new RuntimeException("Resolver error", e);
        }
        return mockSessionResolver;
    }
}