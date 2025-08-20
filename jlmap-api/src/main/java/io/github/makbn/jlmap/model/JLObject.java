package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLTransport;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.listener.OnJLObjectActionListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * Represents basic object classes for interacting with Leaflet
 *
 * @author Matt Akbarian  (@makbn)
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract sealed class JLObject<T extends JLObject<?>> permits JLCircle, JLCircleMarker, JLMarker,
        JLMultiPolyline, JLPolygon, JLPolyline, JLPopup {
    public static final String REFERENCE_PREFIX = "jl_map_item_";

    JLTransporter transport;

    @NonFinal
    OnJLObjectActionListener<T> listener;
    @Getter
    @Setter
    @NonFinal
    JLPopup popup;

    public OnJLObjectActionListener<T> getOnActionListener() {
        return listener;
    }

    public void setOnActionListener(OnJLObjectActionListener<T> listener) {
        this.listener = listener;
    }

    public abstract String getId();

    public void remove() {
        transport.clientToServerTransport().accept(new JLTransport(this,"remove", getId()));
    }

    public void update(Object... params) {

    }
}
