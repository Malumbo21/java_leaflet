module io.github.makbn.jlmap.vaadin {
    // API dependency
    requires io.github.makbn.jlmap.api;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.web;

    //Vaadin UI components used
    requires flow.server;
    requires vaadin.grid.flow;
    requires flow.data;
    requires vaadin.text.field.flow;
    requires vaadin.lumo.theme;
    requires vaadin.ordered.layout.flow;

    // JDK modules
    requires jdk.jsobject;

    // Logging
    requires org.slf4j;

    // JSON processing
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;

    // Annotations
    requires static org.jetbrains.annotations;
    requires static lombok;
    requires vaadin.notification.flow;
    requires flow.html.components;
    requires vaadin.button.flow;
    requires vaadin.spring;
    requires gwt.elemental;
    requires org.apache.tomcat.embed.core;

    // Exports for public API
    exports io.github.makbn.jlmap.vaadin;

    // Opens for reflection (if needed by frameworks)
    opens io.github.makbn.jlmap.vaadin to com.vaadin.flow.server;
    opens io.github.makbn.jlmap.vaadin.engine to com.vaadin.flow.server;
}