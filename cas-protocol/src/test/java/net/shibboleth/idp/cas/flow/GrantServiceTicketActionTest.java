/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.context.SessionContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for {@link GrantServiceTicketAction}.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration({
        "/conf/global-beans.xml",
        "/conf/test-beans.xml",
        "/flows/cas-protocol-beans.xml"
})
public class GrantServiceTicketActionTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("grantServiceTicketAction")
    private GrantServiceTicketAction grantServiceTicketAction;

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
        final RequestContext context = newTestRequestContext(message);
        final Event result = grantServiceTicketAction.execute(context);
        assertEquals(result.getId(), Events.Success.id());
        final ServiceTicketResponse response = FlowStateSupport.getServiceTicketResponse(context);
        final ServiceTicket ticket = ticketService.removeServiceTicket(response.getTicket());
        assertNotNull(ticket);
        assertEquals(ticket.isRenew(), message.isRenew());
        assertEquals(ticket.getId(), response.getTicket());
        assertEquals(ticket.getService(), response.getService());
    }

    private static RequestContext newTestRequestContext(final ServiceTicketRequest message) {
        final MockRequestContext requestContext = new MockRequestContext();
        final MockExternalContext externalContext = new MockExternalContext();
        externalContext.setNativeRequest(new MockHttpServletRequest());
        externalContext.setNativeResponse(new MockHttpServletResponse());
        requestContext.setExternalContext(externalContext);
        final ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        requestContext.getConversationScope().put(ProfileRequestContext.BINDING_KEY, profileRequestContext);
        final IdPSession mockSession = mock(IdPSession.class);
        when(mockSession.getId()).thenReturn("ABC1234567890");
        final SessionContext sessionContext = new SessionContext();
        sessionContext.setIdPSession(mockSession);
        profileRequestContext.addSubcontext(sessionContext);
        FlowStateSupport.setServiceTicketRequest(requestContext, message);
        return requestContext;
    }
}
