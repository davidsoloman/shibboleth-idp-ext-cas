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

/**
 * Immutable service access ticket. Tickets are considered valid from the time of creation until the
 * expiration instant indicated on the ticket.
 *
 * @author Marvin S. Addison
 */
public class Ticket {
    /** Ticket identifier. */
    @Nonnull private final String id;

    /** Expiration instant. */
    @Nonnull private final Instant expirationInstant;

    /**
     * Creates a new ticket with an identifier and expiration date.
     *
     * @param id Ticket ID.
     * @param expiration Expiration instant.
     */
    public Ticket(@Nonnull final String id, @Nonnull final Instant expiration) {
        this.id = id;
        this.expirationInstant = expiration;
    }

    public String getId() {
        return id;
    }

    public Instant getExpirationInstant() {
        return expirationInstant;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Ticket)) {
            return false;
        }
        return ((Ticket ) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return 23 + 31 * id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
