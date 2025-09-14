package ru.kantser.elephantmusic.controller;


import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.service.lastfm.LastFmAuthService;
import ru.kantser.elephantmusic.view.dialog.LastFmAuthDialog;

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