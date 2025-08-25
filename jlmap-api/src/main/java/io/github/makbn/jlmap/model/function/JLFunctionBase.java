package io.github.makbn.jlmap.model.function;

import io.github.makbn.jlmap.engine.JLTransport;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.model.JLObject;

public interface JLFunctionBase<T extends JLObject<T>> {
    T self();

    JLTransporter<?> getTransport();


    default T remove() {
        getTransport().execute(new JLTransport(self(), "this.%$1s.remove();"));
        return self();
    }

    default String getAttribution() {
        return getTransport().execute(new JLTransport(self(),
                String.format("return this.%s.getAttribution();", self().getId())));
    }
}
