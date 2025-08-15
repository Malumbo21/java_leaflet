package io.github.makbn.jlmap.engine;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class JLWebEngine<C> {
    Class<C> defaultClass;
    public abstract <T> T executeScript(String script, Class<T> type);

    public abstract Status getStatus();

    public C executeScript(@NonNull String script) {
        return this.executeScript(script, defaultClass);
    }

    public enum Status {
        SUCCEEDED,
        FAILED
    }
}
