package io.github.makbn.jlmap.vaadin.layer;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.JLMapCallbackHandler;
import io.github.makbn.jlmap.engine.JLWebEngine;
import io.github.makbn.jlmap.layer.leaflet.LeafletControlLayerInt;
import io.github.makbn.jlmap.model.JLBounds;
import io.github.makbn.jlmap.model.JLLatLng;

/**
 * Represents the Control layer on Leaflet map.
 *
 * @author Mehdi Akbarian Rastaghi (@makbn)
 */
public class JLVaadinControlLayer extends JLVaadinLayer implements LeafletControlLayerInt {
    public JLVaadinControlLayer(JLWebEngine<PendingJavaScriptResult> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
    }

    @Override
    public void zoomIn(int delta) {
        engine.executeScript(String.format("this.map.zoomIn(%d)", delta));
    }

    @Override
    public void zoomOut(int delta) {
        engine.executeScript(String.format("this.map.zoomOut(%d)", delta));
    }

    @Override
    public void setZoom(int level) {
        engine.executeScript(String.format("this.map.setZoom(%d)", level));
    }

    @Override
    public void setZoomAround(JLLatLng latLng, int zoom) {
        engine.executeScript(
                String.format("this.map.setZoomAround(L.latLng(%f, %f), %d)",
                        latLng.getLat(), latLng.getLng(), zoom));
    }

    @Override
    public void fitBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.fitBounds(%s)",
                bounds.toString()));
    }

    @Override
    public void fitWorld() {
        engine.executeScript("this.map.fitWorld()");
    }

    @Override
    public void panTo(JLLatLng latLng) {
        engine.executeScript(String.format("this.map.panTo(L.latLng(%f, %f))",
                latLng.getLat(), latLng.getLng()));
    }

    @Override
    public void flyTo(JLLatLng latLng, int zoom) {
        engine.executeScript(
                String.format("this.map.flyTo(L.latLng(%f, %f), %d)",
                        latLng.getLat(), latLng.getLng(), zoom));
    }

    @Override
    public void flyToBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.flyToBounds(%s)",
                bounds.toString()));
    }

    @Override
    public void setMaxBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.setMaxBounds(%s)",
                bounds.toString()));
    }

    @Override
    public void setMinZoom(int zoom) {
        engine.executeScript(String.format("this.map.setMinZoom(%d)", zoom));
    }

    @Override
    public void setMaxZoom(int zoom) {
        engine.executeScript(String.format("this.map.setMaxZoom(%d)", zoom));
    }

    @Override
    public void panInsideBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.panInsideBounds(%s)",
                bounds.toString()));
    }

    @Override
    public void panInside(JLLatLng latLng) {
        engine.executeScript(
                String.format("this.map.panInside(L.latLng(%f, %f))",
                        latLng.getLat(), latLng.getLng()));
    }
}
