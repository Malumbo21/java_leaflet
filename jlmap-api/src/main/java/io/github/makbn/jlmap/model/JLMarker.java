package io.github.makbn.jlmap.model;


import io.github.makbn.jlmap.engine.JLTransport;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.listener.JLAction;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * JLMarker is used to display clickable/draggable icons on the map!
 *
 * @author Matt Akbarian  (@makbn)
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JLMarker extends JLObject<JLMarker> {
    /**
     * id of object! this is an internal id for JLMap Application and not
     * related to Leaflet!
     */
    String id;
    /**
     * optional text for showing on created JLMarker tooltip.
     */
    String text;
    /**
     * Coordinates of the JLMarker on the map
     */
    @NonFinal
    JLLatLng latLng;
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

    @NonFinal
    JLIcon icon;

    @Builder
    public JLMarker(String id, String text, JLLatLng latLng, JLTransporter<?> transport) {
        super(transport);
        this.id = id;
        this.text = text;
        this.latLng = latLng;
    }

    @Override
    public void update(Object... params) {
        super.update(params);
        if (params != null && params.length > 0
                && String.valueOf(params[0]).equals(
                JLAction.MOVE_END.getJsEventName())
                && params[1] != null) {
            latLng = (JLLatLng) params[1];
        }
    }

    @Override
    public JLMarker self() {
        return this;
    }

    /**
     * Changes the marker position to the given point.
     *
     * @param latLng new position of the marker.
     * @return the current instance of JLMarker.
     */
    public JLMarker setLatLng(JLLatLng latLng) {
        getTransport().execute(new JLTransport(this,
                String.format("this.%s.setLatLng([%f, %f]);", getId(), latLng.getLat(), latLng.getLng())));
        this.latLng = latLng;
        return this;
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
    public JLMarker setZIndexOffset(int offset) {
        getTransport().execute(new JLTransport(this,
                String.format("this.%s.setZIndexOffset(%d);", getId(), offset)));
        this.zIndexOffset = offset;
        return this;
    }

    /**
     * Changes the marker opacity.
     * Read more <a href="https://leafletjs.com/reference.html#marker-opacity">here</a>!
     *
     * @param opacity value between 0.0 and 1.0.
     * @return the current instance of JLMarker.
     */
    public JLMarker setOpacity(double opacity) {
        getTransport().execute(new JLTransport(this,
                String.format("this.%s.setOpacity(%f);", getId(), opacity)));
        this.opacity = opacity;
        return this;
    }

    /**
     * Changes the marker icon.
     *
     * @param icon new icon of the marker.
     *             Read more <a href="https://leafletjs.com/reference.html#marker-seticon">here</a>!
     * @return the current instance of JLMarker.
     */
    public JLMarker setIcon(JLIcon icon) {
        getTransport().execute(new JLTransport(this,
                String.format("this.%s.setIcon(%s);", getId(), icon)));
        this.icon = icon;
        return this;
    }
}
