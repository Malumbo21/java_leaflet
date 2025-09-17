package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.fx.engine.JLJavaFxServerToClientTransporter;
import io.github.makbn.jlmap.layer.leaflet.LeafletLayer;
import io.github.makbn.jlmap.model.JLObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

/**
 * Represents the basic layer.
 *
 * @author Matt Akbarian  (@makbn)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class JLLayer implements LeafletLayer {
    JLWebEngine<Object> engine;
    JLMapEventHandler callbackHandler;
    String componentSessionId = "_" + UUID.randomUUID().toString().replace("-", "") + "_";


    protected JLLayer(JLWebEngine<Object> engine, JLMapEventHandler callbackHandler) {
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


    protected @NotNull JLJavaFxServerToClientTransporter getTransporter() {
        return new JLJavaFxServerToClientTransporter() {
            @Override
            public Function<JLTransportRequest, Object> serverToClientTransport() {
                return transport -> engine.executeScript(transport.function());
            }
        };
    }


}
