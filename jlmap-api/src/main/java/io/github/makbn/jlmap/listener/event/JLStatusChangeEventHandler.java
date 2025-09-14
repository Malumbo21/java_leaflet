package io.github.makbn.jlmap.listener.event;

import com.google.gson.Gson;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import io.github.makbn.jlmap.model.JLBounds;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
public class JLStatusChangeEventHandler implements JLEventHandler<Object> {
    public static final String FUNCTION_ZOOM = "zoom";
    public static final String FUNCTION_ZOOM_START = "zoomstart";
    public static final String FUNCTION_ZOOM_END = "zoomend";
    public static final String FUNCTION_RESIZE = "resize";
    public static final Set<String> FUNCTIONS = Set.of(FUNCTION_ZOOM, FUNCTION_ZOOM_START, FUNCTION_ZOOM_END,
            FUNCTION_RESIZE);

    Gson gson = new Gson();

    @Override
    public void handle(@NonNull Object source, @NonNull String functionName, OnJLActionListener<Object> listener, Object param1, Object param2, Object param3, Object param4, Object param5) {
        switch (functionName) {
            case FUNCTION_ZOOM -> listener
                    .onAction(source, new ZoomEvent(JLAction.ZOOM, gson.fromJson(String.valueOf(param3), Integer.class), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            case FUNCTION_ZOOM_START -> listener
                    .onAction(source, new ZoomEvent(JLAction.ZOOM_START, gson.fromJson(String.valueOf(param3), Integer.class), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            case FUNCTION_ZOOM_END -> listener
                    .onAction(source, new ZoomEvent(JLAction.ZOOM_END, gson.fromJson(String.valueOf(param3), Integer.class), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            case FUNCTION_RESIZE -> listener
                    .onAction(source, new ZoomEvent(JLAction.RESIZE, gson.fromJson(String.valueOf(param4), Integer.class), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            default -> log.error("{} not implemented!", functionName);
        }
    }

    @Override
    public boolean canHandle(@NonNull String functionName) {
        return FUNCTIONS.contains(functionName);
    }
}
