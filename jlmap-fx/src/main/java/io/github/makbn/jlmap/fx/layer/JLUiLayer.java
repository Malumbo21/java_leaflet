package io.github.makbn.jlmap.fx.layer;

import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLTransporter;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletUILayerInt;
import io.github.makbn.jlmap.model.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Represents the UI layer on Leaflet map.
 *
 * @author Matt Akbarian  (@makbn)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLUiLayer extends JLLayer implements LeafletUILayerInt {
    JLTransporter transporter;

    public JLUiLayer(JLWebEngine<Object> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
        this.transporter = () -> transport -> {
            // NO-OP
            return null;
        };
    }

    /**
     * Add a {{@link JLMarker}} to the map with given text as content and {{@link JLLatLng}} as position.
     *
     * @param latLng position on the map.
     * @param text   content of the related popup if available!
     * @return the instance of added {{@link JLMarker}} on the map.
     */
    @Override
    public JLMarker addMarker(JLLatLng latLng, String text, boolean draggable) {
        String result = engine.executeScript(String.format("addMarker(%f, %f, '%s', %b)", latLng.getLat(), latLng.getLng(), text, draggable))
                .toString();

        JLMarker marker = new JLMarker(result, text, latLng, transporter);
        callbackHandler.addJLObject(result, marker);
        return marker;
    }

    /**
     * Remove a {{@link JLMarker}} from the map.
     *
     * @param id of the marker for removing.
     * @return {{@link Boolean#TRUE}} if removed successfully.
     */
    @Override
    public boolean removeMarker(String id) {
        String result = engine.executeScript(String.format("removeMarker(%s)", id)).toString();
        callbackHandler.remove(JLMarker.class, String.valueOf(id));
        return Boolean.parseBoolean(result);
    }

    /**
     * Add a {{@link JLPopup}} to the map with given text as content and
     * {@link JLLatLng} as position.
     *
     * @param latLng  position on the map.
     * @param text    content of the popup.
     * @param options see {{@link JLOptions}} for customizing
     * @return the instance of added {{@link JLPopup}} on the map.
     */
    @Override
    public JLPopup addPopup(JLLatLng latLng, String text, JLOptions options) {
        String result = engine.executeScript(String.format("addPopup(%f, %f, \"%s\", %b , %b)", latLng.getLat(), latLng.getLng(), text, options.isCloseButton(), options.isAutoClose()))
                .toString();

        return new JLPopup(result, text, latLng, options, transporter);
    }

    /**
     * Add popup with {{@link JLOptions#DEFAULT}} options
     *
     * @see JLUiLayer#addPopup(JLLatLng, String, JLOptions)
     */
    @Override
    public JLPopup addPopup(JLLatLng latLng, String text) {
        return addPopup(latLng, text, JLOptions.DEFAULT);
    }

    /**
     * Remove a {@link JLPopup} from the map.
     *
     * @param id of the marker for removing.
     * @return true if removed successfully.
     */
    @Override
    public boolean removePopup(String id) {
        String result = engine.executeScript(String.format("removePopup(%s)", id))
                .toString();
        return Boolean.parseBoolean(result);
    }

    @Override
    public JLImageOverlay addImage(JLBounds bounds, String imageUrl, JLOptions options) {
        throw new UnsupportedOperationException();
    }
}
