package io.github.makbn.jlmap.engine;


import io.github.makbn.jlmap.model.JLObject;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable transport request for JavaScript method invocation on map objects.
 * <p>
 * Encapsulates the target object, method name, return type, and parameters for
 * JavaScript execution via the transport layer. Supports both void operations
 * and typed return values.
 * </p>
 *
 * @param self the target JL object for method invocation
 * @param function the JavaScript method name to invoke
 * @param clazz the expected return type (Void.class for void operations)
 * @param params method parameters (converted to JavaScript arguments)
 * @author Matt Akbarian  (@makbn)
 * @since 2.0.0
 */
public record JLTransportRequest(JLObject<?> self, String function, Class<?> clazz, Object... params) {

    /**
     * Creates a void transport request with no return value.
     *
     * @param self     the target object
     * @param function the method name
     * @param params   method parameters
     * @return transport request for void operation
     */
    public static JLTransportRequest voidCall(@NonNull JLObject<?> self, @NonNull String function, Object... params) {
        return new JLTransportRequest(self, function, Void.class, params);
    }

    /**
     * Creates a returnable transport request with typed return value.
     *
     * @param self     the target object
     * @param function the method name
     * @param clazz    the expected return type
     * @param params   method parameters
     * @return transport request for typed operation
     */
    public static JLTransportRequest returnableCall(@NonNull JLObject<?> self, @NonNull String function, Class<?> clazz, Object... params) {
        return new JLTransportRequest(self, function, clazz, params);
    }

    /**
     * Type-safe cast of the return type class.
     *
     * @param <M> the target type
     * @return the class cast to the target type
     */
    @SuppressWarnings("unchecked")
    public <M> Class<M> getCastedClazz() {
        return (Class<M>) clazz;
    }

    /**
     * Equality based on function name and parameters (excludes self and clazz).
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JLTransportRequest that)) return false;
        return Objects.equals(function, that.function) && Objects.deepEquals(params, that.params);
    }

    /**
     * Hash code based on function name and parameters.
     */
    @Override
    public int hashCode() {
        return Objects.hash(function, Arrays.hashCode(params));
    }

    /**
     * String representation showing function name and parameters.
     */
    @NonNull
    @Override
    public String toString() {
        return "JLTransport{" +
                "function='" + function() + '\'' +
                ", params=" + Arrays.toString(params()) +
                '}';
    }
}
