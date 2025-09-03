package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransporter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class JLGeoJson extends JLObject<JLGeoJson> {
    @NonFinal
    String id;
    String geoJsonContent;

    @Builder
    public JLGeoJson(String id, String geoJsonContent, JLTransporter<?> transport) {
        super(transport);
        this.id = id;
        this.geoJsonContent = geoJsonContent;
    }

    @Override
    public JLGeoJson self() {
        return this;
    }
}
