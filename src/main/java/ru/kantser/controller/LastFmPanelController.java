package ru.kantser.controller;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.Launcher;
import ru.kantser.model.AppSettings;
import ru.kantser.service.AppModule;
import ru.kantser.service.FxmlLoaderHelper;
import ru.kantser.service.lastfm.LastFmAuthService;
import ru.kantser.service.settings.JacksonSettingsService;
import ru.kantser.view.dialog.LastFmAuthDialog;

import java.io.IOException;

public class LastFmPanelController {
    private static final Logger logger = LoggerFactory.getLogger(LastFmPanelController.class);

    @Inject
    private LastFmAuthService lastFmAuthService;
    @Inject
    private LastFmAuthDialog lastFmAuthDialog;

    @FXML
    private Button lastFmLoginButton;
    @FXML
    public void initialize() {
        //Проверяем, авторизован ли уже пользователь
        updateLoginButtonState();

        // Обработчик кнопки входа в Last.fm
        lastFmLoginButton.setOnAction(event -> {
            if (lastFmAuthService.isAuthenticated()) {
                // Выход из Last.fm
                lastFmAuthService.logout();
                updateLoginButtonState();
            } else {
                // Показываем диалог авторизации

                lastFmAuthDialog.showAuthDialog();
                updateLoginButtonState();
            }
        });
    }

    private void updateLoginButtonState() {
        if (lastFmAuthService.isAuthenticated()) {
            lastFmLoginButton.setText("Выйти из Last.fm");
            lastFmLoginButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
        } else {
            lastFmLoginButton.setText("Войти в Last.fm");
            lastFmLoginButton.setStyle("-fx-background-color: #1db954; -fx-text-fill: white;");
        }
    }

    public void handleLastFmLogin() {
        //lastFmAuthService.authenticate(lastFmAuthService.getToken());
        logger.info("Try auth");
    }
}