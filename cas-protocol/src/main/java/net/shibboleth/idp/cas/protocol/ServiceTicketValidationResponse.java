package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Service ticket validation response protocol message.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicketValidationResponse {
    @Nonnull private final String username;

    @Nullable private String pgtIou;

    public ServiceTicketValidationResponse(@Nonnull final String username) {
        this.username = Constraint.isNotNull(username, "Username cannot be null");
    }

    @Nonnull public String getUsername() {
        return username;
    }

    @Nullable public String getPgtIou() {
        return pgtIou;
    }

    public void setPgtIou(@Nullable final String pgtIou) {
        this.pgtIou = StringSupport.trimOrNull(pgtIou);
    }
}
