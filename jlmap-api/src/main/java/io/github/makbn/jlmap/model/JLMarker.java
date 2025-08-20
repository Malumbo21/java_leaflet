package io.github.makbn.jlmap.model;


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
 * @author Mehdi Akbarian Rastaghi (@makbn)
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

    @Builder
    public JLMarker(String id, String text, JLLatLng latLng, JLTransporter transport) {
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
}
