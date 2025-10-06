package io.github.makbn.jlmap.element.menu;

import io.github.makbn.jlmap.listener.JLAction;
import io.github.makbn.jlmap.listener.event.ContextMenuEvent;
import io.github.makbn.jlmap.listener.event.Event;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMarker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JLContextMenu.
 * Tests the context menu functionality for JL objects.
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
class JLContextMenuTest {

    private JLMarker owner;
    private JLContextMenu<JLMarker> contextMenu;

    @BeforeEach
    void setUp() {
        owner = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test Marker")
                .transport(null)
                .build();
        contextMenu = new JLContextMenu<>(owner);
    }

    // === Constructor Tests ===

    @Test
    void constructor_withValidOwner_shouldInitializeSuccessfully() {
        // When
        JLContextMenu<JLMarker> menu = new JLContextMenu<>(owner);

        // Then
        assertThat(menu).isNotNull();
        assertThat(menu.getItemCount()).isZero();
        assertThat(menu.isEnabled()).isTrue();
    }

    @Test
    void constructor_withNullOwner_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> new JLContextMenu<>(null))
                .isInstanceOf(NullPointerException.class);
    }

    // === Add Item Tests ===

    @Test
    void addItem_withTextOnly_shouldCreateItemWithGeneratedId() {
        // When
        contextMenu.addItem("Edit Marker");

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("editmarker")).isNotNull();
        assertThat(contextMenu.getItem("editmarker").getText()).isEqualTo("Edit Marker");
    }

    @Test
    void addItem_withIdAndText_shouldCreateItemWithSpecifiedId() {
        // When
        contextMenu.addItem("edit", "Edit Marker");

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("edit")).isNotNull();
        assertThat(contextMenu.getItem("edit").getText()).isEqualTo("Edit Marker");
    }

    @Test
    void addItem_withIdTextAndIcon_shouldCreateItemWithAllProperties() {
        // When
        contextMenu.addItem("edit", "Edit Marker", "https://example.com/icon.png");

        // Then
        JLMenuItem item = contextMenu.getItem("edit");
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo("edit");
        assertThat(item.getText()).isEqualTo("Edit Marker");
        assertThat(item.getIcon()).isEqualTo("https://example.com/icon.png");
    }

    @Test
    void addItem_withMenuItem_shouldAddMenuItem() {
        // Given
        JLMenuItem menuItem = JLMenuItem.of("custom", "Custom Item", "icon.png");

        // When
        contextMenu.addItem(menuItem);

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("custom")).isEqualTo(menuItem);
    }

    @Test
    void addItem_withDuplicateId_shouldReplaceExistingItem() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        contextMenu.addItem("edit", "Edit Updated");

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("edit").getText()).isEqualTo("Edit Updated");
    }

    @Test
    void addItem_withNullText_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem((String) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addItem_shouldReturnContextMenuForChaining() {
        // When
        JLContextMenu<JLMarker> result = contextMenu.addItem("edit", "Edit");

        // Then
        assertThat(result).isSameAs(contextMenu);
    }

    // === Remove Item Tests ===

    @Test
    void removeItem_withExistingId_shouldRemoveItem() {
        // Given
        contextMenu.addItem("edit", "Edit");
        contextMenu.addItem("delete", "Delete");

        // When
        contextMenu.removeItem("edit");

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("edit")).isNull();
        assertThat(contextMenu.getItem("delete")).isNotNull();
    }

    @Test
    void removeItem_withNonExistingId_shouldDoNothing() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        contextMenu.removeItem("nonexistent");

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
    }

    @Test
    void removeItem_shouldReturnContextMenuForChaining() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        JLContextMenu<JLMarker> result = contextMenu.removeItem("edit");

        // Then
        assertThat(result).isSameAs(contextMenu);
    }

    // === Clear Items Tests ===

    @Test
    void clearItems_withMultipleItems_shouldRemoveAllItems() {
        // Given
        contextMenu.addItem("edit", "Edit");
        contextMenu.addItem("delete", "Delete");
        contextMenu.addItem("info", "Info");

        // When
        contextMenu.clearItems();

        // Then
        assertThat(contextMenu.getItemCount()).isZero();
    }

    @Test
    void clearItems_withNoItems_shouldDoNothing() {
        // When
        contextMenu.clearItems();

        // Then
        assertThat(contextMenu.getItemCount()).isZero();
    }

    @Test
    void clearItems_shouldReturnContextMenuForChaining() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        JLContextMenu<JLMarker> result = contextMenu.clearItems();

        // Then
        assertThat(result).isSameAs(contextMenu);
    }

    // === Get Item Tests ===

    @Test
    void getItem_withExistingId_shouldReturnItem() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        JLMenuItem item = contextMenu.getItem("edit");

        // Then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo("edit");
    }

    @Test
    void getItem_withNonExistingId_shouldReturnNull() {
        // When
        JLMenuItem item = contextMenu.getItem("nonexistent");

        // Then
        assertThat(item).isNull();
    }

    // === Update Item Tests ===

    @Test
    void updateItem_withExistingId_shouldUpdateItem() {
        // Given
        contextMenu.addItem("edit", "Edit");
        JLMenuItem updatedItem = JLMenuItem.of("edit", "Edit Updated", "new-icon.png");

        // When
        contextMenu.updateItem(updatedItem);

        // Then
        JLMenuItem item = contextMenu.getItem("edit");
        assertThat(item.getText()).isEqualTo("Edit Updated");
        assertThat(item.getIcon()).isEqualTo("new-icon.png");
    }

    @Test
    void updateItem_withNonExistingId_shouldAddItem() {
        // Given
        JLMenuItem newItem = JLMenuItem.of("new", "New Item");

        // When
        contextMenu.updateItem(newItem);

        // Then
        assertThat(contextMenu.getItemCount()).isEqualTo(1);
        assertThat(contextMenu.getItem("new")).isNotNull();
    }

    @Test
    void updateItem_shouldReturnContextMenuForChaining() {
        // Given
        JLMenuItem item = JLMenuItem.of("edit", "Edit");

        // When
        JLContextMenu<JLMarker> result = contextMenu.updateItem(item);

        // Then
        assertThat(result).isSameAs(contextMenu);
    }

    // === Get Items Tests ===

    @Test
    void getItems_shouldReturnAllItems() {
        // Given
        contextMenu.addItem("edit", "Edit");
        contextMenu.addItem("delete", "Delete");
        contextMenu.addItem("info", "Info");

        // When
        Collection<JLMenuItem> items = contextMenu.getItems();

        // Then
        assertThat(items).hasSize(3);
    }

    @Test
    void getItems_shouldReturnUnmodifiableCollection() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When
        Collection<JLMenuItem> items = contextMenu.getItems();

        // Then
        assertThatThrownBy(() -> items.add(JLMenuItem.of("new", "New")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // === Get Visible Items Tests ===

    @Test
    void getVisibleItems_shouldReturnOnlyVisibleItems() {
        // Given
        JLMenuItem visibleItem = JLMenuItem.builder()
                .id("edit")
                .text("Edit")
                .visible(true)
                .build();
        contextMenu.addItem(visibleItem);

        JLMenuItem hiddenItem = JLMenuItem.builder()
                .id("delete")
                .text("Delete")
                .visible(false)
                .build();
        contextMenu.addItem(hiddenItem);

        // When
        Collection<JLMenuItem> visibleItems = contextMenu.getVisibleItems();

        // Then
        assertThat(visibleItems).hasSize(1);
        assertThat(visibleItems).contains(visibleItem);
        assertThat(visibleItems).doesNotContain(hiddenItem);
    }

    // === Has Visible Items Tests ===

    @Test
    void hasVisibleItems_withVisibleItems_shouldReturnTrue() {
        // Given
        contextMenu.addItem("edit", "Edit");

        // When/Then
        assertThat(contextMenu.hasVisibleItems()).isTrue();
    }

    @Test
    void hasVisibleItems_withOnlyHiddenItems_shouldReturnFalse() {
        // Given
        JLMenuItem hiddenItem = JLMenuItem.builder()
                .id("edit")
                .text("Edit")
                .visible(false)
                .build();
        contextMenu.addItem(hiddenItem);

        // When/Then
        assertThat(contextMenu.hasVisibleItems()).isFalse();
    }

    @Test
    void hasVisibleItems_withNoItems_shouldReturnFalse() {
        // When/Then
        assertThat(contextMenu.hasVisibleItems()).isFalse();
    }

    // === Item Count Tests ===

    @Test
    void getItemCount_withMultipleItems_shouldReturnCorrectCount() {
        // Given
        contextMenu.addItem("edit", "Edit");
        contextMenu.addItem("delete", "Delete");
        contextMenu.addItem("info", "Info");

        // When/Then
        assertThat(contextMenu.getItemCount()).isEqualTo(3);
    }

    @Test
    void getItemCount_withNoItems_shouldReturnZero() {
        // When/Then
        assertThat(contextMenu.getItemCount()).isZero();
    }

    @Test
    void getVisibleItemCount_shouldReturnCountOfVisibleItems() {
        // Given
        contextMenu.addItem("edit", "Edit");
        JLMenuItem hiddenItem = JLMenuItem.builder()
                .id("delete")
                .text("Delete")
                .visible(false)
                .build();
        contextMenu.addItem(hiddenItem);

        // When/Then
        assertThat(contextMenu.getVisibleItemCount()).isEqualTo(1);
    }

    // === Enabled Tests ===

    @Test
    void isEnabled_byDefault_shouldReturnTrue() {
        // When/Then
        assertThat(contextMenu.isEnabled()).isTrue();
    }

    @Test
    void setEnabled_withFalse_shouldDisableMenu() {
        // When
        contextMenu.setEnabled(false);

        // Then
        assertThat(contextMenu.isEnabled()).isFalse();
    }

    @Test
    void setEnabled_withTrue_shouldEnableMenu() {
        // Given
        contextMenu.setEnabled(false);

        // When
        contextMenu.setEnabled(true);

        // Then
        assertThat(contextMenu.isEnabled()).isTrue();
    }

    // === Listener Tests ===

    @Test
    void setOnMenuItemListener_shouldSetListener() {
        // Given
        OnJLContextMenuItemListener listener = item -> {
        };

        // When
        contextMenu.setOnMenuItemListener(listener);

        // Then
        assertThat(contextMenu.getOnMenuItemListener()).isSameAs(listener);
    }

    @Test
    void setOnMenuItemListener_shouldReturnContextMenuForChaining() {
        // Given
        OnJLContextMenuItemListener listener = item -> {
        };

        // When
        contextMenu.setOnMenuItemListener(listener);

        // Then
        assertThat(contextMenu.getOnMenuItemListener()).isSameAs(listener);
    }

    @Test
    void handleMenuItemSelection_withListener_shouldInvokeListener() {
        // Given
        final boolean[] listenerCalled = {false};
        contextMenu.setOnMenuItemListener(item -> listenerCalled[0] = true);
        JLMenuItem menuItem = JLMenuItem.of("test", "Test");

        // When
        contextMenu.handleMenuItemSelection(menuItem);

        // Then
        assertThat(listenerCalled[0]).isTrue();
    }

    @Test
    void handleMenuItemSelection_withoutListener_shouldNotThrowException() {
        // Given
        JLMenuItem menuItem = JLMenuItem.of("test", "Test");

        // When/Then - Should not throw exception
        contextMenu.handleMenuItemSelection(menuItem);
    }

    // === Method Chaining Tests ===

    @Test
    void contextMenu_shouldSupportMethodChaining() {
        // When
        JLContextMenu<JLMarker> result = contextMenu
                .addItem("edit", "Edit")
                .addItem("delete", "Delete")
                .addItem("info", "Info")
                .setOnMenuItemListener(item -> {
                });

        contextMenu.setEnabled(true);

        // Then
        assertThat(result).isSameAs(contextMenu);
        assertThat(contextMenu.getItemCount()).isEqualTo(3);
        assertThat(contextMenu.getOnMenuItemListener()).isNotNull();
        assertThat(contextMenu.isEnabled()).isTrue();
    }

    // === Null Parameter Tests for @NonNull Validation ===

    @Test
    void addItem_withNullId_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem(null, "Edit"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addItem_withNullIdAndIcon_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem(null, "Edit", "icon.png"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addItem_withNullTextInTwoParamMethod_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem("edit", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addItem_withNullTextInThreeParamMethod_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem("edit", null, "icon.png"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addItem_withNullMenuItem_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.addItem((JLMenuItem) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void removeItem_withNullId_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.removeItem(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getItem_withNullId_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.getItem(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateItem_withNullMenuItem_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.updateItem(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleMenuItemSelection_withNullMenuItem_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.handleMenuItemSelection(null))
                .isInstanceOf(NullPointerException.class);
    }

    // === Event Handling Tests ===

    @Test
    void handleContextMenuEvent_withListenerAndEvent_shouldInvokeOwnerListener() {
        // Given
        final boolean[] listenerCalled = {false};
        owner.setOnActionListener((obj, event) -> listenerCalled[0] = true);
        Event testEvent = new ContextMenuEvent(
                JLAction.CONTEXT_MENU,
                new JLLatLng(51.5, -0.09),
                null,
                100.0,
                200.0
        );

        // When
        contextMenu.handleContextMenuEvent(testEvent);

        // Then
        assertThat(listenerCalled[0]).isTrue();
    }

    @Test
    void handleContextMenuEvent_withoutListener_shouldNotThrowException() {
        // Given
        Event testEvent = new ContextMenuEvent(
                JLAction.CONTEXT_MENU,
                new JLLatLng(51.5, -0.09),
                null,
                100.0,
                200.0
        );

        // When/Then - Should not throw exception
        contextMenu.handleContextMenuEvent(testEvent);
    }

    @Test
    void handleContextMenuEvent_withNullEvent_shouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> contextMenu.handleContextMenuEvent(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleContextMenuEvent_withListenerReceivesCorrectEvent() {
        // Given
        final Event[] receivedEvent = {null};
        owner.setOnActionListener((obj, event) -> receivedEvent[0] = event);
        Event testEvent = new ContextMenuEvent(
                JLAction.CONTEXT_MENU,
                new JLLatLng(51.5, -0.09),
                null,
                100.0,
                200.0
        );

        // When
        contextMenu.handleContextMenuEvent(testEvent);

        // Then
        assertThat(receivedEvent[0]).isEqualTo(testEvent);
    }

    @Test
    void handleContextMenuEvent_withListenerReceivesCorrectOwner() {
        // Given
        final JLMarker[] receivedOwner = {null};
        owner.setOnActionListener((obj, event) -> receivedOwner[0] = obj);
        Event testEvent = new ContextMenuEvent(
                JLAction.CONTEXT_MENU,
                new JLLatLng(51.5, -0.09),
                null,
                100.0,
                200.0
        );

        // When
        contextMenu.handleContextMenuEvent(testEvent);

        // Then
        assertThat(receivedOwner[0]).isEqualTo(owner);
    }
}
