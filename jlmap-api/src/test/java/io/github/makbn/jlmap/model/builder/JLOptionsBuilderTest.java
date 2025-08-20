package io.github.makbn.jlmap.model.builder;

import io.github.makbn.jlmap.model.JLOptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class JLOptionsBuilderTest {

    @Test
    void optionBuilder_default_shouldGenerateTheMap() {
        JLOptionsBuilder builder = new JLOptionsBuilder();
        builder.setOption(JLOptions.DEFAULT);

        assertThat(builder.build()).isNotNull()
                .isNotEmpty()
                .satisfies(actualMap -> {
                    assertThat(actualMap).containsEntry("autoClose", true);
                    assertThat(actualMap).containsEntry("closeButton", true);
                    assertThat(actualMap).containsEntry("draggable", false);
                    assertThat(actualMap).containsEntry("fill", true);
                    assertThat(actualMap).containsEntry("fillOpacity", 0.2).hasEntrySatisfying("fillOpacity", value ->
                            assertThat((Double) value).isCloseTo(0.2, within(0.00001)));
                    assertThat(actualMap).containsEntry("opacity", 1.0).hasEntrySatisfying("opacity", value ->
                            assertThat((Double) value).isCloseTo(1.0, within(0.00001)));
                    assertThat(actualMap).containsEntry("smoothFactor", 1.0).hasEntrySatisfying("smoothFactor", value ->
                            assertThat((Double) value).isCloseTo(1.0, within(0.00001)));
                    assertThat(actualMap).containsEntry("stroke", true);
                    assertThat(actualMap).containsEntry("weight", 3);
                });
    }
}
