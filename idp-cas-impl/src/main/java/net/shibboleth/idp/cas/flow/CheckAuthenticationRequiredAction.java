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

import com.google.common.base.Function;
import net.shibboleth.ext.spring.webflow.Event;
import net.shibboleth.ext.spring.webflow.Events;
import net.shibboleth.idp.cas.CasServiceAccessMessage;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.profile.ProfileException;
import net.shibboleth.idp.profile.ProfileRequestContext;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.IdPSessionContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;

/**
 * Determines whether authentication is required by examining both SSO session state and CAS
 * service access request message.
 *
 * @author Marvin S. Addison
 */
@Events({
        @Event(
                id = "sessionNotFound",
                description = "SSO session NOT found for current profile request context."),
        @Event(
                id = "sessionNotFound",
                description = "SSO session found for current profile request context.",
                attributes = "session"),
        @Event(
                id = "renewRequested",
                description = "CAS service access request specifies renew=true.",
                attributes = "session")
})
public class CheckAuthenticationRequiredAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(CheckAuthenticationRequiredAction.class);

    /** Strategy used to look up the current IdP session context if one exists. */
    private Function<ProfileRequestContext, IdPSessionContext> sessionCtxLookupStrategy =
            new ChildContextLookup<ProfileRequestContext, IdPSessionContext>(IdPSessionContext.class, false);

    /** {@inheritDoc} */
    @Override
    protected org.springframework.webflow.execution.Event doExecute(
            @Nullable final HttpServletRequest httpRequest,
            @Nullable final HttpServletResponse httpResponse,
            @Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final IdPSessionContext sessionCtx = sessionCtxLookupStrategy.apply(profileRequestContext);
        if (sessionCtx == null) {
            log.debug("No session currently exists");
            return ActionSupport.buildEvent(this, "noSession");
        }
        final IdPSession session = sessionCtx.getIdPSession();
        log.debug("Found session ID {}", session.getId());

        final MessageContext<CasServiceAccessMessage> messageContext = profileRequestContext.getInboundMessageContext();
        final String eventId;
        if (messageContext.getMessage().isRenew()) {
            log.debug("CAS service access message has renew flag set. Forced authentication required.");
            eventId = "forceAuthentication";
        } else {
            eventId = "hasSession";
        }
        return ActionSupport.buildEvent(this, eventId, new LocalAttributeMap("session", session));
    }
}
