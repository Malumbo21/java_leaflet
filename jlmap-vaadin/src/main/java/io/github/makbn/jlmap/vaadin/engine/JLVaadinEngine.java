package io.github.makbn.jlmap.vaadin.engine;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementAttachListener;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.exception.JLException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Vaadin implementation of the JLWebEngine for executing JavaScript in Vaadin components.
 *
 * @author Vaadin Implementation
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinEngine extends JLWebEngine<PendingJavaScriptResult> {
    Supplier<Element> mapElement;
    @NonFinal
    Status currentStatus;

    /**
     * Creates a new JLVaadinEngine with the specified Vaadin Page.
     */
    public JLVaadinEngine(Supplier<Element> mapElement) {
        super(PendingJavaScriptResult.class);
        this.mapElement = mapElement;
        this.mapElement.get().addAttachListener((ElementAttachListener) elementAttachEvent ->
                currentStatus = Status.SUCCEEDED);
    }

    /**
     * Executes JavaScript code and attempts to cast the result to the specified type.
     * Note: Due to Vaadin's asynchronous JavaScript execution, this method may not return
     * actual values from the JavaScript execution. For operations requiring return values,
     * consider using callbacks or other patterns.
     *
     * @param script the JavaScript code to execute
     * @param type   the class representing the expected return type
     * @param <T>    the type parameter for the return value
     * @return the result of the JavaScript execution cast to type T, or null if not available
     */
    @SneakyThrows
    @Override
    public <T> T executeScript(@NonNull String script, @NonNull Class<T> type) {
        if (mapElement.get() == null) {
            throw new IllegalStateException("mapElement is null");
        }
        return Optional.of(mapElement)
                .map(Supplier::get)
                .map(element -> element.executeJs(script))
                .map(type::cast)
                .orElseThrow(() -> new JLException("Could not execute script " + script));
    }

    /**
     * Gets the current status of the engine.
     * Note: Vaadin doesn't provide a direct way to check the status of JavaScript execution.
     * This implementation always returns the last known status.
     *
     * @return the current status of the engine
     */
    @Override
    public Status getStatus() {
        return currentStatus;
    }

    /**
     * Sets the current status of the engine.
     * This can be used by the application to update the status based on other indicators.
     *
     * @param status the new status to set
     */
    public void setStatus(Status status) {
        this.currentStatus = status;
    }
}