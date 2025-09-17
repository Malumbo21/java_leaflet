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

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract base class for Vaadin-based map layers.
 * <p>
 * Provides common infrastructure for Vaadin map layer implementations including
 * JavaScript execution, element naming, and transport layer management. Uses
 * Vaadin's {@link PendingJavaScriptResult} for asynchronous JavaScript operations.
 * </p>
 * <h3>Core Features:</h3>
 * <ul>
 *   <li><strong>Element Management</strong>: Unique naming and session-based identification</li>
 *   <li><strong>JavaScript Execution</strong>: Vaadin Element.executeJs() integration</li>
 *   <li><strong>Transport Layer</strong>: Server-to-client communication via JLVaadinServerToClientTransporter</li>
 *   <li><strong>Event Handling</strong>: Callback management for user interactions</li>
 * </ul>
 *
 * @author Matt Akbarian  (@makbn)
 * @since 2.0.0
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class JLVaadinLayer implements LeafletLayer {

    /**
     * Vaadin web engine for JavaScript execution with PendingJavaScriptResult
     */
    JLWebEngine<PendingJavaScriptResult> engine;

    /** Event handler for managing object callbacks and interactions */
    JLMapEventHandler callbackHandler;

    /** Unique session identifier for element naming collision avoidance */
    String componentSessionId = "_" + UUID.randomUUID().toString().replace("-", "") + "_";

    /**
     * Constructs a Vaadin layer with the specified engine and callback handler.
     *
     * @param engine the Vaadin web engine for JavaScript execution
     * @param callbackHandler the event handler for managing object callbacks
     */
    protected JLVaadinLayer(JLWebEngine<PendingJavaScriptResult> engine, JLMapEventHandler callbackHandler) {
        this.engine = engine;
        this.callbackHandler = callbackHandler;
    }

    /**
     * Generates unique element names with session-based collision avoidance.
     * <p>
     * Combines class simple name, session ID, and numeric ID to ensure uniqueness
     * across multiple browser sessions and component instances.
     * </p>
     *
     * @param markerClass the JL object class for name prefix
     * @param id the numeric identifier
     * @return unique element name for JavaScript reference
     */
    protected @NotNull String getElementUniqueName(@NonNull Class<? extends JLObject<?>> markerClass, int id) {
        return markerClass.getSimpleName() + componentSessionId + id;
    }

    /**
     * Generates JavaScript code to remove a layer by UUID.
     * <p>
     * Creates the JavaScript statement to remove the specified layer from the map
     * using Leaflet's removeLayer method.
     * </p>
     *
     * @param uuid the unique identifier of the layer to remove
     * @return JavaScript code string for layer removal
     */
    @NonNull
    protected final String removeLayerWithUUID(@NonNull String uuid) {
        return String.format("this.map.removeLayer(this.%s)", uuid);
    }

    /**
     * Creates a server-to-client transporter for JavaScript method invocation.
     * <p>
     * Returns an anonymous implementation that generates JavaScript method calls
     * from transport requests and executes them via the Vaadin engine.
     * </p>
     *
     * @return configured transporter for Vaadin JavaScript execution
     */
    protected @NotNull JLVaadinServerToClientTransporter getTransporter() {
        return new JLVaadinServerToClientTransporter() {
            @Override
            public Function<JLTransportRequest, PendingJavaScriptResult> serverToClientTransport() {
                return transport -> {
                    // Generate JavaScript method call: this.objectId.methodName(param1,param2,...)
                    String script = "return this.%1$s.%2$s(%3$s);" .formatted(transport.self().getJLId(), transport.function(),
                            transport.params().length > 0 ? Arrays.stream(transport.params()).map(String::valueOf).collect(Collectors.joining(",")) : "");
                    return engine.executeScript(script);
                };
            }
        };
    }

    /** @inheritDoc */
    @Override
    public String toString() {
        return super.toString();
    }
}
