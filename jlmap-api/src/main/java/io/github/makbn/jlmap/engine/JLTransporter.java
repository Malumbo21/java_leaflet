package io.github.makbn.jlmap.engine;

import io.github.makbn.jlmap.exception.JLException;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FunctionalInterface
public interface JLTransporter<T> {

    Function<JLTransport, T> clientToServerTransport();

    @SuppressWarnings("unchecked")
    default <M> M covertResult(T result) {
        try {
            return (M) result;
        } catch (Exception e) {
            throw new JLException("Error converting transport result", e);
        }
    }

    default <M> M execute(JLTransport transport) {
        return covertResult(Optional.ofNullable(clientToServerTransport().apply(transport))
                .orElseThrow(() -> new JLException("No client to server transport found")));

    }

}
