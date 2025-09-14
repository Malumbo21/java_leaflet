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
                .trim().replaceAll("( +|\r|\n)", " "))
                .contains("let circle = L.circle([10.200000, 20.100000]")
                .contains("radius: 13.000000")
                .contains("fillOpacity: 0.2")
                .contains("draggable: false")
                .contains("closeButton: true")
                .contains("smoothFactor: 1.0")
                .contains("weight: 3")
                .contains("fill: true")
                .contains("opacity: 1.0")
                .contains("stroke: true")
                .contains("autoClose: true")
                .contains("circle.uuid = 'circle'")
                .contains("this.circle.on('move'")
                .contains("this.circle.on('add',")
                .contains("this.circle.on('remove',")
                .contains("circle.addTo(this.map)");
    }
}
