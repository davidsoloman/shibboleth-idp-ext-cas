/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Ticket validation request message.
 *
 * @author Marvin S. Addison
 */
public class TicketValidationRequest extends ServiceTicketResponse {

    /** CAS protocol renew flag. */
    private boolean renew;

    /** Proxy-granting ticket validation URL. */
    @Nonnull private String pgtUrl;

    /**
     * Creates a CAS ticket validation request message.
     *
     * @param service Service to which ticket was issued.
     * @param ticket Ticket to validate.
     */
    public TicketValidationRequest(@Nonnull final String service, @Nonnull final String ticket) {
        super(service, ticket);
    }

    public boolean isRenew() {
        return renew;
    }

    public void setRenew(final boolean renew) {
        this.renew = renew;
    }

    @Nonnull public String getPgtUrl() {
        return pgtUrl;
    }

    public void setPgtUrl(@Nonnull final String url) {
        this.pgtUrl = StringSupport.trimOrNull(url);
    }
}
