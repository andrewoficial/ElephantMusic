module ru.kantser.firstfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.slf4j;
    requires com.google.guice;
    requires java.xml;
    requires com.fasterxml.jackson.annotation;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javafx.web;
    requires java.desktop;
    requires jaudiotagger;
    requires com.fasterxml.jackson.datatype.jsr310; // Добавлена поддержка JSR-310 (java.time)


    // Экспорт пакетов, которые используются Guice
    exports ru.kantser.controller to com.google.guice;
    exports ru.kantser.service to com.google.guice;
    exports ru.kantser.service.settings to com.google.guice;
    exports ru.kantser.service.lastfm to com.google.guice;
    exports ru.kantser.model to com.fasterxml.jackson.databind;
    exports ru.kantser.view.dialog to com.google.guice;

    // Пакеты для рефлексии (нужно для FXML и Guice)
    opens ru.kantser.controller to javafx.fxml, com.google.guice;
    opens ru.kantser.service to com.google.guice;
    opens ru.kantser.service.settings to com.google.guice;
    opens ru.kantser.service.lastfm to com.google.guice;
    opens ru.kantser.model to com.fasterxml.jackson.databind, com.fasterxml.jackson.datatype.jsr310; // Добавлен модуль jsr310

    // Если используете FXML
    opens ru.kantser.view to javafx.fxml;

    exports ru.kantser; // Exports the package containing MyApplication and Launcher
}