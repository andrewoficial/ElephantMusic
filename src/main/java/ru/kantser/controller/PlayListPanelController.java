package ru.kantser.controller;


import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.model.Track;
import ru.kantser.service.AppModule;
import ru.kantser.service.AudioPlayerService;
import ru.kantser.service.PlaylistService;
import ru.kantser.service.WindowTitleService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

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
                audioPlayerService.notifyPlaybackStateChanged(true);
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
                new FileChooser.ExtensionFilter("Аудио файлы", "*.mp3", "*.wav", "*.aac", "*.flac", "*.m4a")
        );

        File file = fileChooser.showOpenDialog(playlistView.getScene().getWindow());
        if (file != null) {
            try {
                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();
                AudioHeader header = audioFile.getAudioHeader();

                String title = file.getName();
                String artist = "Неизвестный исполнитель";
                Duration duration = Duration.ofSeconds(header.getTrackLength());

                if (tag != null) {
                    // Извлекаем название трека
                    if (tag.getFirst(FieldKey.TITLE) != null && !tag.getFirst(FieldKey.TITLE).isEmpty()) {
                        title = tag.getFirst(FieldKey.TITLE);
                    }

                    // Извлекаем исполнителя
                    if (tag.getFirst(FieldKey.ARTIST) != null && !tag.getFirst(FieldKey.ARTIST).isEmpty()) {
                        artist = tag.getFirst(FieldKey.ARTIST);
                    }
                }

                Track track = new Track(
                        title,
                        artist,
                        file.toPath(),
                        duration
                );
                playlistService.addTrack(track);

            } catch (Exception e) {
                // Если не удалось прочитать метаданные, создаем трек с базовой информацией
                Track track = new Track(
                        file.getName(),
                        "Неизвестный исполнитель",
                        file.toPath(),
                        Duration.ofSeconds(0)
                );
                playlistService.addTrack(track);
                logger.warn("Ошибка чтения метаданных: {}", e.getMessage());
            }
        }
    }
    
    @FXML
    private void clearPlaylist() {
        playlistService.clearPlaylist();
    }




    public void addFolderForScan(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку с аудиофайлами");

        File selectedDirectory = directoryChooser.showDialog(playlistView.getScene().getWindow());

        if (selectedDirectory != null) {
            // Запускаем в отдельном потоке, чтобы не блокировать UI
            new Thread(() -> {
                AtomicInteger addedTracks = new AtomicInteger(0);

                try {
                    // Рекурсивно обходим все файлы в выбранной папке
                    Files.walk(selectedDirectory.toPath())
                            .filter(this::isAudioFile)
                            .forEach(filePath -> {
                                try {
                                    processAudioFile(filePath.toFile());
                                    addedTracks.incrementAndGet();
                                } catch (Exception e) {
                                    logger.warn("Не удалось обработать файл: " + filePath, e);
                                }
                            });
                } catch (IOException e) {
                    logger.error("Ошибка при сканировании папки: " + selectedDirectory, e);
                }

                // Показываем уведомление о завершении в UI потоке
                Platform.runLater(() -> {
                    showCompletionAlert(addedTracks.get());
                });
            }).start();
        }
    }

    // Проверяем, является ли файл аудиофайлом
    private boolean isAudioFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }

        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".mp3") ||
                fileName.endsWith(".wav") ||
                fileName.endsWith(".aac") ||
                fileName.endsWith(".flac") ||
                fileName.endsWith(".m4a");
    }

    // Обрабатываем аудиофайл и добавляем его в плейлист
    private void processAudioFile(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            AudioHeader header = audioFile.getAudioHeader();

            String title = file.getName();
            String artist = "Неизвестный исполнитель";
            Duration duration = Duration.ofSeconds(header.getTrackLength());

            if (tag != null) {
                if (tag.getFirst(FieldKey.TITLE) != null && !tag.getFirst(FieldKey.TITLE).isEmpty()) {
                    title = tag.getFirst(FieldKey.TITLE);
                }

                if (tag.getFirst(FieldKey.ARTIST) != null && !tag.getFirst(FieldKey.ARTIST).isEmpty()) {
                    artist = tag.getFirst(FieldKey.ARTIST);
                }
            }

            Track track = new Track(title, artist, file.toPath(), duration);

            // Добавляем трек в UI потоке
            Platform.runLater(() -> {
                playlistService.addTrack(track);
            });

        } catch (Exception e) {
            // Если не удалось прочитать метаданные, создаем трек с базовой информацией
            Track track = new Track(
                    file.getName(),
                    "Неизвестный исполнитель",
                    file.toPath(),
                    Duration.ofSeconds(0)
            );

            Platform.runLater(() -> {
                playlistService.addTrack(track);
            });

            logger.warn("Ошибка чтения метаданных: {}", e.getMessage());
        }
    }

    // Показываем уведомление о завершении сканирования
    private void showCompletionAlert(int tracksAdded) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Сканирование завершено");
        alert.setHeaderText(null);
        alert.setContentText("Добавлено треков: " + tracksAdded);
        alert.showAndWait();
    }
}