package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.idp.cas.protocol.ProtocolUri;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Dispatches incoming CAS protocol requests onto the appropriate action state by examining the protocol request URI.
 * The events emitted by this action correspond to the CAS protocol URIs:
 *
 * <ol>
 *     <li><code>login</code></li>
 *     <li><code>validate</code></li>
 *     <li><code>serviceValidate</code></li>
 *     <li><code>proxy</code></li>
 *     <li><code>proxyValidate</code></li>
 * </ol>
 *
 * @author Marvin S. Addison
 */
public class DispatchProtocolAction implements Action {
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(DispatchProtocolAction.class);

    @Override
    public Event execute(@Nonnull RequestContext context) throws Exception {
        final ProtocolUri uri = determineUri(context);
        if (uri == null) {
            return new Event(this, Events.ProtocolViolation.id());
        }
        log.debug("Dispatching to {}", uri.id());
        return new Event(this, uri.id());
    }


    /**
     * Parses the request URI and determines the CAS protocol URI.
     *
     * @param context HTTP servlet request context.
     *
     * @return CAS protocol URI or null if the request URI does not match a known value.
     */
    private static final ProtocolUri determineUri(final RequestContext context) {
        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
        final String parts[] = request.getRequestURI().split("/");
        if (parts.length > 0) {
            final String last = StringSupport.trimOrNull(parts[parts.length - 1]);
            if (last != null) {
                for (ProtocolUri uri : ProtocolUri.values()) {
                    if (uri.name().compareToIgnoreCase(last) == 0) {
                        return uri;
                    }
                }
            }
        }
        return null;
    }


}
