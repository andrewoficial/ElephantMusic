package ru.kantser.elephantmusic.service.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.Playlist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class JacksonPlayListService implements PlayListSaverService {
    private static final Logger logger = LoggerFactory.getLogger(JacksonPlayListService.class);

    private final ObjectMapper objectMapper;
    private final Path playListPath;

    @Inject
    public JacksonPlayListService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.playListPath = Paths.get(System.getProperty("user.home"), ".ElephantPlayer", "playlist.json");
    }

    @Override
    public Playlist loadPlayList() throws IOException {
        if (Files.exists(playListPath)) {
            return objectMapper.readValue(playListPath.toFile(), Playlist.class);
        } else {
            logger.warn("Ошибка доступа к папке, возвращаю пустой плейлист.");
            return getDefaultPlayList();
        }
    }


    @Override
    public void savePlaylist(Playlist playlist) throws IOException {
        logger.info("Сохраняю в {}", playListPath.getParent());
        if(playlist == null){
            logger.warn("playlist settings IS NULL");
        }
        Files.createDirectories(playListPath.getParent());

        objectMapper.writeValue(playListPath.toFile(), playlist);
    }

    @Override
    public Playlist getDefaultPlayList() {
        Playlist defaults = new Playlist("Основной плейлист");
        defaults.clear();
        return defaults;
    }
}