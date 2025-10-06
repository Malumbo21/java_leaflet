package io.github.makbn.jlmap.element.menu;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JLMenuItem.
 * Tests the menu item functionality.
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
class JLMenuItemTest {

    // === Factory Method Tests ===

    @Test
    void of_withIdAndText_shouldCreateMenuItem() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // Then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo("edit");
        assertThat(item.getText()).isEqualTo("Edit");
        assertThat(item.getIcon()).isNull();
        assertThat(item.isVisible()).isTrue();
        assertThat(item.isEnabled()).isTrue();
    }

    @Test
    void of_withIdTextAndIcon_shouldCreateMenuItemWithIcon() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "Edit", "https://example.com/icon.png");

        // Then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo("edit");
        assertThat(item.getText()).isEqualTo("Edit");
        assertThat(item.getIcon()).isEqualTo("https://example.com/icon.png");
    }

    @Test
    void of_withNullId_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> JLMenuItem.of(null, "Edit"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_withNullText_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> JLMenuItem.of("edit", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_withBlankId_shouldAcceptBlankId() {
        // When
        JLMenuItem item = JLMenuItem.of("", "Edit");

        // Then - Current implementation accepts blank ID
        // This documents the current behavior
        assertThat(item.getId()).isEmpty();
    }

    @Test
    void of_withBlankText_shouldAcceptBlankText() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "");

        // Then - Current implementation accepts blank text
        // This documents the current behavior
        assertThat(item.getText()).isEmpty();
    }

    @Test
    void of_withWhitespaceId_shouldAcceptWhitespaceId() {
        // When
        JLMenuItem item = JLMenuItem.of("   ", "Edit");

        // Then - Current implementation accepts whitespace ID
        // This documents the current behavior
        assertThat(item.getId()).isEqualTo("   ");
    }

    @Test
    void of_withWhitespaceText_shouldAcceptWhitespaceText() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "   ");

        // Then - Current implementation accepts whitespace text
        // This documents the current behavior
        assertThat(item.getText()).isEqualTo("   ");
    }

    // === Visibility Tests ===

    @Test
    void isVisible_byDefault_shouldReturnTrue() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When/Then
        assertThat(item.isVisible()).isTrue();
    }

    @Test
    void builder_withVisibleFalse_shouldCreateHiddenItem() {
        // When
        JLMenuItem item = JLMenuItem.builder()
                .id("edit")
                .text("Edit")
                .visible(false)
                .build();

        // Then
        assertThat(item.isVisible()).isFalse();
    }

    @Test
    void toBuilder_withVisibleFalse_shouldCreateHiddenItem() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When
        JLMenuItem modifiedItem = item.toBuilder()
                .visible(false)
                .build();

        // Then
        assertThat(modifiedItem.isVisible()).isFalse();
        assertThat(item.isVisible()).isTrue(); // Original unchanged
    }

    // === Enabled Tests ===

    @Test
    void isEnabled_byDefault_shouldReturnTrue() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When/Then
        assertThat(item.isEnabled()).isTrue();
    }

    @Test
    void builder_withEnabledFalse_shouldCreateDisabledItem() {
        // When
        JLMenuItem item = JLMenuItem.builder()
                .id("edit")
                .text("Edit")
                .enabled(false)
                .build();

        // Then
        assertThat(item.isEnabled()).isFalse();
    }

    @Test
    void toBuilder_withEnabledFalse_shouldCreateDisabledItem() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When
        JLMenuItem modifiedItem = item.toBuilder()
                .enabled(false)
                .build();

        // Then
        assertThat(modifiedItem.isEnabled()).isFalse();
        assertThat(item.isEnabled()).isTrue(); // Original unchanged
    }

    // === Icon Tests ===

    @Test
    void toBuilder_shouldAllowUpdatingIcon() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When
        JLMenuItem modifiedItem = item.toBuilder()
                .icon("https://example.com/new-icon.png")
                .build();

        // Then
        assertThat(modifiedItem.getIcon()).isEqualTo("https://example.com/new-icon.png");
        assertThat(item.getIcon()).isNull(); // Original unchanged
    }

    @Test
    void toBuilder_withNullIcon_shouldAcceptNullIcon() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit", "old-icon.png");

        // When
        JLMenuItem modifiedItem = item.toBuilder()
                .icon(null)
                .build();

        // Then
        assertThat(modifiedItem.getIcon()).isNull();
    }

    // === Text Tests ===

    @Test
    void toBuilder_shouldAllowUpdatingText() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When
        JLMenuItem modifiedItem = item.toBuilder()
                .text("Edit Updated")
                .build();

        // Then
        assertThat(modifiedItem.getText()).isEqualTo("Edit Updated");
        assertThat(item.getText()).isEqualTo("Edit"); // Original unchanged
    }

    // === Equals and HashCode Tests ===

    @Test
    void equals_withAllFieldsEqual_shouldReturnTrue() {
        // Given
        JLMenuItem item1 = JLMenuItem.of("edit", "Edit");
        JLMenuItem item2 = JLMenuItem.of("edit", "Edit");

        // When/Then
        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void equals_withDifferentText_shouldReturnFalse() {
        // Given
        JLMenuItem item1 = JLMenuItem.of("edit", "Edit");
        JLMenuItem item2 = JLMenuItem.of("edit", "Edit Different");

        // When/Then - @Value uses all fields for equals
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void equals_withDifferentId_shouldReturnFalse() {
        // Given
        JLMenuItem item1 = JLMenuItem.of("edit", "Edit");
        JLMenuItem item2 = JLMenuItem.of("delete", "Edit");

        // When/Then
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void hashCode_withAllFieldsEqual_shouldReturnSameHashCode() {
        // Given
        JLMenuItem item1 = JLMenuItem.of("edit", "Edit");
        JLMenuItem item2 = JLMenuItem.of("edit", "Edit");

        // When/Then
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    // === toString Tests ===

    @Test
    void toString_shouldContainIdAndText() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit Marker");

        // When
        String result = item.toString();

        // Then
        assertThat(result).contains("edit");
        assertThat(result).contains("Edit Marker");
    }

    // === Immutability Tests ===

    @Test
    void menuItem_shouldBeImmutable() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");
        String originalId = item.getId();
        String originalText = item.getText();

        // When - Create modified version using toBuilder
        JLMenuItem modifiedItem = item.toBuilder()
                .text("Updated")
                .icon("new-icon.png")
                .visible(false)
                .enabled(false)
                .build();

        // Then - Original should be unchanged
        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getText()).isEqualTo(originalText);
        assertThat(item.isVisible()).isTrue();
        assertThat(item.isEnabled()).isTrue();
        assertThat(item.getIcon()).isNull();

        // And modified should have new values
        assertThat(modifiedItem.getText()).isEqualTo("Updated");
        assertThat(modifiedItem.getIcon()).isEqualTo("new-icon.png");
        assertThat(modifiedItem.isVisible()).isFalse();
        assertThat(modifiedItem.isEnabled()).isFalse();
    }

    // === Null Parameter Tests for @NonNull Validation (Three-param method) ===

    @Test
    void of_withNullIdAndIcon_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> JLMenuItem.of(null, "Edit", "icon.png"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_withNullTextAndIcon_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> JLMenuItem.of("edit", null, "icon.png"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_withNullIcon_shouldAcceptNullIcon() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "Edit", null);

        // Then
        assertThat(item.getIcon()).isNull();
    }

    @Test
    void of_withBlankIcon_shouldAcceptBlankIcon() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "Edit", "");

        // Then
        assertThat(item.getIcon()).isEmpty();
    }

    @Test
    void of_withWhitespaceIcon_shouldAcceptWhitespaceIcon() {
        // When
        JLMenuItem item = JLMenuItem.of("edit", "Edit", "   ");

        // Then
        assertThat(item.getIcon()).isEqualTo("   ");
    }
}
