package ru.kantser.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.model.Track;
import ru.kantser.service.AppModule;
import ru.kantser.service.AudioPlayerService;
import ru.kantser.service.PlaylistService;

public class PlayerPanelController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerPanelController.class);

    @FXML
    private Label currentTrackLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider seekSlider;

    @Inject
    private AudioPlayerService audioPlayerService;

    @Inject
    private PlaylistService playlistService;

    private Timeline progressTimeline;

    @FXML
    public void initialize() {
        // Начальное состояние - воспроизведение
        playPauseButton.getStyleClass().add("play-state");

        // Настройка слайдера громкости
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (audioPlayerService != null) {
                audioPlayerService.setVolume(newValue.doubleValue() / 100.0);
            }
        });

        // Настройка слайдера перемотки
        seekSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (seekSlider.isValueChanging() && audioPlayerService != null) {
                logger.info("Set position {}", newValue.doubleValue());
                audioPlayerService.seek(newValue.doubleValue()); // Ожидает значение от 0 до 100
            }
        });

        // Создаем таймер для обновления прогресса воспроизведения
        progressTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> updateProgress())
        );

        if (audioPlayerService != null) {
            audioPlayerService.addPlaybackStateListener(this::updatePlaybackStateFromService);
        }
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updatePlaybackStateFromService() {
        if (audioPlayerService != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }
            boolean isPlaying = audioPlayerService.isPlaying();
            updatePlaybackState(isPlaying);
        }
    }

    private void updateProgress() {
        if (audioPlayerService != null && audioPlayerService.isPlaying()) {
            double progress = audioPlayerService.getCurrentPosition(); // Должен возвращать от 0 до 100
            double duration = audioPlayerService.getDuration(); // Должен возвращать общую длительность в секундах

            // Устанавливаем максимальное значение слайдера в 100
            seekSlider.setMax(100);
            seekSlider.setValue(progress);

            // Обновляем метку времени
            String currentTime = formatTime(progress * duration / 100); // progress в процентах
            String totalTime = formatTime(duration);
            timeLabel.setText(currentTime + " / " + totalTime);
        }
    }

    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @FXML
    private void togglePlayPause() {
        if (audioPlayerService != null) {
            if (!audioPlayerService.isPlaying()) {
                // Логика начала воспроизведения
                audioPlayerService.resume();
                if (audioPlayerService.isPlaying()) {
                    updateTrackInfo();
                    playPauseButton.getStyleClass().remove("play-state");
                    playPauseButton.getStyleClass().add("pause-state");
                    progressTimeline.play(); // Запускаем обновление прогресса
                }
            } else {
                // Логика паузы
                audioPlayerService.pause();
                playPauseButton.getStyleClass().remove("pause-state");
                playPauseButton.getStyleClass().add("play-state");
                progressTimeline.stop(); // Останавливаем обновление прогресса
            }
        }
    }

    @FXML
    private void previousTrack() {
        if (audioPlayerService != null && playlistService != null) {
            Track previousTrack = playlistService.getPreviousTrack();
            if (previousTrack != null) {
                audioPlayerService.play(previousTrack);
                updateTrackInfo();
                playPauseButton.getStyleClass().remove("play-state");
                playPauseButton.getStyleClass().add("pause-state");
                progressTimeline.play();
            }
        }
    }

    @FXML
    private void nextTrack() {
        if (audioPlayerService != null && playlistService != null) {
            Track nextTrack = playlistService.getNextTrack();
            if (nextTrack != null) {
                audioPlayerService.play(nextTrack);
                updateTrackInfo();
                playPauseButton.getStyleClass().remove("play-state");
                playPauseButton.getStyleClass().add("pause-state");
                progressTimeline.play();
            }
        }
    }

    private void updateTrackInfo() {
        if (audioPlayerService.getCurrentTrack() != null) {
            currentTrackLabel.setText(
                    audioPlayerService.getCurrentTrack().getArtist() + " - " +
                            audioPlayerService.getCurrentTrack().getTitle()
            );
        }
    }

    // Метод для обновления UI при изменении состояния воспроизведения извне
    public void updatePlaybackState(boolean isPlaying) {
        logger.info("Уведомление о начале проигрывания{}", isPlaying);
        if (isPlaying) {
            playPauseButton.getStyleClass().remove("play-state");
            playPauseButton.getStyleClass().add("pause-state");
            progressTimeline.play();
        } else {
            playPauseButton.getStyleClass().remove("pause-state");
            playPauseButton.getStyleClass().add("play-state");
            progressTimeline.stop();
        }
        updateTrackInfo();
    }
}