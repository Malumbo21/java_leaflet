package io.github.makbn.jlmap.model.function;

import io.github.makbn.jlmap.engine.JLTransport;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.model.JLObject;

public interface JLFunctionBase<T extends JLObject<T>> {
    T self();


    JLTransporter<?> getTransport();


    default T remove() {
        getTransport().execute(new JLTransport(self(),
                String.format("%1$s.%2$s.remove()", mapReference(), self().getId())));
        return self();
    }

    default String getAttribution() {
        return getTransport().execute(new JLTransport(self(),
                String.format("%1$s.%2$s.getAttribution();", mapReference(), self().getId())));
    }

    default String mapReference() {
        return "this";
    }
}
