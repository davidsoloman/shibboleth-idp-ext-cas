package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service ticket validation response protocol message.
 *
 * @author Marvin S. Addison
 */
public class TicketValidationResponse {
    @Nonnull private String userName;

    @Nonnull private Map<String, List<String>> attributes = Collections.emptyMap();

    @Nullable private String pgtIou;

    @Nonnull private List<String> proxies = new ArrayList<String>();

    @Nonnull public String getUserName() {
        return userName;
    }

    public void setUserName(@Nonnull final String user) {
        Constraint.isNotNull(user, "Username cannot be null");
        this.userName = user;
    }

    /** @return Immutable map of user attributes. */
    public Map<String, List<String>> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Nullable public String getPgtIou() {
        return pgtIou;
    }

    public void setPgtIou(@Nullable final String pgtIou) {
        this.pgtIou = StringSupport.trimOrNull(pgtIou);
    }

    /** @return Immutable list of proxies traversed in order of most recent to last recent. */
    @Nonnull public List<String> getProxies() {
        return Collections.unmodifiableList(proxies);
    }

    /**
     * Adds a proxy to the list of proxies traversed.
     *
     * @param proxy Name of a proxying service, typically a URI.
     */
    public void addProxy(final String proxy) {
        proxies.add(proxy);
    }
}
