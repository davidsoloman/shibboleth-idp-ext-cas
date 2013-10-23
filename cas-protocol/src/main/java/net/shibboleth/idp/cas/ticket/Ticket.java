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
package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;

import org.joda.time.Instant;
import org.opensaml.storage.annotation.Context;
import org.opensaml.storage.annotation.Key;
import org.opensaml.storage.annotation.Value;
import org.opensaml.storage.annotation.Expiration;

/**
 * Immutable service access ticket. Tickets are considered valid from the time of creation until the
 * expiration instant indicated on the ticket.
 *
 * @author Marvin S. Addison
 */
@Context("CONTEXT")
@Key("id")
@Value("service")
@Expiration("expirationInstant")
public class Ticket {
    /** Storage service context name. */
    public static final String CONTEXT = "http://jasig.org/cas/tickets";

    /** Ticket identifier. */
    @Nonnull private String id;

    /** Service/relying party that requested the ticket. */
    @Nonnull private String service;

    /** Expiration instant. */
    @Nonnull private Instant expirationInstant;

    /** Creates a new instance with empty values. */
    Ticket() {}

    /**
     * Creates a new ticket with an identifier, service, and expiration date.
     *
     * @param id Ticket ID.
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     */
    public Ticket(@Nonnull final String id, @Nonnull final String service, @Nonnull final Instant expiration) {
        this.id = id;
        this.service = service;
        this.expirationInstant = expiration;
    }

    public String getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public Instant getExpirationInstant() {
        return expirationInstant;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Ticket)) {
            return false;
        }
        final Ticket other = (Ticket) o;
        return other.id.equals(id) && other.service.equals(service);
    }

    @Override
    public int hashCode() {
        return 23 + 31 * id.hashCode() + 35 * service.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}