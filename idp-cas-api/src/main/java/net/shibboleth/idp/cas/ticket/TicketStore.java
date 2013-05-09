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
import javax.annotation.Nullable;

/**
 * Ticket storage service.
 *
 * @author Marvin S. Addison
 */
public interface TicketStore {

    /**
     * Stores the given ticket.
     *
     * @param ticket Ticket to store.
     */
    void add(@Nonnull Ticket ticket);


    /**
     * Fetches the ticket with the given ID from the store.
     *
     * @param id ID of ticket to fetch.
     *
     * @return Ticket or null if not found.
     */
    @Nullable Ticket get(@Nonnull String id);


    /**
     * Removes the ticket with the given ID.
     *
     * @param id ID of ticket to remove.
     */
    void remove(@Nonnull String id);


    /**
     * Removes all expired tickets from the store.
     *
     * @return Number of expired tickets removed or a negative number
     * if removal counts are not supported.
     */
    int expunge();
}
