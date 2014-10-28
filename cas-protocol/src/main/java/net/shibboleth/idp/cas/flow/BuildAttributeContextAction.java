/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.attribute.context.AttributeContext;
import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import net.shibboleth.idp.session.context.SessionContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Creates the following contexts needed for attribute resolution:
 * <ul>
 *     <li>{@link net.shibboleth.idp.attribute.context.AttributeContext} -
 *         Child of {@link RelyingPartyContext} will hold resolved attributes.</li>
 *     <li>{@link net.shibboleth.idp.authn.context.SubjectContext} -
 *         Contains IdP session principal name needed for attribute resolution.</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class BuildAttributeContextAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BuildAttributeContextAction.class);

    @Nonnull
    @Override
    protected Event doExecute(
        final @Nonnull RequestContext springRequestContext,
        final @Nonnull ProfileRequestContext<TicketValidationRequest, TicketValidationResponse> profileRequestContext) {

        final SessionContext sessionContext = profileRequestContext.getSubcontext(SessionContext.class);
        if (sessionContext == null || sessionContext.getIdPSession() == null) {
            log.info("Cannot locate IdP session");
            return ProtocolError.IllegalState.event(this);
        }
        final SubjectContext sc = new SubjectContext();
        sc.setPrincipalName(sessionContext.getIdPSession().getPrincipalName());
        profileRequestContext.addSubcontext(sc);
        return Events.Proceed.event(this);
    }
}
