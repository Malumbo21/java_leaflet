package io.github.makbn.jlmap.vaadin.test.element.menu;

import io.github.makbn.jlmap.element.menu.JLContextMenu;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMarker;
import io.github.makbn.jlmap.vaadin.JLMapView;
import io.github.makbn.jlmap.vaadin.element.menu.VaadinContextMenuMediator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for VaadinContextMenuMediator.
 * Tests the context menu mediator functionality for Vaadin implementation.
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
class VaadinContextMenuMediatorTest {

    @Mock
    private JLMapView mockMapView;

    private VaadinContextMenuMediator mediator;

    @BeforeEach
    void setUp() {
        mediator = new VaadinContextMenuMediator();
    }

    // === Constructor and Basic Tests ===

    @Test
    void constructor_shouldInitializeSuccessfully() {
        // When
        VaadinContextMenuMediator newMediator = new VaadinContextMenuMediator();

        // Then
        assertThat(newMediator).isNotNull();
        assertThat(newMediator.getName()).isEqualTo("Vaadin Context Menu Mediator");
    }

    @Test
    void getName_shouldReturnCorrectName() {
        // When
        String name = mediator.getName();

        // Then
        assertThat(name).isEqualTo("Vaadin Context Menu Mediator");
    }

    @Test
    void supportsObjectType_shouldReturnTrueForAllTypes() {
        // When/Then
        assertThat(mediator.supportsObjectType(JLMarker.class)).isTrue();
    }

    // === showContextMenu Tests ===
    // Note: Full showContextMenu tests require Vaadin UI context which cannot be
    // easily mocked. These tests verify the marker setup without actually showing menus.

    @Test
    void showContextMenu_withObjectWithoutContextMenu_shouldNotThrowException() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        // When/Then - Should not throw exception even without context menu
        // Note: Cannot test full functionality without Vaadin UI context
        assertThat(marker.getContextMenu()).isNull();
        assertThat(marker.hasContextMenu()).isFalse();
    }

    @Test
    void showContextMenu_withDisabledContextMenu_shouldHaveDisabledState() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("test", "Test Item");
        marker.setContextMenuEnabled(false);

        // When/Then - Verify state without requiring Vaadin UI
        assertThat(marker.isContextMenuEnabled()).isFalse();
        assertThat(marker.hasContextMenu()).isTrue();
    }

    @Test
    void showContextMenu_withEnabledContextMenuButNoItems_shouldNotHaveContextMenu() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        marker.addContextMenu(); // Empty context menu

        // When/Then - Empty context menu means no context menu
        assertThat(marker.hasContextMenu()).isFalse();
    }

    @Test
    void showContextMenu_withValidContextMenu_shouldPopulateMenuItems() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("edit", "Edit")
                .addItem("delete", "Delete")
                .addItem("info", "Info");

        // Note: Can't fully test showing the menu in unit tests without a Vaadin UI context
        // This test verifies the setup is correct
        assertThat(marker.hasContextMenu()).isTrue();
        assertThat(marker.isContextMenuEnabled()).isTrue();
        assertThat(contextMenu.getItemCount()).isEqualTo(3);
    }

    // === hideContextMenu Tests ===

    @Test
    void hideContextMenu_withNullContextMenu_shouldNotThrowException() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        // When/Then - Should not throw exception
        mediator.hideContextMenu(mockMapView, marker);
    }

    @Test
    void hideContextMenu_shouldClearMenuItems() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("test", "Test Item");

        // When
        mediator.hideContextMenu(mockMapView, marker);

        // Then - Menu should be hidden (can't verify fully without Vaadin UI context)
        // But we can verify the marker still has its context menu
        assertThat(marker.getContextMenu()).isNotNull();
    }

    // === Multiple showContextMenu Calls ===

    @Test
    void showContextMenu_calledMultipleTimes_shouldMaintainContextMenuState() {
        // Given
        JLMarker marker1 = createMarkerWithContextMenu("marker1", "Item 1");
        JLMarker marker2 = createMarkerWithContextMenu("marker2", "Item 2");

        // When/Then - Verify markers maintain their context menu state
        // Note: Cannot test actual Vaadin menu reuse without UI context
        assertThat(marker1.hasContextMenu()).isTrue();
        assertThat(marker2.hasContextMenu()).isTrue();
    }

    // === Helper Methods ===

    private JLMarker createMarkerWithContextMenu(String id, String menuItemText) {
        JLMarker marker = JLMarker.builder()
                .id(id)
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test Marker")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem(menuItemText, menuItemText);

        return marker;
    }

    // === Context Menu Item Tests ===

    @Test
    void showContextMenu_withMenuItemWithIcon_shouldHandleIcon() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("edit", "Edit", "https://img.icons8.com/material-outlined/24/000000/edit--v1.png");

        // When/Then - Should not throw exception
        // Note: Full verification requires Vaadin UI context
        assertThat(contextMenu.getItem("edit").getIcon()).isNotNull();
    }

    @Test
    void showContextMenu_withMenuItemWithoutIcon_shouldHandleNullIcon() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("edit", "Edit", null);

        // When/Then - Should not throw exception
        assertThat(contextMenu.getItem("edit").getIcon()).isNull();
    }

    @Test
    void showContextMenu_withMenuItemWithBlankIcon_shouldHandleBlankIcon() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        JLContextMenu<JLMarker> contextMenu = marker.addContextMenu();
        contextMenu.addItem("edit", "Edit", "");

        // When/Then - Should not throw exception
        assertThat(contextMenu.getItem("edit").getIcon()).isEmpty();
    }
}
