package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletLayer;
import io.github.makbn.jlmap.model.JLObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the basic layer.
 *
 * @author Matt Akbarian  (@makbn)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class JLLayer implements LeafletLayer {
    JLWebEngine<Object> engine;
    JLMapCallbackHandler callbackHandler;
    String componentSessionId = "_" + UUID.randomUUID().toString().replace("-", "") + "_";


    protected JLLayer(JLWebEngine<Object> engine, JLMapCallbackHandler callbackHandler) {
        this.engine = engine;
        this.callbackHandler = callbackHandler;
    }

    @NotNull
    protected String getElementUniqueName(@NonNull Class<? extends JLObject<?>> markerClass, int id) {
        return markerClass.getSimpleName() + componentSessionId + id;
    }

    @NonNull
    protected final String removeLayerWithUUID(@NonNull String uuid) {
        return String.format("this.map.removeLayer(this.%s)", uuid);
    }

}
