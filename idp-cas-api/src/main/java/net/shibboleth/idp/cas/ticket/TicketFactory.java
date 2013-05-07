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

import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

/**
 * Creates tickets using potentially protocol-specific components.
 *
 * @author Marvin S. Addison
 */
public class TicketFactory {

    /** Creates identifiers for tickets created with this factory. */
    @Nonnull private TicketIdGenerator ticketIdGenerator;

    /** Validity time period of tickets created with this factory. */
    @Nonnull private ReadablePeriod ticketValidityPeriod;

    public void setTicketIdGenerator(@Nonnull final TicketIdGenerator generator) {
        this.ticketIdGenerator = generator;
    }

    public void setTicketValidityPeriod(@Nonnull final ReadablePeriod period) {
        this.ticketValidityPeriod = period;
    }

    /**
     * Creates a ticket for the requesting service.
     *
     * @param service Requester.
     *
     * @return Protocol-specific ticket.
     */
    public Ticket createTicket(final String service) {
        return new Ticket(
                ticketIdGenerator.generate(),
                service,
                DateTime.now().plus(ticketValidityPeriod).toInstant());
    }

}