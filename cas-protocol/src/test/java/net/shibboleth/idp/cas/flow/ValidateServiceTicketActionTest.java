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

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit test for {@link ValidateServiceTicketAction} class.
 *
 * @author Marvin S. Addison
 */
public class ValidateServiceTicketActionTest extends AbstractProfileActionTest<ValidateServiceTicketAction> {

    private static final String TEST_SESSION_ID = "+TkSGIRofZyue/p8F4M7TA==";

    private static final String TEST_SERVICE = "https://example.com/widget";

    @Autowired
    private ValidateServiceTicketAction action;

    @Autowired
    private TicketService ticketService;

    @BeforeTest
    public void setUp() throws Exception {
        final SessionResolver mockSessionResolver = mock(SessionResolver.class);
        when(mockSessionResolver.resolveSingle(any(CriteriaSet.class))).thenThrow(new ResolverException("Broken"));
    }

    @Test
    public void testServiceMismatch() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest("mismatch", ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), ProtocolError.ServiceMismatch.id());
    }

    @Test
    public void testSessionExpired() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, false)));
        assertEquals(action.execute(context).getId(), ProtocolError.SessionExpired.id());
    }

    @Test
    public void testSessionRetrievalError() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        final SessionResolver throwingSessionResolver = mock(SessionResolver.class);
        when(throwingSessionResolver.resolveSingle(any(CriteriaSet.class))).thenThrow(new ResolverException("Broken"));
        action.setSessionResolver(throwingSessionResolver);
        assertEquals(action.execute(context).getId(), ProtocolError.SessionRetrievalError.id());
    }

    @Test
    public void testTicketExpired() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        // Remove the ticket prior to validation to simulate expiration
        ticketService.removeServiceTicket(ticket.getId());
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), ProtocolError.TicketExpired.id());
    }

    @Test
    public void testTicketNotFromRenew() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), ProtocolError.TicketNotFromRenew.id());
    }

    @Test
    public void testTicketRetrievalError() throws Exception {
        final RequestContext context = createProfileContext();
        final TicketService throwingTicketService = mock(TicketService.class);
        when(throwingTicketService.removeServiceTicket(any(String.class))).thenThrow(new RuntimeException("Broken"));
        action.setTicketService(throwingTicketService);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, "ST-12345");
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), ProtocolError.TicketRetrievalError.id());
    }

    @Test
    public void testSuccess() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), Events.Success.id());
        assertNotNull(FlowStateSupport.getServiceTicketValidationResponse(context));
        assertEquals(FlowStateSupport.getServiceTicketValidationResponse(context).getUsername(), TEST_PRINCIPAL_NAME);
    }

    @Test
    public void testSuccessWithRenew() throws Exception {
        final RequestContext context = createProfileContext();
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        request.setRenew(true);
        FlowStateSupport.setTicketValidationRequest(context, request);
        action.setSessionResolver(mockResolver(createSession(TEST_SESSION_ID, true)));
        assertEquals(action.execute(context).getId(), Events.Success.id());
        assertNotNull(FlowStateSupport.getServiceTicketValidationResponse(context));
        assertEquals(FlowStateSupport.getServiceTicketValidationResponse(context).getUsername(), TEST_PRINCIPAL_NAME);
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
