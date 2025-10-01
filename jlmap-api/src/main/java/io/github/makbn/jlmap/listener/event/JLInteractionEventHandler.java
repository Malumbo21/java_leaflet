package io.github.makbn.jlmap.listener.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.element.menu.JLContextMenuMediator;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;
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
    private static final String FUNCTION_CONTEXT_MENU = "contextmenu";

    public static final Set<String> FUNCTIONS = Set.of(FUNCTION_CLICK, FUNCTION_DOUBLE_CLICK, FUNCTION_CONTEXT_MENU);

    Gson gson;
    JLContextMenuMediator contextMenuMediator;

    public JLInteractionEventHandler() {
        this.gson = new Gson();
        this.contextMenuMediator = ServiceLoader.load(JLContextMenuMediator.class).findFirst().orElseThrow();
    }

    @Override
    public void handle(@NonNull JLMap<?> map, @NonNull JLObject<?> source, @NonNull String functionName, OnJLActionListener<JLObject<?>> listener, Object param1, Object param2, Object param3, Object param4, Object param5) {
        switch (functionName) {
            case FUNCTION_CLICK -> listener
                    .onAction(source, new ClickEvent(JLAction.CLICK, gson.fromJson(String.valueOf(param4), JLLatLng.class)));
            case FUNCTION_DOUBLE_CLICK -> listener
                    .onAction(source, new ClickEvent(JLAction.DOUBLE_CLICK, gson.fromJson(String.valueOf(param4), JLLatLng.class)));
            case FUNCTION_CONTEXT_MENU -> handleContextMenuEvent(map, source, listener, param4, param5);
            default -> log.error("{} not implemented!", functionName);
        }
    }

    private void handleContextMenuEvent(@NonNull JLMap<?> map, @NonNull JLObject<?> source, OnJLActionListener<JLObject<?>> listener, Object param4, Object param5) {
        ContextMenuEvent event = new ContextMenuEvent(JLAction.CONTEXT_MENU, gson.fromJson(String.valueOf(param4), JLLatLng.class),
                gson.fromJson(String.valueOf(param5), JLBounds.class), getPosition(String.valueOf(param4)));

        contextMenuMediator.showContextMenu(map, source, event.x(), event.y());
        listener.onAction(source, event);
    }


    private double[] getPosition(String positionJson) {
        try {
            JsonObject jsonObject = JsonParser.parseString(positionJson).getAsJsonObject();
            return new double[]{jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble()};
        } catch (Exception e) {
            log.error("Error parsing position from {}", positionJson, e);
            return new double[]{0, 0};
        }
    }


    @Override
    public boolean canHandle(@NonNull String functionName) {
        return FUNCTIONS.contains(functionName);
    }
}
