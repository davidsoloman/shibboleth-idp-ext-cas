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
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.ext.spring.webflow.Event;
import net.shibboleth.ext.spring.webflow.Events;
import net.shibboleth.idp.cas.CasServiceAccessMessage;
import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketFactory;
import net.shibboleth.idp.persistence.PersistenceManager;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.profile.ProfileException;
import net.shibboleth.idp.profile.ProfileRequestContext;
import org.opensaml.messaging.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;

/**
 * Generates and stores a CAS protocol ticket.
 *
 * @author Marvin S. Addison
 */
@Events({
        @Event(
                id = "ticketGenerated",
                description = "Ticket successfully generated.",
                attributes = "ticket"),
        @Event(
                id = "ticketGenerationFailed",
                description = "Ticket creation or storage failed.",
                attributes = "error")
})
public class GenerateTicketAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GenerateTicketAction.class);

    /** Creates CAS tickets. */
    @Nonnull private TicketFactory ticketFactory;

    /** Ticket store. */
    @Nonnull private PersistenceManager<Ticket> ticketStore;

    public void setTicketFactory(@Nonnull final TicketFactory ticketFactory) {
        this.ticketFactory = ticketFactory;
    }

    public void setTicketStore(@Nonnull final PersistenceManager<Ticket> ticketStore) {
        this.ticketStore = ticketStore;
    }

    /** {@inheritDoc} */
    @Override
    protected org.springframework.webflow.execution.Event doExecute(
            @Nullable final HttpServletRequest httpRequest,
            @Nullable final HttpServletResponse httpResponse,
            @Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final MessageContext<CasServiceAccessMessage> messageContext = profileRequestContext.getInboundMessageContext();
        final Ticket ticket;
        try {
            ticket = ticketFactory.createTicket();
            log.debug("Persisting ticket {}", ticket);
            ticketStore.persist(ticket.getId(), ticket);
            messageContext.getMessage().setTicket(ticket.getId());
        } catch (RuntimeException e) {
            return ActionSupport.buildEvent(this, "ticketGenerationFailed", new LocalAttributeMap("error", e));
        }
        return ActionSupport.buildEvent(this, "ticketGenerated", new LocalAttributeMap("ticket", ticket));
    }
}
