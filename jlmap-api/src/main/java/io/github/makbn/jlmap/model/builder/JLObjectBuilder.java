package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.model.JLObject;
import io.github.makbn.jlmap.model.JLOptions;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Matt Akbarian  (@makbn)
 */
abstract class JLObjectBuilder<M extends JLObject<?>, T extends JLObjectBuilder<M, T>> {
    protected String uuid;
    protected JLOptions jlOptions;
    @Nullable
    protected JLTransporter<?> transporter;
    protected final Map<String, Object> options = new LinkedHashMap<>();
    protected final List<String> callbacks = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    public T setUuid(@NonNull String uuid) {
        this.uuid = uuid;
        return self();
    }

    @Nullable
    protected JLTransporter getTransporter() {
        return transporter;
    }

    public T withOptions(@NonNull JLOptions jlOptions) {
        this.jlOptions = jlOptions;
        options.clear();
        JLOptionsBuilder builder = new JLOptionsBuilder();
        builder.setOption(jlOptions);
        options.putAll(builder.build());
        return self();
    }

    public T withCallbacks(Consumer<JLCallbackBuilder> config) {
        JLCallbackBuilder cb = new JLCallbackBuilder(getElementType(), getElementVarName());
        config.accept(cb);
        callbacks.addAll(cb.build());
        return self();
    }

    public T setTransporter(@Nullable JLTransporter<?> transporter) {
        this.transporter = transporter;
        return self();
    }

    protected abstract String getElementVarName();

    protected abstract String getElementType();

    protected String renderOptions() {
        return options.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(e -> e.getKey() + ": " + getValue(e.getValue()))
                .collect(Collectors.joining(", "));
    }

    private String getValue(@NonNull Object value) {
        if (value instanceof String stringValue) {
            return "\"" + stringValue + "\"";
        } else {
            return Objects.toString(value);
        }
    }

    protected String renderCallbacks() {
        return String.join("\n", callbacks);
    }

    public abstract String buildJsElement();

    public abstract M buildJLObject();
}
