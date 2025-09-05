package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.model.JLOptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JLCircleBuilderTest {

    @Test
    void builder_withOptions_buildCircle() {
        var elementUniqueName = "circle";

        var circleBuilder = new JLCircleBuilder()
                .setUuid(elementUniqueName)
                .setLat(10.2)
                .setLng(20.1)
                .setRadius(13)
                .setTransporter(() -> transport -> {
                    return null;
                })
                .withOptions(JLOptions.DEFAULT)
                .withCallbacks(jlCallbackBuilder -> {
                    jlCallbackBuilder.on(JLAction.MOVE);
                    jlCallbackBuilder.on(JLAction.ADD);
                    jlCallbackBuilder.on(JLAction.REMOVE);
                });

        assertThat(circleBuilder.buildJsElement()
                .trim().replaceAll("( +|\r|\n)", " ")).isEqualTo("""
                let circle = L.circle([10.200000, 20.100000], { radius: 13.000000, fillOpacity: 0.2, draggable: false, closeButton: true, smoothFactor: 1.0, weight: 3, fill: true, opacity: 1.0, stroke: true, autoClose: true });
                this.circle = circle;
                circle.uuid = 'circle';
                // callback start
                this.circle.on('move', e => this.jlMapElement.$server.eventHandler('move', 'jlcircle', e.target.uuid, this.map.getZoom(),
                 JSON.stringify(e.target.getLatLng() ? { "lat": e.target.getLatLng().lat, "lng": e.target.getLatLng().lng } : {"lat": this.map.getCenter().lat, "lng": this.map.getCenter().lng}),
                 JSON.stringify(this.map.getBounds())
                ));
                
                this.circle.on('add', e => this.jlMapElement.$server.eventHandler('add', 'jlcircle', e.target.uuid, this.map.getZoom(),
                 JSON.stringify(e.target.getLatLng() ? { "lat": e.target.getLatLng().lat, "lng": e.target.getLatLng().lng } : {"lat": this.map.getCenter().lat, "lng": this.map.getCenter().lng}),
                 JSON.stringify(this.map.getBounds())
                ));
                
                this.circle.on('remove', e => this.jlMapElement.$server.eventHandler('remove', 'jlcircle', e.target.uuid, this.map.getZoom(),
                 JSON.stringify(e.target.getLatLng() ? { "lat": e.target.getLatLng().lat, "lng": e.target.getLatLng().lng } : {"lat": this.map.getCenter().lat, "lng": this.map.getCenter().lng}),
                 JSON.stringify(this.map.getBounds())
                ));
                
                // callback end
                circle.addTo(this.map);
                """.trim().replaceAll("( +|\r|\n)", " "));

    }
}
