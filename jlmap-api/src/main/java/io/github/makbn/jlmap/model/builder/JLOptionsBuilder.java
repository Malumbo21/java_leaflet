package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLOptions;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Matt Akbarian  (@makbn)
 */
public class JLOptionsBuilder {

    private JLOptions jlOptions;

    Map<String, Object> build() {
        return Arrays.stream(jlOptions.getClass().getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0)
                .filter(method -> method.getReturnType().isPrimitive() || method.getReturnType().isEnum() || method.getReturnType().equals(String.class))
                .filter(method -> method.getName().matches("(^get.*|^is.*)"))
                .collect(Collectors.toMap(this::getKey, this::getValue));
    }

    private String getKey(@NotNull Method method) {
        String fieldName = method.getName().replace("get", "").replace("is", "");
        return Character.toLowerCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
    }

    @SneakyThrows
    private Object getValue(Method method) {
        return method.invoke(jlOptions);
    }

    public JLOptionsBuilder setOption(@NonNull JLOptions options) {
        this.jlOptions = options;
        return this;
    }
}