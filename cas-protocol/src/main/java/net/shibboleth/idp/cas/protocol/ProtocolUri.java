package net.shibboleth.idp.cas.protocol;

/**
 * Describes CAS protocol URIs.
 *
 * @author Marvin S. Addison
 */
public enum ProtocolUri {
    /** Login URI called presumably to request a service ticket to access a target service. */
    Login,

    /** CAS 1.0 service ticket validation event. */
    Validate,

    /** CAS 2.0 service ticket validation event. */
    ServiceValidate,

    /** Proxy ticket request event. */
    Proxy,

    /** Proxy ticket validation event. */
    ProxyValidate;


    /**
     * Converts enumeration name to lower-case name as used by CAS protocol document.
     *
     * @return Enumeration name with first letter lower-cased.
     */
    public String id() {
        return this.name().substring(0, 1).toLowerCase() + this.name().substring(1);
    }
}
