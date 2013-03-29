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

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.idp.profile.HttpServletRequestMessageDecoderFactory;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;

/**
 * Description of CasServiceAccessMessageDecoderFactory.
 *
 * @author Marvin S. Addison
 */
public class CasServiceAccessMessageDecoderFactory implements HttpServletRequestMessageDecoderFactory {

    public MessageDecoder newDecoder(final HttpServletRequest httpRequest) throws MessageDecodingException {
        final Cas20MessageDecoder decoder = new Cas20MessageDecoder();
        decoder.setHttpServletRequest(httpRequest);
        return decoder;
    }
}
