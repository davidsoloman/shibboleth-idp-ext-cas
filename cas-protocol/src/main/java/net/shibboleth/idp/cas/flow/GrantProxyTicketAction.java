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

import net.shibboleth.idp.cas.protocol.*;
import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.idp.cas.ticket.ProxyTicket;
import net.shibboleth.idp.cas.ticket.TicketService;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Generates and stores a CAS protocol proxy ticket. Possible outcomes:
 * <ul>
 *     <li>{@link net.shibboleth.idp.cas.flow.Events#Success success}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketRetrievalError ticketRetrievalError}</li>
 *     <li>{@link net.shibboleth.idp.cas.protocol.ProtocolError#TicketCreationError ticketCreationError}</li>
 * </ul>
 * In the success case a {@link net.shibboleth.idp.cas.protocol.ProxyTicketResponse} message is created and stored
 * as request scope parameter under the key {@value net.shibboleth.idp.cas.flow.FlowStateSupport#PROXY_TICKET_RESPONSE_KEY}.
 *
 * @author Marvin S. Addison
 */
public class GrantProxyTicketAction extends AbstractProfileAction<ProxyTicketRequest, ProxyTicketResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GrantProxyTicketAction.class);

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
            final @Nonnull ProfileRequestContext<ProxyTicketRequest, ProxyTicketResponse> profileRequestContext) {

        final ProxyTicketRequest request = FlowStateSupport.getProxyTicketRequest(springRequestContext);
        final SessionContext sessionCtx = profileRequestContext.getSubcontext(SessionContext.class, false);
        if (sessionCtx == null || sessionCtx.getIdPSession() == null) {
            throw new IllegalStateException("Cannot locate IdP session");
        }
        final ProxyGrantingTicket pgt;
        try {
            log.debug("Fetching proxy-granting ticket {}", request.getPgt());
            pgt = ticketService.fetchProxyGrantingTicket(request.getPgt());
        } catch (RuntimeException e) {
            log.error("Failed looking up " + request.getPgt(), e);
            return ProtocolError.TicketRetrievalError.event(this);
        }
        final ProxyTicket pt;
        try {
            log.debug("Granting proxy ticket for {}", request.getTargetService());
            pt = ticketService.createProxyTicket(pgt, request.getTargetService());
        } catch (RuntimeException e) {
            log.error("Failed granting proxy ticket due to error.", e);
            return ProtocolError.TicketCreationError.event(this);
        }
        log.info("Granted proxy ticket for {}", request.getTargetService());
        FlowStateSupport.setProxyTicketResponse(springRequestContext, new ProxyTicketResponse(pt.getId()));
        return new Event(this, Events.Success.id());
    }
}
