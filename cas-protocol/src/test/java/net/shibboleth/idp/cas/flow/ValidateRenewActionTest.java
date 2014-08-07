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
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final ProxyGrantingTicket pgt = ticketService.createProxyGrantingTicket(st, "PGT-12345");
        final ProxyTicket pt = ticketService.createProxyTicket(pgt, "https://foo.example.org");
        final RequestContext context = createTicketContext(pt);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, pt.getId());
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), ProtocolError.RenewIncompatibleWithProxy.id());
    }

    @Test
    public void testSuccess() throws Exception {
        final ServiceTicket ticket = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, true);
        final RequestContext context = createTicketContext(ticket);
        final TicketValidationRequest request = new TicketValidationRequest(TEST_SERVICE, ticket.getId());
        request.setRenew(true);
        FlowStateSupport.setTicketValidationRequest(context, request);
        assertEquals(action.execute(context).getId(), Events.Success.id());
    }

}