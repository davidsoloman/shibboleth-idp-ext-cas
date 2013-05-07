package net.shibboleth.idp.cas.ticket.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the field of a class that serves as the value in a persistence storage context.
 *
 * @author Marvin S. Addison
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}
