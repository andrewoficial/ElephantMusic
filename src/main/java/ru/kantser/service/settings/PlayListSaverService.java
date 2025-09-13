package ru.kantser.service.settings;

import ru.kantser.model.Playlist;

import java.io.IOException;

public interface PlayListSaverService {
    Playlist loadPlayList() throws IOException;
    void savePlaylist(Playlist settings) throws IOException;
    Playlist getDefaultPlayList();
}