package net.shibboleth.idp.cas.ticket.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

/**
 * Utility class that reads and writes values of fields annotated with metadata annotations.
 *
 * @author Marvin S. Addison
 */
public final class MetaDataUtil {
    /** Private constructor of utility class. */
    private MetaDataUtil() {}

    /** Simple cache of fields we have seen. */
    private static final Map<String, Field> FIELD_CACHE = new HashMap<String, Field>();


    /**
     * Gets the value of the field indicated by the {@link Key} annotation on the given object.
     *
     * @param target Object from which to get key.
     *
     * @return Key field value. If the value is not a {@link String}, then it is converted to a <code>String</code>
     * by calling <code>toString()</code> on the object.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Key} annotation.
     * @throws RuntimeException If the field cannot be read on the target object.
     */
    public static String getKey(@Nonnull final Object target) {
        final Key keyField = getAnnotation(target, Key.class);
        final Object value = getFieldValue(target, keyField.value());
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }


    /**
     * Sets the value of the field indicated by the {@link Key} annotation on the given object.
     *
     * @param target Object on which to set key.
     * @param key Key value.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Key} annotation.
     * @throws RuntimeException If the field cannot be set on the target object.
     */
    public static void setKey(@Nonnull final Object target, @Nonnull final String key) {
        final Key keyField = getAnnotation(target, Key.class);
        // TODO: handle String to Object conversion in general case
        setFieldValue(target, keyField.value(), key);
    }


    /**
     * Gets the value of the field indicated by the {@link Value} annotation on the given object.
     *
     * @param target Object from which to get value.
     *
     * @return Value field value. If the value is not a {@link String}, then it is converted to a <code>String</code>
     * by calling <code>toString()</code> on the object.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Value} annotation.
     * @throws RuntimeException If the field cannot be read on the target object.
     */
    public static String getValue(@Nonnull final Object target) {
        final Value valueField = getAnnotation(target, Value.class);
        final Object value = getFieldValue(target, valueField.value());
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }


    /**
     * Sets the value of the field indicated by the {@link Value} annotation on the given object.
     *
     * @param target Object on which to set value.
     * @param value Value field value.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Value} annotation.
     * @throws RuntimeException If the field cannot be set on the target object.
     */
    public static void setValue(@Nonnull final Object target, @Nonnull final String value) {
        final Value valueField = getAnnotation(target, Value.class);
        // TODO: handle String to Object conversion in general case
        setFieldValue(target, valueField.value(), value);
    }


    /**
     * Gets the value of the field indicated by the {@link Expiration} annotation on the given object.
     *
     * @param target Object from which to get expiration.
     *
     * @return Expiration field value as a long indicating milliseconds since the beginning of the Unix epoch.
     * The following data types are supported:
     * <ul>
     *     <li><code>long</code></li>
     *     <li>{@link Date}</li>
     *     <li>{@link ReadableInstant}</li>
     * </ul>
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Expiration} annotation.
     * @throws RuntimeException If the field cannot be read on the target object or if it is an unsupported data type.
     *
     */
    public static long getExpiration(@Nonnull final Object target) {
        final Expiration expField = getAnnotation(target, Expiration.class);
        final Object value = getFieldValue(target, expField.value());
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        } else if (value instanceof ReadableInstant) {
            return ((ReadableInstant) value).getMillis();
        }
        throw new RuntimeException(value + " is an unsupported data type for an expiration field.");
    }


    /**
     * Sets the value of the field indicated by the {@link Expiration} annotation on the given object.
     *
     * @param target Object on which to set expiration. The expiration field of the target object may be one of the
     *               following supported types:
     * <ul>
     *     <li><code>long</code></li>
     *     <li>{@link Date}</li>
     *     <li>{@link ReadableInstant}</li>
     * </ul>
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Expiration} annotation.
     * @throws RuntimeException If the field cannot be set on the target object or if it is an unsupported data type.
     *
     */
    public static void setExpiration(@Nonnull final Object target, final long expiration) {
        final Expiration expField = getAnnotation(target, Expiration.class);
        final Class<?> type = getField(target, expField.value()).getType();
        if (Long.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), expiration);
        } else if (Date.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), new Date(expiration));
        } else if (ReadableInstant.class.isAssignableFrom(type)) {
            setFieldValue(target, expField.value(), new Instant(expiration));
        } else {
            throw new RuntimeException(type + " is an unsupported data type for an expiration field.");
        }
    }


    /**
     * Gets the value of the field indicated by the {@link Version} annotation on the given object.
     *
     * @param target Object from which to get version.
     *
     * @return Version field value, which must be of type <code>int</code> or <code>Integer</code>.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Expiration} annotation.
     * @throws RuntimeException If the field cannot be read on the target object or if it is an unsupported data type.
     *
     */
    public static int getVersion(@Nonnull final Object target) {
        final Version versionField = getAnnotation(target, Version.class);
        final Object value = getFieldValue(target, versionField.value());
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new RuntimeException(value + " is an unsupported data type for a version field.");
    }


    /**
     * Sets the value of the field indicated by the {@link Version} annotation on the given object.
     *
     * @param target Object from which to get version.
     * @param version Object version.
     *
     * @throws IllegalArgumentException If the target object does declare a {@link Expiration} annotation.
     * @throws RuntimeException If the field cannot be set on the target object or if it is an unsupported data type.
     *
     */
    public static void setVersion(@Nonnull final Object target, final int version) {
        final Version versionField = getAnnotation(target, Version.class);
        final Class<?> type = getField(target, versionField.value()).getType();
        if (Integer.class.isAssignableFrom(type)) {
            setFieldValue(target, versionField.value(), version);
        } else {
            throw new RuntimeException(type + " is an unsupported data type for a version field.");
        }
    }


    private static <T extends Annotation> T getAnnotation(final Object target, final Class<T> annotationType) {
        final Class<?> targetClass = target.getClass();
        final T keyField = targetClass.getAnnotation(annotationType);
        if (keyField == null) {
            throw new IllegalArgumentException("Key annotation not found on " + target);
        }
        return keyField;
    }

    private static Field getField(final Object target, final String fieldName) {
        final Class<?> targetClass = target.getClass();
        final String key = targetClass.getName() + "." + fieldName;
        Field field = FIELD_CACHE.get(key);
        if (field == null) {
            try {
                field = targetClass.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    // Try to make it accessible
                    field.setAccessible(true);
                }
                FIELD_CACHE.put(key, field);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field " + fieldName + " does not exist on " + target);
            }
        }
        return field;
    }

    private static Object getFieldValue(final Object target, final String fieldName) {
        try {
            final Field field = getField(target, fieldName);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + fieldName + " cannot be read on " + target);
        }
    }

    private static void setFieldValue(final Object target, final String fieldName, final Object fieldValue) {
        try {
            final Field field = getField(target, fieldName);
            field.set(target, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + fieldName + " cannot be set on " + target);
        }
    }
}
