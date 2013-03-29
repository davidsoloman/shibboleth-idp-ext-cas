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
package net.shibboleth.idp.cas;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.AbstractHttpServletRequestMessageDecoder;

/**
 * Extracts the service and ticket request parameters from the request and stores them in a
 * {@link Cas20MessageDecoder} object.
 *
 * @author Marvin S. Addison
 */
public class Cas20MessageDecoder extends AbstractHttpServletRequestMessageDecoder<CasServiceAccessMessage> {

    /** CAS 2.0 protocol. */
    private static final Protocol CAS20 = Protocol.Cas20;

    private MessageContext<CasServiceAccessMessage> messageContext;

    public MessageContext<CasServiceAccessMessage> getMessageContext() {
        return messageContext;
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final String service = StringSupport.trimOrNull(
                getHttpServletRequest().getParameter(CAS20.getServiceArtifactName()));
        if (service == null) {
            throw new MessageDecodingException("Required CAS 2.0 protocol parameter 'service' not found");
        }
        final CasServiceAccessMessage message = new CasServiceAccessMessage(CAS20, service);
        message.setTicket(getHttpServletRequest().getParameter(CAS20.getTicketArtifactName()));
        final String renew = getHttpServletRequest().getParameter(Protocol.RENEW_ARTIFACT_NAME);
        if (renew != null) {
            try {
                message.setRenew(Boolean.parseBoolean(renew));
            } catch (RuntimeException e) {
                throw new MessageDecodingException("Invalid renew parameter value " + renew);
            }
        }
        final String gateway = getHttpServletRequest().getParameter(Protocol.GATEWAY_ARTIFACT_NAME);
        if (gateway != null) {
            try {
                message.setGateway(Boolean.parseBoolean(gateway));
            } catch (RuntimeException e) {
                throw new MessageDecodingException("Invalid gateway parameter value " + gateway);
            }
        }
        this.messageContext = new MessageContext<CasServiceAccessMessage>();
        this.messageContext.setMessage(message);
    }
}
