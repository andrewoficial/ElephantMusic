package ru.kantser.elephantmusic.service.settings;

import ru.kantser.elephantmusic.model.Playlist;

import java.io.IOException;

public interface PlayListSaverService {
    Playlist loadPlayList() throws IOException;
    void savePlaylist(Playlist settings) throws IOException;
    Playlist getDefaultPlayList();
}