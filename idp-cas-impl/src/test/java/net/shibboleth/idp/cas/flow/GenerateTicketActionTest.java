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

import net.shibboleth.idp.cas.CasServiceAccessMessage;
import net.shibboleth.idp.cas.Protocol;
import net.shibboleth.idp.cas.ticket.SimpleTicketStore;
import net.shibboleth.idp.profile.impl.WebFlowProfileActionAdaptor;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.EventContext;
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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Unit test for {@link GenerateTicketAction}.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration("/META-INF/flows/service-access-beans.xml")
public class GenerateTicketActionTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("generateTicket")
    private WebFlowProfileActionAdaptor generateTicketAction;

    @Autowired
    private SimpleTicketStore simpleTicketStore;


    @DataProvider(name = "messages")
    public Object[][] provideMessages() {
        return new Object[][] {
                { new CasServiceAccessMessage(Protocol.Cas20, "https://www.example.com/alpha") },
        };
    }

    @Test(dataProvider = "messages")
    public void testExecute(final CasServiceAccessMessage message) throws Exception {
        final Event result = generateTicketAction.execute(newTestRequestContext(message));
        assertEquals(Events.TicketCreated.id(), result.getId());
        final String ticket = message.getTicket();
        assertNotNull(ticket);
        assertEquals(ticket, simpleTicketStore.get(ticket).getId());
    }

    private static RequestContext newTestRequestContext(final CasServiceAccessMessage message) {
        final ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(message);
        profileRequestContext.setInboundMessageContext(messageContext);
        final MockRequestContext requestContext = new MockRequestContext();
        requestContext.getConversationScope().put(ProfileRequestContext.BINDING_KEY, profileRequestContext);
        final MockExternalContext externalContext = new MockExternalContext();
        externalContext.setNativeRequest(new MockHttpServletRequest());
        externalContext.setNativeResponse(new MockHttpServletResponse());
        requestContext.setExternalContext(externalContext);
        return requestContext;
    }
}
