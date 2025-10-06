module io.github.makbn.jlmap.fx {
    // API dependency
    requires io.github.makbn.jlmap.api;

    // JavaFX modules
    requires javafx.controls;
    requires javafx.base;
    requires javafx.swing;
    requires javafx.web;
    requires javafx.graphics;

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
    requires com.j2html;

    // Exports for public API
    exports io.github.makbn.jlmap.fx;
    exports io.github.makbn.jlmap.fx.demo;
    exports io.github.makbn.jlmap.fx.element.menu;
    exports io.github.makbn.jlmap.fx.engine;
    exports io.github.makbn.jlmap.fx.layer;
    exports io.github.makbn.jlmap.fx.internal;

    // Service providers
    provides io.github.makbn.jlmap.element.menu.JLContextMenuMediator
            with io.github.makbn.jlmap.fx.element.menu.JavaFXContextMenuMediator;

    // Opens for reflection (if needed by frameworks)
    opens io.github.makbn.jlmap.fx to javafx.graphics, io.github.makbn.jlmap.fx.test;
    opens io.github.makbn.jlmap.fx.engine to javafx.graphics, io.github.makbn.jlmap.fx.test;
    opens io.github.makbn.jlmap.fx.demo to javafx.graphics, io.github.makbn.jlmap.fx.test;
    opens io.github.makbn.jlmap.fx.layer to javafx.graphics, io.github.makbn.jlmap.fx.test;
    opens io.github.makbn.jlmap.fx.internal to javafx.graphics, io.github.makbn.jlmap.fx.test;
} 