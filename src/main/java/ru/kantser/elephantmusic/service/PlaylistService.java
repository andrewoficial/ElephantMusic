package ru.kantser.elephantmusic.service;

import com.google.inject.Inject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.Playlist;
import ru.kantser.elephantmusic.model.Track;
import javafx.collections.ObservableList;
import ru.kantser.elephantmusic.service.settings.JacksonPlayListService;

import java.io.IOException;

public class PlaylistService {
    @Inject
    JacksonPlayListService playListSaver;

    @Getter
    private Playlist currentPlaylist;
    private int currentTrackIndex = -1;
    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);

    public PlaylistService() {

    }

    @Inject
    public void initialize() {
        logger.info("Инициализирую сервис плейлиста");
        try {
            currentPlaylist = playListSaver.loadPlayList();
        } catch (IOException e) {
            logger.warn("Ошибка чтения плейлиста при запуске {}", e.getMessage());
            currentPlaylist = new Playlist("Основной плейлист");
        }
    }

    public void addTrack(Track track) {
        currentPlaylist.addTrack(track);
        logger.info("Добавлен трек, теперь в коллекции {}", getCurrentPlaylist().getTracks().size());
        updateFile("Добавление трека");
    }

    public void removeTrack(Track track) {
        int index = currentPlaylist.getTracks().indexOf(track);
        currentPlaylist.removeTrack(track);

        // Обновляем текущий индекс, если удаляемый трек был перед текущим
        if (index != -1 && index < currentTrackIndex) {
            currentTrackIndex--;
        }
        updateFile("Удаление трека");
    }

    public void clearPlaylist() {
        currentPlaylist.clear();
        currentTrackIndex = -1;
        updateFile("Очистка листа");
    }

    public ObservableList<Track> getTracks() {
        return currentPlaylist.getTracks();
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

    private void updateFile(String debugComment){
        try {
            playListSaver.savePlaylist(currentPlaylist);
        } catch (IOException e) {
            logger.warn("Не удалось обновление файла плейлиста{} при отладочном комментарии [{}]", e.getMessage(), debugComment);
        }
    }
}