package io.github.makbn.jlmap.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for JLGeoJsonOptions.
 *
 * @author Matt Akbarian (@makbn)
 */
class JLGeoJsonOptionsTest {

    @Test
    void shouldCreateDefaultOptions() {
        JLGeoJsonOptions options = JLGeoJsonOptions.DEFAULT;

        assertThat(options).isNotNull();
        assertThat(options.getStyleFunction()).isNull();
        assertThat(options.getFilter()).isNull();
    }

    @Test
    void shouldCreateOptionsWithStyleFunction() {
        JLGeoJsonOptions options = JLGeoJsonOptions.builder().styleFunction(properties -> JLOptions.builder().color(JLColor.RED).build())
                .build();

        assertThat(options.getStyleFunction()).isNotNull();

        // Test the function
        Map<String, Object> testProperties = Map.of("type", "test");
        JLOptions result = options.getStyleFunction().apply(List.of(testProperties));
        assertThat(result.getColor()).isEqualTo(JLColor.RED);
    }


    @Test
    void shouldCreateOptionsWithFilter() {
        JLGeoJsonOptions options = JLGeoJsonOptions.builder().filter((properties ->
                "active".equals(properties.get(0).get("status")))).build();

        assertThat(options.getFilter()).isNotNull();

        // Test the filter function
        Map<String, Object> activeProps = Map.of("status", "active");
        Map<String, Object> inactiveProps = Map.of("status", "inactive");

        assertThat(options.getFilter().test(List.of(activeProps))).isTrue();
        assertThat(options.getFilter().test(List.of(inactiveProps))).isFalse();
    }

    @Test
    void shouldBuildOptionsWithStyleAndFilter() {
        JLOptions baseStyle = JLOptions.builder()
                .color(JLColor.RED)
                .weight(5)
                .opacity(0.8)
                .build();

        JLGeoJsonOptions options = JLGeoJsonOptions.builder()
                .styleFunction(properties -> baseStyle)
                .filter(properties -> Boolean.TRUE.equals(properties.get(0).get("visible")))
                .build();

        assertThat(options.getStyleFunction().apply(Collections.emptyList())).isEqualTo(baseStyle);
        assertThat(options.getStyleFunction()).isNotNull();
        assertThat(options.getFilter()).isNotNull();
    }
}