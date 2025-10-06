package io.github.makbn.jlmap.listener.event;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.github.makbn.jlmap.JLMap;
import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.OnJLActionListener;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLLayerEventHandler implements JLEventHandler<Object> {
    /**
     * Fired after the layer is added to a map
     */
    public static final String FUNCTION_ADD = "add";
    /**
     * Fired after the layer is removed from a map
     */
    public static final String FUNCTION_REMOVE = "remove";

    public static final Set<String> FUNCTIONS = Set.of(FUNCTION_ADD, FUNCTION_REMOVE);

    Gson gson = new Gson();

    @Override
    public void handle(@NonNull JLMap<?> map, @NonNull Object source, @NonNull String functionName, OnJLActionListener<Object> listener,
                       Object param1, Object param2, Object param3, Object param4, Object param5) {
        switch (functionName) {
            case FUNCTION_ADD -> listener
                    .onAction(source, new LayerEvent(JLAction.ADD, getJlLatLng((String) param4), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            case FUNCTION_REMOVE -> listener
                    .onAction(source, new LayerEvent(JLAction.REMOVE, getJlLatLng((String) param4), gson.fromJson(String.valueOf(param5), JLBounds.class)));
            default -> log.error("{} not implemented!", functionName);
        }
    }

    @NonNull
    private List<List<JLLatLng>> getJlLatLng(String latLngString) {
        try {
            if (JsonParser.parseString(latLngString).isJsonArray()) {
                Type listType = new TypeToken<List<List<JLLatLng>>>() {
                }.getType();
                return gson.fromJson(latLngString, listType);
            } else {
                return List.of(List.of(gson.fromJson(latLngString, JLLatLng.class)));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canHandle(@NonNull String functionName) {
        return FUNCTIONS.contains(functionName);
    }
}
