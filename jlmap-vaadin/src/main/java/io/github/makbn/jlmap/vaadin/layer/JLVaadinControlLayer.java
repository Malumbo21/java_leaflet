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
 * @author Matt Akbarian  (@makbn)
 */
public class JLVaadinControlLayer extends JLVaadinLayer implements LeafletControlLayerInt {
    public JLVaadinControlLayer(JLWebEngine<PendingJavaScriptResult> engine, JLMapCallbackHandler callbackHandler) {
        super(engine, callbackHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoomIn(int delta) {
        engine.executeScript(String.format("this.map.zoomIn(%d)", delta));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoomOut(int delta) {
        engine.executeScript(String.format("this.map.zoomOut(%d)", delta));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setZoom(int level) {
        engine.executeScript(String.format("this.map.setZoom(%d)", level));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setZoomAround(JLLatLng latLng, int zoom) {
        engine.executeScript(
                String.format("this.map.setZoomAround(L.latLng(%f, %f), %d)",
                        latLng.getLat(), latLng.getLng(), zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fitBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.fitBounds(%s)",
                bounds.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fitWorld() {
        engine.executeScript("this.map.fitWorld()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void panTo(JLLatLng latLng) {
        engine.executeScript(String.format("this.map.panTo(L.latLng(%f, %f))",
                latLng.getLat(), latLng.getLng()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flyTo(JLLatLng latLng, int zoom) {
        engine.executeScript(
                String.format("this.map.flyTo(L.latLng(%f, %f), %d)",
                        latLng.getLat(), latLng.getLng(), zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flyToBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.flyToBounds(%s)",
                bounds.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.setMaxBounds(%s)",
                bounds.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinZoom(int zoom) {
        engine.executeScript(String.format("this.map.setMinZoom(%d)", zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxZoom(int zoom) {
        engine.executeScript(String.format("this.map.setMaxZoom(%d)", zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void panInsideBounds(JLBounds bounds) {
        engine.executeScript(String.format("this.map.panInsideBounds(%s)",
                bounds.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void panInside(JLLatLng latLng) {
        engine.executeScript(
                String.format("this.map.panInside(L.latLng(%f, %f))",
                        latLng.getLat(), latLng.getLng()));
    }
}
