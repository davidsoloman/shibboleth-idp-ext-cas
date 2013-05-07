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

import com.google.common.base.Function;
import net.shibboleth.idp.cas.CasServiceAccessMessage;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.IdPSessionContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Determines whether authentication is required by examining both SSO session state and CAS
 * service access request message.
 *
 * @author Marvin S. Addison
 */
public class CheckAuthenticationRequiredAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(CheckAuthenticationRequiredAction.class);

    /** Strategy used to look up the current IdP session context if one exists. */
    private Function<ProfileRequestContext, IdPSessionContext> sessionCtxLookupStrategy =
            new ChildContextLookup<ProfileRequestContext, IdPSessionContext>(IdPSessionContext.class, false);

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final IdPSessionContext sessionCtx = sessionCtxLookupStrategy.apply(profileRequestContext);
        if (sessionCtx == null) {
            log.debug("No session currently exists");
            ActionSupport.buildEvent(profileRequestContext, Events.SessionNotFound.id());
            return;
        }
        final IdPSession session = sessionCtx.getIdPSession();
        log.debug("Found session ID {}", session.getId());

        final MessageContext<CasServiceAccessMessage> messageContext = profileRequestContext.getInboundMessageContext();
        if (messageContext.getMessage().isRenew()) {
            log.debug("CAS service access message has renew flag set. Forced authentication required.");
            ActionSupport.buildEvent(profileRequestContext, Events.RenewRequested.id());
        } else {
            ActionSupport.buildEvent(profileRequestContext, Events.SessionFound.id());
        }
    }
}
