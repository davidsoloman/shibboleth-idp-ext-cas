package net.shibboleth.idp.cas.protocol;

/**
 * Protocol parameter name enumeration.
 *
 * @author Marvin S. Addison
 */
public enum ProtocolParam {

    /** Service identifier, which is typically a URL. */
    Service,

    /** Service ticket. */
    Ticket,

    /** Forced authentication flag. */
    Renew,

    /** Gateway authentication flag. */
    Gateway,

    /** Proxy-granting ticket callback URL. */
    PgtUrl,

    /** Proxy-granting ticket. */
    Pgt,

    /** Target service for proxy-granting ticket. */
    TargetService;


    /**
     * Converts enumeration name to lower-case name as used by CAS protocol document.
     *
     * @return Events enumeration name with first letter lower-cased.
     */
    public String id() {
        return this.name().substring(0, 1).toLowerCase() + this.name().substring(1);
    }
}
