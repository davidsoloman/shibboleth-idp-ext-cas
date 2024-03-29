/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.IdPAttributeValue;
import net.shibboleth.idp.attribute.context.AttributeContext;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * Extracts {@link IdPAttribute}s from a populated {@link AttributeContext} and places them in the
 * {@link TicketValidationResponse}.
 *
 * @author Marvin S. Addison
 */
public class ExtractAttributesAction
        extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ExtractAttributesAction.class);

    /** Function used to retrieve AttributeContext. */
    private Function<ProfileRequestContext,AttributeContext> attributeContextFunction =
            Functions.compose(
                    new ChildContextLookup<>(AttributeContext.class),
                    new ChildContextLookup<ProfileRequestContext, RelyingPartyContext>(RelyingPartyContext.class));


    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<TicketValidationRequest, TicketValidationResponse> profileRequestContext) {

        final AttributeContext ac = attributeContextFunction.apply(profileRequestContext);
        if (ac == null) {
            log.info("AttributeContext not found in profile request context.");
            return ProtocolError.IllegalState.event(this);
        }

        final TicketValidationResponse response = FlowStateSupport.getTicketValidationResponse(springRequestContext);
        if (response == null) {
            log.info("TicketValidationResponse not found in request scope.");
            return ProtocolError.IllegalState.event(this);
        }

        for (IdPAttribute attribute : ac.getIdPAttributes().values()) {
            log.debug("Processing {}", attribute);
            for (IdPAttributeValue<?> value : attribute.getValues()) {
                response.addAttribute(attribute.getId(), value.getValue().toString());
            }
        }
        return Events.Proceed.event(this);
    }
}
