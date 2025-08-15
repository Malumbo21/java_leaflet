package io.github.makbn.jlmap.engine;


import io.github.makbn.jlmap.model.JLObject;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record JLTransport(JLObject<?> self, String function, Object... params) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JLTransport that)) return false;
        return Objects.equals(function, that.function) && Objects.deepEquals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, Arrays.hashCode(params));
    }

    @NonNull
    @Override
    public String toString() {
        return "JLTransport{" +
                "function='" + function() + '\'' +
                ", params=" + Arrays.toString(params()) +
                '}';
    }
}
