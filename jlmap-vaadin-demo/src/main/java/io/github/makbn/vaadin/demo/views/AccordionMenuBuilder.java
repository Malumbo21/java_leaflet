package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccordionMenuBuilder {

    private final Accordion accordion;
    private final Map<String, VerticalLayout> menuContents = new LinkedHashMap<>();
    private String currentMenuName;

    public AccordionMenuBuilder(Accordion accordion) {
        this.accordion = accordion;
    }

    /**
     * Starts a new menu (accordion panel) with the given title.
     */
    public AccordionMenuBuilder menu(String name) {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        menuContents.put(name, content);
        currentMenuName = name;
        return this;
    }

    /**
     * Adds a clickable item (button) to the current menu.
     */
    public AccordionMenuBuilder item(String name, ComponentEventListener<ClickEvent<Button>> clickListener) {
        if (currentMenuName == null) {
            throw new IllegalStateException("Call menu(name) before adding items");
        }
        Button button = new Button(name, clickListener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.setWidthFull();
        menuContents.get(currentMenuName).add(button);
        return this;
    }

    /**
     * Builds the accordion with all menus and items.
     */
    public Accordion build() {
        accordion.getChildren().forEach(accordion::remove);
        menuContents.forEach((name, content) -> {
            AccordionPanel panel = accordion.add(name, content);
        });
        return accordion;
    }
}
