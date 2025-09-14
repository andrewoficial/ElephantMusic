package ru.kantser.elephantmusic.controller;


import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.AppSettings;
import ru.kantser.elephantmusic.service.lastfm.LastFmAuthService;
import ru.kantser.elephantmusic.service.settings.JacksonSettingsService;
import ru.kantser.elephantmusic.view.dialog.LastFmAuthDialog;

import java.io.IOException;

public class LastFmPanelController {
    private static final Logger logger = LoggerFactory.getLogger(LastFmPanelController.class);

    @Inject
    private LastFmAuthService lastFmAuthService;
    @Inject
    private LastFmAuthDialog lastFmAuthDialog;
    @Inject
    private JacksonSettingsService settingsService;

    @FXML
    private ToggleButton scrobblingToggle;
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label statusLabel;
    @FXML
    public void initialize() {
        updateUI();

        // Обработка изменения состояния переключателя
        scrobblingToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (lastFmAuthService.isAuthenticated()) {
                AppSettings settings =  settingsService.loadSettings();
                settings.setActiveScrobbling(newValue);
                settingsService.saveSettings(settings);
            } else {
                // Если пользователь не авторизован, вернем переключатель в исходное состояние
                scrobblingToggle.setSelected(oldValue);
            }
        });
    }
    private void updateUI() {
        boolean isAuthenticated = lastFmAuthService.isAuthenticated();
        Boolean isScrobblingEnabled = false;

        isScrobblingEnabled = safetyGetIsScrobblingEnabled();

        scrobblingToggle.setSelected(isScrobblingEnabled);

        if (isAuthenticated) {
            AppSettings settings = settingsService.loadSettings();
            String username = settings.getLastFmName();
            statusLabel.setText("Авторизован как: " + username);
            scrobblingToggle.setDisable(false);
            scrobblingToggle.setSelected(safetyGetIsScrobblingEnabled());
            logoutButton.setVisible(true);
            loginButton.setVisible(false);
        } else {
            statusLabel.setText("Не авторизован");
            scrobblingToggle.setDisable(true);
            scrobblingToggle.setSelected(safetyGetIsScrobblingEnabled());
            logoutButton.setVisible(false);
            loginButton.setVisible(true);
        }
    }

    private boolean safetyGetIsScrobblingEnabled(){
        AppSettings settings = settingsService.loadSettings();
        Boolean  isScrobblingEnabled = settings.getActiveScrobbling();
        if(isScrobblingEnabled == null){
            settings.setActiveScrobbling(true);
            isScrobblingEnabled = true;
            settingsService.saveSettings(settings);
            return true;
        }
        return isScrobblingEnabled;
    }
//
//        //Проверяем, авторизован ли уже пользователь
//        updateLoginButtonState();
//
//        // Обработчик кнопки входа в Last.fm
//        lastFmLoginButton.setOnAction(event -> {
//            if (lastFmAuthService.isAuthenticated()) {
//                // Выход из Last.fm
//                lastFmAuthService.logout();
//                updateLoginButtonState();
//            } else {
//                // Показываем диалог авторизации
//
//                lastFmAuthDialog.showAuthDialog();
//                updateLoginButtonState();
//            }
//        });
//    }

    @FXML
    private void handleScrobblingToggle() {
        AppSettings settings = null;

            settings = settingsService.loadSettings();
            settings.setActiveScrobbling(scrobblingToggle.isSelected());
            settingsService.saveSettings(settings);

    }

    @FXML
    private void handleLogin() {
        // Показать диалог авторизации
        // Предположим, что у нас есть метод в lastFmAuthService для показа диалога
        lastFmAuthDialog.showAuthDialog(); // Этот метод должен блокировать до завершения авторизации или использовать обратный вызов
        // После диалога обновляем UI
        updateUI();
    }


    @FXML
    public void handleLastFmLogout(ActionEvent actionEvent) {
        lastFmAuthService.logout();
        updateUI();
    }

    @FXML
    public void handleLastFmLogin(ActionEvent actionEvent) {
        lastFmAuthDialog.showAuthDialog();
        updateUI();
    }
}