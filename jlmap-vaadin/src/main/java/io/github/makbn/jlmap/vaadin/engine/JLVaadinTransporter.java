package io.github.makbn.jlmap.vaadin.engine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.engine.JLTransporter;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

public abstract class JLVaadinTransporter implements JLTransporter<PendingJavaScriptResult> {

    Gson gson = new Gson();

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
