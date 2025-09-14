package ru.kantser.elephantmusic.service.settings;

import ru.kantser.elephantmusic.model.AppSettings;

import java.io.IOException;

public interface SettingsService {
    AppSettings loadSettings() throws IOException;
    void saveSettings(AppSettings settings) throws IOException;
    AppSettings getDefaultSettings();
}