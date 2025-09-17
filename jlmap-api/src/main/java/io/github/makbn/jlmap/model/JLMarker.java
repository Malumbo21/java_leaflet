package io.github.makbn.jlmap.model;


import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.engine.JLTransportRequest;
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
public final class JLMarker extends JLObjectBase<JLMarker> {
    /**
     * optional text for showing on created JLMarker tooltip.
     */
    String text;
    /**
     * Coordinates of the JLMarker on the map
     */
    @NonFinal
    JLLatLng latLng;

    @NonFinal
    JLIcon icon;

    @Builder
    public JLMarker(String id, String text, JLLatLng latLng, JLServerToClientTransporter<?> transport) {
        super(id, transport);
        this.text = text;
        this.latLng = latLng;
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
        getTransport().execute(JLTransportRequest.voidCall(this,
                String.format("this.%s.setLatLng([%f, %f]);", getJLId(), latLng.getLat(), latLng.getLng())));
        this.latLng = latLng;
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
        getTransport().execute(JLTransportRequest.voidCall(this,
                String.format("this.%s.setIcon(%s);", getJLId(), icon)));
        this.icon = icon;
        return this;
    }
}
