package ru.kantser.controller;


import com.google.inject.Guice;
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
import ru.kantser.service.AppModule;
import ru.kantser.service.FxmlLoaderHelper;

import java.io.IOException;

public class MainWindowController {
    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);
    @FXML
    private StackPane contentArea;

    private VBox playerPanel;
    private VBox playlistPanel;
    private VBox aboutPanel;
    @FXML
    private Button currentButton;
    @FXML
    private Button playlistButton;
    @FXML
    private Button aboutButton;
    //private Injector injector = Guice.createInjector(new AppModule());
    @FXML
    public void initialize() {
        try {
            // Загрузка панелей из отдельных FXML файлов
            logger.info("Сосздаю панель playerPanel");
            playerPanel = FxmlLoaderHelper.load("/ru/kantser/view/player_panel.fxml");
            logger.info("Создал панель playerPanel");

            logger.info("Сосздаю панель playlistPanel");
            playlistPanel = FxmlLoaderHelper.load("/ru/kantser/view/playlist_panel.fxml");
            logger.info("Создал панель playlistPanel");

            //FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("/ru/igm/view/about_panel.fxml"));
            aboutPanel = FxmlLoaderHelper.load("/ru/kantser/view/about_panel.fxml");
            logger.info("Создал панель aboutPanel");
            // Показываем панель плейлиста по умолчанию
            showPlayerPanel();

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
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

    private void setActiveButton(Button activeButton) {
        currentButton.getStyleClass().remove("active");
        playlistButton.getStyleClass().remove("active");
        aboutButton.getStyleClass().remove("active");
        activeButton.getStyleClass().add("active");
    }
}