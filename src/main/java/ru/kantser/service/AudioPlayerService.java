package ru.kantser.service;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.model.Track;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AudioPlayerService {
    private MediaPlayer mediaPlayer;
    private Track currentTrack;
    private boolean isPlaying = false;
    private static final Logger logger = LoggerFactory.getLogger(AudioPlayerService.class);
    private Runnable onTrackChanged;
    private List<Runnable> playbackStateListeners = new ArrayList<>();

    public void setOnTrackChanged(Runnable listener) {
        this.onTrackChanged = listener;
    }

    private void notifyTrackChanged() {
        if (onTrackChanged != null) {
            Platform.runLater(onTrackChanged);
        }
    }

    @Inject
    private PlaylistService playlistService;

    @Inject
    private WindowTitleService windowTitleService;

    public void play(Track track) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        
        currentTrack = track;
        Media media = new Media(track.getFilePath().toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            isPlaying = true;
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            isPlaying = false;
            // Здесь можно добавить логику перехода к следующему треку
        });
        notifyTrackChanged();
    }
    
    public void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            // Устанавливаем Stage для сервиса заголовка
            logger.info("Устанавливаю в заголовок ОСТАНОВЛЕНО");
            windowTitleService.updateTitle("[остановлено]");
        }

    }
    
    public void resume() {
        if(mediaPlayer == null){
            logger.info("Пытаюсь запустить с mediaPlayer == null");
            if(playlistService == null){
                logger.error("playlistService == null");
                return;
            }

            if(playlistService.getTracks() == null){
                logger.warn("playlistService.getTracks() == null");
                return;
            }

            if(playlistService.getCurrentPlaylist().getTracks().isEmpty()){
                logger.warn("playlistService.getTracks().isEmpty()");
                return;
            }
            Track track = playlistService.getTracks().getFirst();
            this.play(track);
        }

        if(!isPlaying){
            mediaPlayer.play();
            isPlaying = true;
        }else{
            logger.info("Пытаюсь запустить с нуля");
            Track track = playlistService.getTracks().getFirst();
            this.play(track);
        }

    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
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
        return isPlaying;
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


    public void notifyPlaybackStateChanged() {
        for (Runnable listener : playbackStateListeners) {
            Platform.runLater(listener);
        }
    }
}