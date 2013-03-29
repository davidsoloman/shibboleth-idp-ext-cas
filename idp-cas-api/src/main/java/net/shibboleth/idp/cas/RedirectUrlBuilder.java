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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Creates the URL for the service redirect portion of the CAS protocol.
 *
 * @author Marvin S. Addison
 */
public class RedirectUrlBuilder {

    /**
     * Builds the redirect URL used in the CAS protocol step that provides a granted ticket
     * to the requesting service, which happens by way of an HTTP 302 redirect.
     *
     * @param message CAS service access request message.
     *
     * @return Redirect URL containing granted service access ticket.
     */
    public String buildUrl(@Nonnull final CasServiceAccessMessage message) {
        final String ticket = StringSupport.trimOrNull(message.getTicket());
        if (ticket == null) {
            throw new IllegalStateException("Ticket cannot be null or empty.");
        }
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(message.getService());
        builder.queryParam(message.getProtocol().getTicketArtifactName(), ticket);
        return builder.build().toUriString();
    }
}
