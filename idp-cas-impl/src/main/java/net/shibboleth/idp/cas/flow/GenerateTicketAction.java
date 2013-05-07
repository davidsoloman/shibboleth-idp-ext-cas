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

import net.shibboleth.idp.cas.CasServiceAccessMessage;
import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketFactory;
import net.shibboleth.idp.persistence.PersistenceManager;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates and stores a CAS protocol ticket.
 *
 * @author Marvin S. Addison
 */
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
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final MessageContext<CasServiceAccessMessage> messageContext = profileRequestContext.getInboundMessageContext();
        final Ticket ticket;
        try {
            ticket = ticketFactory.createTicket();
            log.debug("Persisting ticket {}", ticket);
            ticketStore.persist(ticket.getId(), ticket);
            messageContext.getMessage().setTicket(ticket.getId());
        } catch (RuntimeException e) {
            ActionSupport.buildEvent(profileRequestContext, Events.TicketCreationFailed.id());
        }
        ActionSupport.buildEvent(profileRequestContext, Events.TicketCreated.id());
    }
}
