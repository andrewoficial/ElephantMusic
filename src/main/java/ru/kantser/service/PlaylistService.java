package ru.kantser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.controller.MainWindowController;
import ru.kantser.model.Playlist;
import ru.kantser.model.Track;
import javafx.collections.ObservableList;

public class PlaylistService {
    private Playlist currentPlaylist;
    private int currentTrackIndex = -1;
    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);

    public PlaylistService() {
        currentPlaylist = new Playlist("Основной плейлист");
    }

    public void addTrack(Track track) {
        currentPlaylist.addTrack(track);
        logger.info("Добавлен трек, теперь в коллекции {}", getCurrentPlaylist().getTracks().size());
    }

    public void removeTrack(Track track) {
        int index = currentPlaylist.getTracks().indexOf(track);
        currentPlaylist.removeTrack(track);

        // Обновляем текущий индекс, если удаляемый трек был перед текущим
        if (index != -1 && index < currentTrackIndex) {
            currentTrackIndex--;
        }
    }

    public void clearPlaylist() {
        currentPlaylist.clear();
        currentTrackIndex = -1;
    }

    public ObservableList<Track> getTracks() {
        return currentPlaylist.getTracks();
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public Track getNextTrack() {
        if (currentPlaylist.getTracks().isEmpty()) {
            return null;
        }

        if (currentTrackIndex < currentPlaylist.getTracks().size() - 1) {
            currentTrackIndex++;
        } else {
            // Если это последний трек, начинаем сначала
            currentTrackIndex = 0;
        }

        return currentPlaylist.getTracks().get(currentTrackIndex);
    }

    public Track getPreviousTrack() {
        if (currentPlaylist.getTracks().isEmpty()) {
            return null;
        }

        if (currentTrackIndex > 0) {
            currentTrackIndex--;
        } else {
            // Если это первый трек, переходим к последнему
            currentTrackIndex = currentPlaylist.getTracks().size() - 1;
        }

        return currentPlaylist.getTracks().get(currentTrackIndex);
    }

    public void setCurrentTrack(Track track) {
        currentTrackIndex = currentPlaylist.getTracks().indexOf(track);
    }
}