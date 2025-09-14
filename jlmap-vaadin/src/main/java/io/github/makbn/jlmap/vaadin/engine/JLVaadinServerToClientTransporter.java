package io.github.makbn.jlmap.vaadin.engine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

/**
 * JLVaadinTransporter is an abstract implementation of JLTransporter for Vaadin,
 * handling the conversion of JavaScript results to Java objects using Gson.
 *
 * <p>This class is used to bridge between JavaScript execution results (PendingJavaScriptResult)
 * and Java model objects, providing a generic conversion mechanism for Vaadin-based JLMap integrations.</p>
 *
 * @author Matt Akbarian (@makbn)
 */
public abstract class JLVaadinServerToClientTransporter implements JLServerToClientTransporter<PendingJavaScriptResult> {

    Gson gson = new Gson();

    /**
     * Converts a PendingJavaScriptResult to a Java object of type M using Gson.
     *
     * @param result the PendingJavaScriptResult from Vaadin
     * @param <M>    the type of the result object
     * @return the converted Java object
     */
    @Override
    @SneakyThrows
    public <M> M covertResult(PendingJavaScriptResult result) {
        CompletableFuture<M> future = new CompletableFuture<>();
        result.then(value -> {
            try {
                future.complete(gson.fromJson(value.toJson(), new TypeToken<M>() {
                }.getType()));
            } catch (ClassCastException e) {
                future.completeExceptionally(e);
            }
        });
        return future.get();
    }
}
