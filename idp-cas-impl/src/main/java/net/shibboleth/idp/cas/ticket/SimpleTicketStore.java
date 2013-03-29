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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.shibboleth.idp.persistence.PersistenceManager;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;

/**
 * Stores CAS tickets in a concurrent Map.
 *
 * @author Marvin S. Addison
 */
public class SimpleTicketStore implements PersistenceManager<Ticket> {

    /** Map that stores tickets. */
    private final ConcurrentMap<String, Ticket> backingMap = new ConcurrentHashMap<String, Ticket>();

    /**
     * Determines whether a ticket with the given ID exists in the store.
     *
     * @param s ID of ticket to check.
     *
     * @return True if ticket exists, false otherwise.
     */
    public boolean contains(final String s) {
        return backingMap.containsKey(s);
    }

    /**
     * Determines whether the ticket exists in the store.
     *
     * @param ticket Ticket to check.
     *
     * @return True if ticket exists, false otherwise.
     */
    public boolean contains(final Ticket ticket) {
        return backingMap.containsKey(ticket.getId());
    }

    /**
     * Gets the ticket with the given ticket ID.
     *
     * @param s ID of ticket to get.
     *
     * @return Ticket with matching ID or null if not found.
     */
    public Ticket get(String s) {
        return backingMap.get(s);
    }

    /**
     * Persists a ticket according to the persistence manager contract, but ignores the key.
     * Since tickets are self-identifiying, they are stored using the {@link Ticket#id} as a key.
     *
     * @param s Ignored.
     * @param ticket Ticket to store.
     *
     * @return Stored ticket.
     */
    public Ticket persist(final String s, final Ticket ticket) {
        backingMap.put(ticket.getId(), ticket);
        return ticket;
    }

    /**
     * Removes the ticket with the given ticket ID.
     *
     * @param s ID of ticket to remove.
     *
     * @return Removed ticket or null if none removed.
     */
    public Ticket remove(final String s) {
        return backingMap.remove(s);
    }

    /**
     * Removes the given ticket.
     *
     * @param ticket Ticket to remove.
     *
     * @return Removed ticket or null if none removed.
     */
    public Ticket remove(Ticket ticket) {
        return backingMap.remove(ticket.getId());
    }

    /**
     * Gets the unqiue identifier for this ticket store.
     *
     * @return {@link Object#toString()}
     */
    public String getId() {
        return toString();
    }

    /** No-op. Nothing to validate. */
    public void validate() throws ComponentValidationException {}
}
