package io.github.makbn.jlmap.listener;

import io.github.makbn.jlmap.JLMapController;
import io.github.makbn.jlmap.listener.event.Event;
import lombok.NonNull;


public interface OnJLMapViewListener extends OnJLActionListener<JLMapController> {

    /**
     * called after the map is fully loaded
     *
     * @param mapView loaded map
     */
    void mapLoadedSuccessfully(@NonNull JLMapController mapView);

    /**
     * called after the map got an exception on loading
     */
    default void mapFailed() {

    }

    void onActionReceived(Event event);

    @Override
    default void onAction(JLMapController source, Event event) {
        onActionReceived(event);
    }
}
