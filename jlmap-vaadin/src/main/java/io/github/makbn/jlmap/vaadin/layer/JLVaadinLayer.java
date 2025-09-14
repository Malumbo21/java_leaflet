package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapEventHandler;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletLayer;
import io.github.makbn.jlmap.model.JLObject;
import io.github.makbn.jlmap.vaadin.engine.JLVaadinServerToClientTransporter;
import lombok.AccessLevel;
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

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class JLVaadinLayer implements LeafletLayer {
    JLWebEngine<PendingJavaScriptResult> engine;
    JLMapEventHandler callbackHandler;
    String componentSessionId = "_" + UUID.randomUUID().toString().replace("-", "") + "_";

    protected JLVaadinLayer(JLWebEngine<PendingJavaScriptResult> engine, JLMapEventHandler callbackHandler) {
        this.engine = engine;
        this.callbackHandler = callbackHandler;
    }

    protected @NotNull String getElementUniqueName(@NonNull Class<? extends JLObject<?>> markerClass, int id) {
        return markerClass.getSimpleName() + componentSessionId + id;
    }

    @NonNull
    protected final String removeLayerWithUUID(@NonNull String uuid) {
        return String.format("this.map.removeLayer(this.%s)", uuid);
    }

    protected @NotNull JLVaadinServerToClientTransporter getTransporter() {
        return new JLVaadinServerToClientTransporter() {
            @Override
            public Function<JLTransportRequest, PendingJavaScriptResult> serverToClientTransport() {
                return transport -> engine.executeScript(transport.function());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
