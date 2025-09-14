package ru.kantser.elephantmusic.controller;


import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.AppSettings;
import ru.kantser.elephantmusic.service.FxmlLoaderHelper;
import ru.kantser.elephantmusic.service.settings.JacksonSettingsService;

import java.io.IOException;

public class MainWindowController {
    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    @Inject
    JacksonSettingsService settingsService;

    private VBox playerPanel;
    private VBox playlistPanel;
    private VBox aboutPanel;
    private VBox lastFmPanel;

    @FXML
    private StackPane contentArea;


    @FXML
    private Button currentButton;
    @FXML
    private Button playlistButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button lastFmButton;
    @FXML
    public void initialize() {
        if(settingsService == null){
            logger.warn("settingsService == null");
        }else {
            logger.info("Инициализирую настройки");
            AppSettings settings = null;
            try {
                settings = settingsService.loadSettings();
            } catch (IOException e) {
                settings = settingsService.getDefaultSettings();
                logger.error(e.getMessage());
            }

            settings.setLanguage("ENG");
            try {
                settingsService.saveSettings(settings);
            } catch (IOException e) {
                //throw new RuntimeException(e);
                logger.warn(e.getMessage());
            }


        }
        try {
            // Загрузка панелей из отдельных FXML файлов
            logger.info("Сосздаю панель playerPanel");
            playerPanel = FxmlLoaderHelper.load("/ru/kantser/elephantmusic/view/player_panel.fxml");

            logger.info("Сосздаю панель playlistPanel");
            playlistPanel = FxmlLoaderHelper.load("/ru/kantser/elephantmusic/view/playlist_panel.fxml");

            logger.info("Сосздаю панель lastFm");
            lastFmPanel = FxmlLoaderHelper.load("/ru/kantser/elephantmusic/view/last_fm_panel.fxml");

            logger.info("Сосздаю панель aboutPanel");
            aboutPanel = FxmlLoaderHelper.load("/ru/kantser/elephantmusic/view/about_panel.fxml");

            showPlayerPanel();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    private void showPlayerPanel() {
        contentArea.getChildren().setAll(playerPanel);
        setActiveButton(currentButton);
    }

    @FXML
    private void showPlaylistPanel() {
        contentArea.getChildren().setAll(playlistPanel);
        setActiveButton(playlistButton);
    }

    @FXML
    private void showAboutPanel() {
        contentArea.getChildren().setAll(aboutPanel);
        setActiveButton(aboutButton);
    }
    public void showLastFmPanel() {
        contentArea.getChildren().setAll(lastFmPanel);
        setActiveButton(lastFmButton);
    }

    private void setActiveButton(Button activeButton) {
        currentButton.getStyleClass().remove("active");
        playlistButton.getStyleClass().remove("active");
        aboutButton.getStyleClass().remove("active");
        lastFmButton.getStyleClass().remove("active");
        activeButton.getStyleClass().add("active");
    }


}