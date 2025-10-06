package io.github.makbn.jlmap.model;

import io.github.makbn.jlmap.engine.JLServerToClientTransporter;
import io.github.makbn.jlmap.listener.OnJLActionListener;

/**
 * Represents basic object classes for interacting with Leaflet
 *
 * @author Matt Akbarian  (@makbn)
 */

public interface JLObject<T extends JLObject<T>> {


    OnJLActionListener<T> getOnActionListener();

    void setOnActionListener(OnJLActionListener<T> listener);

    String getJLId();

    T self();

    JLServerToClientTransporter<?> getTransport();

    /**
     * By default, marker images zIndex is set automatically based on its latitude. Use this option if you want
     * to put the marker on top of all others (or below), specifying a high value like 1000 (or high
     * negative value, respectively).
     * Read more <a href="https://leafletjs.com/reference.html#marker-zindexoffset">here</a>!
     *
     * @param offset new zIndex offset of the marker.
     * @return the current instance of JLMarker.
     */
    T setZIndexOffset(int offset);

    /**
     * Changes the marker opacity.
     * Read more <a href="https://leafletjs.com/reference.html#marker-opacity">here</a>!
     *
     * @param opacity value between 0.0 and 1.0.
     * @return the current instance of JLMarker.
     */
    T setJLObjectOpacity(double opacity);
}
