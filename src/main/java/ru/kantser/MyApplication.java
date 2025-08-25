package ru.kantser;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.service.AppModule;
import ru.kantser.service.FxmlLoaderHelper;
import ru.kantser.service.WindowTitleService;

import java.net.URL;

public class MyApplication extends javafx.application.Application {
    private static final Logger logger = LoggerFactory.getLogger(MyApplication.class);
    private Injector injector;
    private WindowTitleService windowTitleService;

    @Override
    public void init() {
        injector = Guice.createInjector(new AppModule());
        FxmlLoaderHelper.setInjector(injector); // Устанавливаем инжектор для помощника
        logger.info("Создал инжектор от гугла");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Начинаю запуск приложения (метод start в классе MyApplication)");
        // Устанавливаем Stage для сервиса заголовка
        windowTitleService = injector.getInstance(WindowTitleService.class);
        windowTitleService.setPrimaryStage(primaryStage);

        // Загружаем главное окно
        logger.info("Начинаю создание main_window");
        Parent mainRoot = FxmlLoaderHelper.load("/ru/kantser/view/main_window.fxml");
        logger.info("Вызвал mainLoader.load()");

        try {
            URL resourceUrl = getClass().getResource("/ru/kantser/view/images/elephant.jpg");
            if (resourceUrl != null) {
                Image icon = new Image(resourceUrl.toExternalForm());
                primaryStage.getIcons().add(icon);
                logger.info("Иконка приложения загружена через URL");
            } else {
                logger.error("Ресурс не найден: /ru/kantser/view/images/elephant.jpg");
            }
        } catch (Exception e) {
            logger.error("Не удалось загрузить иконку приложения", e);
        }

        primaryStage.setTitle("Музыкальный проигрыватель");
        primaryStage.setScene(new Scene(mainRoot, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}