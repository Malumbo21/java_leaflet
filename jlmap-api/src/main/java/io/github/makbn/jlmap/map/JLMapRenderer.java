package io.github.makbn.jlmap.map;

import io.github.makbn.jlmap.model.JLMapOption;
import lombok.NonNull;

public interface JLMapRenderer {

    @NonNull
    String render(@NonNull JLMapOption option);
}
