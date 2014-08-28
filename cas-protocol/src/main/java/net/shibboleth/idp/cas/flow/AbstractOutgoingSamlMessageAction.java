/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.profile.AbstractProfileAction;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Response;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Base class for all actions that build SAML {@link org.opensaml.saml.saml1.core.Response} messages for output.
 *
 * @author Marvin S. Addison
 */
public abstract class AbstractOutgoingSamlMessageAction extends AbstractProfileAction<SAMLObject, SAMLObject> {

    /** CAS namespace. */
    protected static final String NAMESPACE = "http://www.ja-sig.org/products/cas/";

    protected static <T extends SAMLObject> T newSAMLObject(final Class<T> type, final QName elementName) {
        final SAMLObjectBuilder<T> builder = (SAMLObjectBuilder<T>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<T>getBuilderOrThrow(elementName);
        return builder.buildObject();
    }

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<SAMLObject, SAMLObject> profileRequestContext) {

        final MessageContext<SAMLObject> msgContext = new MessageContext<>();
        try {
            msgContext.setMessage(buildSamlResponse(springRequestContext, profileRequestContext));
        } catch (IllegalStateException e) {
            return ProtocolError.IllegalState.event(this);
        }
        final SAMLBindingContext bindingContext = new SAMLBindingContext();
        bindingContext.setBindingUri(SAMLConstants.SAML1_SOAP11_BINDING_URI);
        msgContext.addSubcontext(bindingContext);
        profileRequestContext.setOutboundMessageContext(msgContext);

        // Return null to signal that other actions must follow this one before proceeding to next state
        return null;
    }

    protected abstract Response buildSamlResponse(
            @Nonnull RequestContext springRequestContext,
            @Nonnull ProfileRequestContext<SAMLObject, SAMLObject> profileRequestContext);
}
