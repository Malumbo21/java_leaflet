package io.github.makbn.jlmap.engine;

import io.github.makbn.jlmap.exception.JLException;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FunctionalInterface
public interface JLServerToClientTransporter<T> {

    Function<JLTransportRequest, T> serverToClientTransport();

    @SuppressWarnings("unchecked")
    default <M> M covertResult(T result) {
        try {
            return (M) result;
        } catch (Exception e) {
            throw new JLException("Error converting transport result", e);
        }
    }

    default <M> M execute(JLTransportRequest transport) {
        return covertResult(Optional.ofNullable(serverToClientTransport().apply(transport))
                .orElseThrow(() -> new JLException("No client to server transport found")));

    }

}
