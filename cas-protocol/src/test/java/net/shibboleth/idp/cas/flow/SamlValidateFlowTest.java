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

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Tests the flow behind the <code>/samlValidate</code> endpoint.
 *
 * @author Marvin S. Addison
 */
public class SamlValidateFlowTest extends AbstractFlowTest {

    /** Flow id. */
    @Nonnull private static String FLOW_ID = "cas/samlValidate";

    private static final String SAML_REQUEST_TEMPLATE =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<SOAP-ENV:Header/><SOAP-ENV:Body>" +
            "<samlp:Request xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\" MajorVersion=\"1\" " +
                    "MinorVersion=\"1\" RequestID=\"_192.168.16.51.1024506224022\" " +
                    "IssueInstant=\"2002-06-19T17:03:44.022Z\">" +
            "<samlp:AssertionArtifact>@@TICKET@@</samlp:AssertionArtifact>" +
            "</samlp:Request></SOAP-ENV:Body></SOAP-ENV:Envelope>";

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SessionManager sessionManager;

    @Test
    public void testSuccess() throws Exception {
        final String principal = "john";
        final String service = "https://test.example.org/";
        final IdPSession session = sessionManager.createSession(principal);
        final ServiceTicket ticket = ticketService.createServiceTicket(session.getId(), service, false);
        final String requestBody = SAML_REQUEST_TEMPLATE.replace("@@TICKET@@", ticket.getId());
        request.setMethod("POST");
        request.setContent(requestBody.getBytes("UTF-8"));
        externalContext.getMockRequestParameterMap().put("TARGET", service);

        final FlowExecutionResult result = flowExecutor.launchExecution(FLOW_ID, null, externalContext);

        assertEquals(result.getOutcome().getId(), "serviceValidateSuccess");
        final String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains("<saml1p:StatusCode Value=\"saml1p:Success\"/>"));
        assertTrue(responseBody.contains("<saml1:NameIdentifier>john</saml1:NameIdentifier>"));
    }


    @Test
    public void testFailureTicketExpired() throws Exception {
        final String principal = "john";
        final String service = "https://test.example.org/";
        sessionManager.createSession(principal);
        final String requestBody = SAML_REQUEST_TEMPLATE.replace("@@TICKET@@", "ST-123-abcdefg");
        request.setMethod("POST");
        request.setContent(requestBody.getBytes("UTF-8"));
        externalContext.getMockRequestParameterMap().put("TARGET", service);

        final FlowExecutionResult result = flowExecutor.launchExecution(FLOW_ID, null, externalContext);

        assertEquals(result.getOutcome().getId(), "serviceValidateFailure");
        final String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains("<saml1p:StatusCode Value=\"INVALID_TICKET\""));
        assertTrue(responseBody.contains("<saml1p:StatusMessage>E_TICKET_EXPIRED</saml1p:StatusMessage>"));
    }


    @Test
    public void testFailureSessionExpired() throws Exception {
        final String service = "https://test.example.org/";
        final ServiceTicket ticket = ticketService.createServiceTicket("A1B2C3D4E5F6", service, false);
        final String requestBody = SAML_REQUEST_TEMPLATE.replace("@@TICKET@@", ticket.getId());
        request.setMethod("POST");
        request.setContent(requestBody.getBytes("UTF-8"));
        externalContext.getMockRequestParameterMap().put("TARGET", service);

        final FlowExecutionResult result = flowExecutor.launchExecution(FLOW_ID, null, externalContext);

        assertEquals(result.getOutcome().getId(), "serviceValidateFailure");
        final String responseBody = response.getContentAsString();
        assertTrue(responseBody.contains("<saml1p:StatusCode Value=\"INVALID_TICKET\""));
        assertTrue(responseBody.contains("<saml1p:StatusMessage>E_SESSION_EXPIRED</saml1p:StatusMessage>"));
    }
}
