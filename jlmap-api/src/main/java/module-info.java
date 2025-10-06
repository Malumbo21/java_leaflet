module io.github.makbn.jlmap.api {
    // JDK modules
    requires jdk.jsobject;

    // Logging
    requires org.slf4j;

    // Annotations
    requires static org.jetbrains.annotations;
    requires static lombok;

    // JSON processing
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;

    // Exports for public API
    exports io.github.makbn.jlmap;
    exports io.github.makbn.jlmap.layer.leaflet;
    exports io.github.makbn.jlmap.listener;
    exports io.github.makbn.jlmap.listener.event;
    exports io.github.makbn.jlmap.model;
    exports io.github.makbn.jlmap.map;
    exports io.github.makbn.jlmap.model.builder;
    exports io.github.makbn.jlmap.exception;
    exports io.github.makbn.jlmap.geojson;
    exports io.github.makbn.jlmap.engine;
    exports io.github.makbn.jlmap.element.menu;

    opens io.github.makbn.jlmap.model to com.google.gson;
    opens io.github.makbn.jlmap.element.menu to com.google.gson;

    uses io.github.makbn.jlmap.element.menu.JLContextMenuMediator;
} 