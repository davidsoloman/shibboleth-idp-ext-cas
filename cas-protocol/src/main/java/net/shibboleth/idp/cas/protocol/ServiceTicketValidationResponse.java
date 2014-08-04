package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service ticket validation response protocol message.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicketValidationResponse {
    @Nonnull private String userName;

    @Nonnull private Map<String, List<String>> attributes = Collections.emptyMap();

    @Nullable private String pgtIou;

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
}
