package ru.kantser.elephantmusic.service.lastfm;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.model.Track;
import ru.kantser.elephantmusic.service.settings.JacksonSettingsService;

import java.net.http.HttpClient;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LastFmScrobblerService {
    private static final Logger logger = LoggerFactory.getLogger(LastFmScrobblerService.class);
    private static final String API_KEY = "c16c73bb3c2f5f792df77fa4f0740d8d"; // Должен совпадать с ключом в LastFmAuthService
    private static final String API_SECRET = "7a7c8515b163a720f71443123ec1c421"; // Должен совпадать с секретом в LastFmAuthService
    
    private final HttpClient httpClient;
    
    @Inject
    private JacksonSettingsService settingsService;
    
    @Inject
    private LastFmAuthService authService;
    
    public LastFmScrobblerService() {
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public void scrobble(Track track) {
        if (!authService.isAuthenticated()) {
            logger.info("Not authenticated with Last.fm, skipping scrobble");
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("method", "track.scrobble");
                params.put("api_key", API_KEY);
                //====
                settingsService.loadSettings();
                //====
                params.put("sk", settingsService.loadSettings().getLastFmToken());
                params.put("artist", track.getArtist());
                params.put("track", track.getTitle());
                params.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));



                Utils utils = new Utils();
                String signature = utils.generateSignature(params, API_SECRET);
                params.put("api_sig", signature);
                
                String response = utils.sendRequest(params);
                logger.info("Last.fm scrobble response: {}", response);
            } catch (Exception e) {
                logger.error("Failed to scrobble to Last.fm", e);
            }
        });
    }
    
    public void updateNowPlaying(Track track) {
        if (!authService.isAuthenticated()) {
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("method", "track.updateNowPlaying");
                params.put("api_key", API_KEY);
                params.put("sk", settingsService.loadSettings().getLastFmToken());
                params.put("artist", track.getArtist());
                params.put("track", track.getTitle());
                
//                if (track.getAlbum() != null) {
//                    params.put("album", track.getAlbum());
//                }

                Utils utils = new Utils();
                String signature = utils.generateSignature(params, API_SECRET);
                params.put("api_sig", signature);

                String response = utils.sendRequest(params);
                logger.info("Last.fm now playing response: {}", response);
            } catch (Exception e) {
                logger.error("Failed to update now playing on Last.fm", e);
            }
        });
    }
    

    

}