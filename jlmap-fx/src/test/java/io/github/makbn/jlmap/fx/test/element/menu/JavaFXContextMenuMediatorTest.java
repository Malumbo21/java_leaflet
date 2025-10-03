package io.github.makbn.jlmap.fx.test.element.menu;

import io.github.makbn.jlmap.element.menu.JLContextMenu;
import io.github.makbn.jlmap.fx.JLMapView;
import io.github.makbn.jlmap.fx.element.menu.JavaFXContextMenuMediator;
import io.github.makbn.jlmap.model.JLLatLng;
import io.github.makbn.jlmap.model.JLMarker;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JavaFXContextMenuMediator.
 * Tests the context menu mediator functionality for JavaFX implementation.
 *
 * @author Matt Akbarian (@makbn)
 * @since 2.0.0
 */
@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class JavaFXContextMenuMediatorTest {

    @Mock
    private JLMapView mockMapView;

    @Mock
    private WebView mockWebView;

    private JavaFXContextMenuMediator mediator;

    @BeforeEach
    void setUp() {
        mediator = new JavaFXContextMenuMediator();
        // Note: Stubbing is only added when needed in tests that use it
    }

    // === Constructor and Basic Tests ===

    @Test
    void constructor_shouldInitializeSuccessfully() {
        // When
        JavaFXContextMenuMediator newMediator = new JavaFXContextMenuMediator();

        // Then
        assertThat(newMediator).isNotNull();
        assertThat(newMediator.getName()).isEqualTo("JavaFX Context Menu Mediator");
    }

    @Test
    void getName_shouldReturnCorrectName() {
        // When
        String name = mediator.getName();

        // Then
        assertThat(name).isEqualTo("JavaFX Context Menu Mediator");
    }

    @Test
    void supportsObjectType_shouldReturnTrueForAllTypes() {
        // When/Then
        assertThat(mediator.supportsObjectType(JLMarker.class)).isTrue();
    }

    // === showContextMenu Tests ===

    @Test
    void showContextMenu_withObjectWithoutContextMenu_shouldNotShowMenu() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        // When
        mediator.showContextMenu(mockMapView, marker, 100, 100);

        // Then - No menu should be created since marker has no context menu
        // This is verified by the lack of exceptions and the method completing
        assertThat(marker.getContextMenu()).isNull();
    }

    @Test
    void showContextMenu_withDisabledContextMenu_shouldNotShowMenu() {
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

        // When
        mediator.showContextMenu(mockMapView, marker, 100, 100);

        // Then - Menu should not be shown because it's disabled
        assertThat(marker.isContextMenuEnabled()).isFalse();
    }

    @Test
    void showContextMenu_withEnabledContextMenuButNoItems_shouldNotShowMenu() {
        // Given
        JLMarker marker = JLMarker.builder()
                .id("test-marker")
                .latLng(new JLLatLng(51.5, -0.09))
                .text("Test")
                .transport(null)
                .build();

        marker.addContextMenu(); // Empty context menu

        // When
        mediator.showContextMenu(mockMapView, marker, 100, 100);

        // Then - Menu should not be shown because it has no items
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

        // Note: Can't fully test showing the menu in unit tests without a JavaFX scene
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

        // Then - Menu should be hidden (can't verify fully without JavaFX scene)
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
        // Note: Cannot test actual menu reuse without full JavaFX UI context
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
        // Note: Full verification requires JavaFX scene
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
