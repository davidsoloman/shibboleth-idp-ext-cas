/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.idp.authn.AuthenticationResult;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Attribute;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.AttributeValue;
import org.opensaml.saml.saml1.core.Audience;
import org.opensaml.saml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.saml.saml1.core.StatusCode;
import org.opensaml.saml.saml1.core.StatusMessage;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Creates the SAML response message for failed ticket validation at the <code>/samlValidate</code> URI.
 * The SAML message is bound to the outgoing message context as needed by the
 * {@link org.opensaml.profile.action.impl.EncodeMessage} action.
 *
 * @author Marvin S. Addison
 */
public class BuildSamlValidationFailureMessageAction extends AbstractOutgoingSamlMessageAction {

    @Nonnull
    @Override
    protected Response buildSamlResponse(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<SAMLObject, SAMLObject> profileRequestContext) {

        final String code = (String) springRequestContext.getFlashScope().get("code");
        final String detailCode = (String) springRequestContext.getFlashScope().get("detailCode");

        final Response response = newSAMLObject(Response.class, Response.DEFAULT_ELEMENT_NAME);
        final Status status = newSAMLObject(Status.class, Status.DEFAULT_ELEMENT_NAME);
        final StatusCode statusCode = newSAMLObject(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(new QName(NAMESPACE, code));
        status.setStatusCode(statusCode);
        final StatusMessage message = newSAMLObject(StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
        message.setMessage(detailCode);
        status.setStatusMessage(message);
        response.setStatus(status);

        return response;
    }
}
