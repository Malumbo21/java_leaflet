package io.github.makbn.jlmap.engine;

import java.util.function.Consumer;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FunctionalInterface
public interface JLTransporter {

    Consumer<JLTransport> clientToServerTransport();

}
