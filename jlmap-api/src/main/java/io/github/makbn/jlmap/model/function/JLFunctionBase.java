package io.github.makbn.jlmap.model.function;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.model.JLObject;

public interface JLFunctionBase<T extends JLObject<T>> {
    T self();


    JLServerToClientTransporter<?> getTransport();


    default T remove() {
        getTransport().execute(new JLTransportRequest(self(),
                String.format("this.%1$s.remove()", self().getJLId())));
        return self();
    }

    default String getAttribution() {
        return getTransport().execute(new JLTransportRequest(self(),
                String.format("this.%1$s.getAttribution();", self().getJLId())));
    }
}
