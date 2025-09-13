package ru.kantser.service.settings;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.controller.MainWindowController;
import ru.kantser.model.AppSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class JacksonSettingsService implements SettingsService {
    private static final Logger logger = LoggerFactory.getLogger(JacksonSettingsService.class);

    private final ObjectMapper objectMapper;
    private final Path settingsPath;

    @Inject
    public JacksonSettingsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.settingsPath = Paths.get(System.getProperty("user.home"), ".ElephantPlayer", "settings.json");
    }

    @Override
    public AppSettings loadSettings() throws IOException {
        if (Files.exists(settingsPath)) {
            return objectMapper.readValue(settingsPath.toFile(), AppSettings.class);
        } else {
            return getDefaultSettings();
        }
    }



    @Override
    public void saveSettings(AppSettings settings) throws IOException {
        logger.info("Сохраняю в {}", settingsPath.getParent());
        if(settings == null){
            logger.warn("AppSettings settings IS NULL");
        }
        Files.createDirectories(settingsPath.getParent());
        //logger.info("Создал файл {}", settingsPath.toFile());
        logger.info("Язык в новых настройках {}", settings.getLanguage());
        objectMapper.writeValue(settingsPath.toFile(), settings);
    }

    @Override
    public AppSettings getDefaultSettings() {
        AppSettings defaults = new AppSettings();
        defaults.setLanguage("RU");
        defaults.setLastFmName("Anonim");
        defaults.setLastFmToken("NULL");
        return defaults;
    }
}