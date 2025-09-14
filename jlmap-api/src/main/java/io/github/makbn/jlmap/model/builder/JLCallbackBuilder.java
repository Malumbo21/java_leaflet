package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.listener.JLAction;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLCallbackBuilder {
    List<String> callbacks = new ArrayList<>();
    String varName;
    String elementType;

    public JLCallbackBuilder(String elementType, String varName) {
        this.varName = varName;
        this.elementType = elementType;
    }

    public JLCallbackBuilder on(JLAction event) {
        callbacks.add(String.format("""
                this.%3$s.on('%1$s', e => this.jlMapElement.$server.eventHandler('%1$s', '%2$s', e.target.uuid, this.map.getZoom(),
                    JSON.stringify((typeof e.target.getLatLng === "function") ? { "lat": e.target.getLatLng().lat, "lng": e.target.getLatLng().lng } : {"lat": e.latlng.lat, "lng": e.latlng.lng}),
                    JSON.stringify(this.map.getBounds())
                ));
                """, event.getJsEventName(), elementType, varName));
        return this;
    }

    public List<String> build() {
        return callbacks;
    }
}
