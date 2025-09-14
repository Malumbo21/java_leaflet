package io.github.makbn.jlmap.listener.event;

import com.google.gson.Gson;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLInteractionEventHandler implements JLEventHandler<JLObject<?>> {
    /**
     * Fired when the user clicks (or taps) the layer.
     */
    private static final String FUNCTION_CLICK = "click";
    /**
     * Fired when the user double-clicks (or double-taps) the layer.
     */
    private static final String FUNCTION_DOUBLE_CLICK = "dblclick";

    public static final Set<String> FUNCTIONS = Set.of(FUNCTION_CLICK, FUNCTION_DOUBLE_CLICK);

    Gson gson = new Gson();


    @Override
    public void handle(@NonNull JLObject<?> source, @NonNull String functionName, OnJLActionListener<JLObject<?>> listener, Object param1, Object param2, Object param3, Object param4, Object param5) {
        switch (functionName) {
            case FUNCTION_CLICK -> listener
                    .onAction(source, new ClickEvent(JLAction.CLICK, gson.fromJson(String.valueOf(param4), JLLatLng.class)));
            case FUNCTION_DOUBLE_CLICK -> listener
                    .onAction(source, new ClickEvent(JLAction.DOUBLE_CLICK, gson.fromJson(String.valueOf(param4), JLLatLng.class)));
            default -> log.error("{} not implemented!", functionName);
        }
    }

    @Override
    public boolean canHandle(@NonNull String functionName) {
        return FUNCTIONS.contains(functionName);
    }
}
