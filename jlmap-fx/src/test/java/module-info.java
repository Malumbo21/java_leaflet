module io.github.makbn.jlmap.fx.test {
    // API dependency
    requires io.github.makbn.jlmap.api;
    requires io.github.makbn.jlmap.fx;

    // JavaFX modules
    requires javafx.controls;
    requires javafx.base;
    requires javafx.swing;
    requires javafx.web;
    requires javafx.graphics;

    requires jdk.jsobject;
    requires org.slf4j;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;

    requires static org.jetbrains.annotations;
    requires static lombok;
    requires com.j2html;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.assertj.core;
    requires org.testfx.junit5;
    requires org.mockito.junit.jupiter;
    requires org.mockito;
    // Opens for reflection as needed by frameworks
    opens io.github.makbn.jlmap.fx.test.integration to org.testfx.junit5, org.junit.platform.commons;
    opens io.github.makbn.jlmap.fx.test.internal to org.junit.platform.commons, org.mockito;
    opens io.github.makbn.jlmap.fx.test.layer to org.junit.platform.commons, org.mockito;
    opens io.github.makbn.jlmap.fx.test.engine to org.junit.platform.commons, org.mockito;
    opens io.github.makbn.jlmap.fx.test.element.menu to org.junit.platform.commons, org.mockito;
}