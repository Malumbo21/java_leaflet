package io.github.makbn.jlmap.listener.event;

import io.github.makbn.jlmap.listener.OnJLActionListener;
import lombok.NonNull;

public interface JLEventHandler<T> {

    void handle(@NonNull T source, @NonNull String functionName, OnJLActionListener<T> listener, Object param1, Object param2,
                Object param3, Object param4, Object param5);

    boolean canHandle(@NonNull String functionName);
}
