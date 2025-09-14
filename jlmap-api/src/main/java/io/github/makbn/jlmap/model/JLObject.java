package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
import io.github.makbn.jlmap.listener.OnJLObjectActionListener;
import io.github.makbn.jlmap.model.function.JLFunctionBase;
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
public abstract sealed class JLObject<T extends JLObject<T>> implements JLFunctionBase<T> permits JLCircle, JLCircleMarker, JLGeoJson, JLImageOverlay, JLMarker, JLMultiPolyline, JLPolygon, JLPolyline, JLPopup {

    @Getter(AccessLevel.PUBLIC)
    JLServerToClientTransporter<?> transport;

    @NonFinal
    OnJLObjectActionListener<T> listener;
    @Getter
    @Setter
    @NonFinal
    JLPopup popup;

    /**
     * By default, marker images zIndex is set automatically based on its latitude.
     * Use this option if you want to put the marker on top of all others (or below),
     * specifying a high value like 1000 (or high negative value, respectively).
     */
    @NonFinal
    int zIndexOffset = 0;
    /**
     * The opacity of the marker.
     */
    @NonFinal
    double opacity = 1.0;

    public OnJLObjectActionListener<T> getOnActionListener() {
        return listener;
    }

    public void setOnActionListener(OnJLObjectActionListener<T> listener) {
        this.listener = listener;
    }

    public abstract String getId();

    public void update(Object... params) {

    }


    /**
     * By default, marker images zIndex is set automatically based on its latitude. Use this option if you want
     * to put the marker on top of all others (or below), specifying a high value like 1000 (or high
     * negative value, respectively).
     * Read more <a href="https://leafletjs.com/reference.html#marker-zindexoffset">here</a>!
     *
     * @param offset new zIndex offset of the marker.
     * @return the current instance of JLMarker.
     */
    public T setZIndexOffset(int offset) {
        getTransport().execute(new JLTransportRequest(this,
                String.format("this.%s.setZIndexOffset(%d);", getId(), offset)));
        this.zIndexOffset = offset;
        return self();
    }

    /**
     * Changes the marker opacity.
     * Read more <a href="https://leafletjs.com/reference.html#marker-opacity">here</a>!
     *
     * @param opacity value between 0.0 and 1.0.
     * @return the current instance of JLMarker.
     */
    public T setOpacity(double opacity) {
        getTransport().execute(new JLTransportRequest(this,
                String.format("this.%s.setOpacity(%f);", getId(), opacity)));
        this.opacity = opacity;
        return self();
    }
}
