package ru.kantser.service;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.model.Track;
import ru.kantser.service.lastfm.LastFmScrobblerService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlayerService {
    private MediaPlayer mediaPlayer;
    private Track currentTrack;
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private AtomicBoolean hasScrobbled = new AtomicBoolean(false); // Флаг для скробблинга (чтобы не дублировать)
    private static final Logger logger = LoggerFactory.getLogger(AudioPlayerService.class);
    private Runnable onTrackChanged;
    private List<Runnable> playbackStateListeners = new ArrayList<>();

    @Inject
    private PlaylistService playlistService;

    @Inject
    private WindowTitleService windowTitleService;

    @Inject
    private LastFmScrobblerService scrobbler; // Инжект скробблера

    public void setOnTrackChanged(Runnable listener) {
        this.onTrackChanged = listener;
    }

    private void notifyTrackChanged() {
        if (onTrackChanged != null) {
            Platform.runLater(onTrackChanged);
        }
    }

    public void play(Track track) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        currentTrack = track;
        Media media = new Media(track.getFilePath().toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        isPlaying.set(true);
        hasScrobbled.set(false); // Сбрасываем флаг скробблинга для нового трека

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            scrobbler.updateNowPlaying(track); // Обновляем "сейчас играет" при старте

            // Добавляем listener для мониторинга прогресса
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    if (hasScrobbled.get()) return; // Уже скробблили — выходим

                    double playedSeconds = newValue.toSeconds();
                    double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
                    double playedPercent = (playedSeconds / totalSeconds) * 100;

                    // Проверяем правило: >50% или >240 сек
                    if (playedPercent >= 50 || playedSeconds >= 240) {
                        scrobbler.scrobble(track); // Скробблим
                        hasScrobbled.set(true); // Устанавливаем флаг
                        logger.info("Scrobbled track: {} by {}", track.getTitle(), track.getArtist());
                    }
                }
            });
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            isPlaying.set(false);
            if (!hasScrobbled.get()) {
                scrobbler.scrobble(track); // Если не скробблили — скробблим на конце (если >50%)
            }
            // Здесь можно добавить логику перехода к следующему треку
        });
        notifyTrackChanged();
    }

    public void pause() {
        if (mediaPlayer != null && isPlaying.get()) {
            mediaPlayer.pause();
            isPlaying.set(false);
        }
    }

    public void resume() {
        if (mediaPlayer == null) {
            logger.info("Пытаюсь запустить с mediaPlayer == null");
            if (playlistService == null) {
                logger.error("playlistService == null");
                return;
            }
            if (playlistService.getTracks() == null) {
                logger.warn("playlistService.getTracks() == null");
                return;
            }
            if (playlistService.getCurrentPlaylist().getTracks().isEmpty()) {
                logger.warn("playlistService.getTracks().isEmpty()");
                return;
            }
            Track track = playlistService.getTracks().getFirst();
            this.play(track);
        }

        if (!isPlaying.get()) {
            mediaPlayer.play();
            isPlaying.set(true);
        } else {
            logger.info("Пытаюсь запустить с нуля");
            Track track = playlistService.getTracks().getFirst();
            this.play(track);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying.set(false);
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }

    public void seek(double v) {
        javafx.util.Duration duration = new Duration(v);
        mediaPlayer.seek(duration);
    }

    public double getCurrentPosition() {
        double duration = mediaPlayer.getTotalDuration().toSeconds();
        double current = mediaPlayer.getCurrentTime().toSeconds();
        double percent =  current / duration * 100;
        return percent;
    }

    public double getDuration() {
        return mediaPlayer.getTotalDuration().toSeconds();
    }


    public void addPlaybackStateListener(Runnable listener) {
        playbackStateListeners.add(listener);
    }


    public void removePlaybackStateListener(Runnable listener) {
        playbackStateListeners.remove(listener);
    }


    public void notifyPlaybackStateChanged(boolean state) {
        for (Runnable listener : playbackStateListeners) {
            Platform.runLater(listener);
        }
    }


}