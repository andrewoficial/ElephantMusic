package ru.kantser.elephantmusic.service.lastfm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.AppSettings;
import ru.kantser.elephantmusic.service.settings.JacksonSettingsService;


import java.util.HashMap;
import java.util.Map;

public class LastFmAuthService {
    private static final Logger logger = LoggerFactory.getLogger(LastFmAuthService.class);
    private static final String API_KEY = "c16c73bb3c2f5f792df77fa4f0740d8d";
    private static final String API_SECRET = "7a7c8515b163a720f71443123ec1c421";
    private static final String AUTH_URL = "https://www.last.fm/api/auth/?api_key=" + API_KEY;
    //private final



    @Inject
    private JacksonSettingsService settingsService;


    public String getAuthUrl(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return "https://www.last.fm/api/auth/?api_key=" + API_KEY + "&token=" + token;
    }

    public String getAuthToken() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("method", "auth.getToken");
            params.put("api_key", API_KEY);
            Utils utils = new Utils();
            String signature = utils.generateSignature(params, API_SECRET);
            params.put("api_sig", signature);
            params.put("format", "json");

            logger.info("Requesting auth token with params: {}", params);
            String response = utils.sendRequest(params);

            // Извлекаем токен из JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            if (root.has("error")) {
                logger.error("Error getting auth token: {}", root.get("message").asText());
                return null;
            }
            logger.info("Return token as text [{}]", root.path("token").asText());
            return root.path("token").asText();
        } catch (Exception e) {
            logger.error("Failed to get auth token", e);
            return null;
        }
    }

    public boolean authenticate(String token) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("method", "auth.getSession");
            params.put("api_key", API_KEY);
            params.put("token", token);
            Utils utils = new Utils();
            String signature = utils.generateSignature(params, API_SECRET);
            params.put("api_sig", signature);
            params.put("format", "json");

            logger.info("Request parameters: {}", params);
            String response = utils.sendRequest(params);

            String sessionKey = utils.extractSessionKey(response);
            String username = utils.extractUsername(response);

            if (sessionKey != null && !sessionKey.isEmpty()) {
                AppSettings settings = settingsService.loadSettings();
                settings.setLastFmToken(sessionKey); // Сохраняем sk, а не auth_token!
                settings.setLastFmName(username);
                settingsService.saveSettings(settings);
                logger.info("Authentication successful for user: {}", username);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Last.fm authentication failed", e);
            return false;
        }
    }


    public boolean isAuthenticated() {
        try {
            String token = settingsService.loadSettings().getLastFmToken();
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        try {
            // Очищаем настройки
            AppSettings settings = settingsService.loadSettings();
            settings.setLastFmToken(null);
            settings.setLastFmName(null);
            settings.setLanguage("LogOut");
            settingsService.saveSettings(settings);
        } catch (Exception e) {
            logger.error("Failed to logout from Last.fm", e);
        }
    }
}