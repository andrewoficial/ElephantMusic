package ru.kantser.controller;


import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.model.Track;
import ru.kantser.service.AppModule;
import ru.kantser.service.AudioPlayerService;
import ru.kantser.service.PlaylistService;
import ru.kantser.service.WindowTitleService;

import java.io.File;
import java.time.Duration;

public class PlayListPanelController {
    private static final Logger logger = LoggerFactory.getLogger(PlayListPanelController.class);

    @FXML
    private ListView<Track> playlistView;

    @Inject
    private PlaylistService playlistService;

    @Inject
    private AudioPlayerService audioPlayerService;

    @Inject
    private WindowTitleService windowTitleService;

    @FXML
    public void initialize() {
        if(this.playlistService == null){
            logger.info("Ошибка инжектирования playlistService");
        }
        // Инициализация панели плейлиста
        playlistView.setItems(playlistService.getTracks());

        playlistView.setCellFactory(lv -> new ListCell<Track>() {
            @Override
            protected void updateItem(Track item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });

        playlistView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track selectedTrack = playlistView.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    playTrack(selectedTrack);
                }
            }
        });

        // В методе initialize()
        ContextMenu contextMenu = new ContextMenu();
        MenuItem playItem = new MenuItem("Воспроизвести");
        MenuItem removeItem = new MenuItem("Удалить из плейлиста");

        playItem.setOnAction(e -> {
            Track selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) playTrack(selected);
        });

        removeItem.setOnAction(e -> {
            Track selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) playlistService.removeTrack(selected);
        });

        contextMenu.getItems().addAll(playItem, removeItem);
        playlistView.setContextMenu(contextMenu);
    }

    private void playTrack(Track track) {
        if (audioPlayerService != null) {
            audioPlayerService.play(track);
            playlistService.setCurrentTrack(track);
            // Обновляем заголовок окна
            if (windowTitleService != null) {
                windowTitleService.updateTitle("Воспроизведение: " + track.getTitle());
            }
            if(audioPlayerService != null){
                audioPlayerService.notifyPlaybackStateChanged();
            }
            //Уведомить об начале воспроизведения
        } else {
            logger.warn("AudioPlayerService не доступен для воспроизведения трека");
        }
    }

    @FXML
    private void addTrack() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аудио файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Аудио файлы", "*.mp3", "*.wav", "*.aac")
        );

        File file = fileChooser.showOpenDialog(playlistView.getScene().getWindow());
        if (file != null) {
            // В реальном приложении здесь нужно извлечь метаданные из файла
            Track track = new Track(
                    file.getName(),
                    "Неизвестный исполнитель",
                    file.toPath(),
                    Duration.ofMinutes(3) // Заглушка, нужно получить реальную длительность
            );
            playlistService.addTrack(track);
        }

    }
    
    @FXML
    private void clearPlaylist() {
        playlistService.clearPlaylist();
    }
}