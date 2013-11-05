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
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Generates and stores a CAS protocol ticket. Possible outcomes are are {@link Events#Success success} and
 * {@link Events#Failure failure}. In the success case a {@link ServiceTicketResponse} message is created and stored
 * as request scope parameter under the key {@value FlowStateSupport#SERVICE_TICKET_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class GrantServiceTicketAction extends AbstractProfileAction<ServiceTicketRequest, ServiceTicketRequest> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GrantServiceTicketAction.class);

    /** Manages CAS tickets. */
    @Nonnull private TicketService ticketService;


    public void setTicketService(@Nonnull final TicketService ticketService) {
        this.ticketService = Constraint.isNotNull(ticketService, "Ticket service cannot be null.");
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ServiceTicketRequest, ServiceTicketRequest> profileRequestContext)
            throws ProfileException {

        final ServiceTicketRequest request = FlowStateSupport.getServiceTicketRequest(springRequestContext);
        final SessionContext sessionCtx = profileRequestContext.getSubcontext(SessionContext.class, false);
        if (sessionCtx == null || sessionCtx.getIdPSession() == null) {
            throw new IllegalStateException("Cannot locate IdP session");
        }
        final ServiceTicket ticket;
        try {
            log.debug("Creating ticket for ", request.getService());
            ticket = ticketService.createServiceTicket(
                    sessionCtx.getIdPSession().getId(), request.getService(), request.isRenew());
            FlowStateSupport.setServiceTicketResponse(
                    springRequestContext, new ServiceTicketResponse(request.getService(), ticket.getId()));
            return new Event(this, Events.Success.id());
        } catch (RuntimeException e) {
            log.error("Failed granting service ticket due to error.", e);
            return new Event(this, Events.Failure.id());
        }
    }
}
