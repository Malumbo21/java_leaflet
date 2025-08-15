package io.github.makbn.jlmap.engine;

import java.util.function.Consumer;

@FunctionalInterface
public interface JLTransporter {

    Consumer<JLTransport> clientToServerTransport();

}
