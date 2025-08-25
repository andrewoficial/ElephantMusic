module ru.kantser.firstfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.slf4j;
    requires com.google.guice;
    requires java.xml;

    // Экспортируем пакеты, которые используются Guice
    exports ru.kantser.controller to com.google.guice;
    exports ru.kantser.service to com.google.guice;

    // Открываем пакеты для рефлексии (нужно для FXML и Guice)
    opens ru.kantser.controller to javafx.fxml, com.google.guice;
    opens ru.kantser.service to com.google.guice;

    // Если используете FXML
    opens ru.kantser.view to javafx.fxml;

    exports ru.kantser; // Exports the package containing MyApplication and Launcher
}
