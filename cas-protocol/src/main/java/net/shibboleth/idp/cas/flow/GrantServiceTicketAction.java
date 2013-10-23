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

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.idp.cas.ticket.Ticket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Generates and stores a CAS protocol ticket and generates a {@link ServiceTicketResponse} message stored
 * as request scope parameter under the key {@value FlowStateSupport#SERVICE_TICKET_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class GrantServiceTicketAction implements Action {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GrantServiceTicketAction.class);

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }

    @Override
    public Event execute(final RequestContext context) throws Exception {
        final ServiceTicketRequest request = FlowStateSupport.getServiceTicketRequest(context);
        final ServiceTicket ticket;
        try {
            log.debug("Creating ticket for ", request.getService());
            ticket = ticketService.createServiceTicket(request.getService(), request.isRenew());
            FlowStateSupport.setServiceTicketResponse(
                    context, new ServiceTicketResponse(request.getService(), ticket.getId()));
            return new Event(this, Events.TicketCreated.id());
        } catch (RuntimeException e) {
            return new Event(this, Events.TicketCreationFailed.id());
        }
    }
}
