package ru.kantser.service.settings;

import ru.kantser.model.AppSettings;

import java.io.IOException;

public interface SettingsService {
    AppSettings loadSettings() throws IOException;
    void saveSettings(AppSettings settings) throws IOException;
    AppSettings getDefaultSettings();
}