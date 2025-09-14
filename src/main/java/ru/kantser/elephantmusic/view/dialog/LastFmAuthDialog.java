package ru.kantser.elephantmusic.view.dialog;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.elephantmusic.service.lastfm.LastFmAuthService;

import java.util.Timer;
import java.util.TimerTask;

public class LastFmAuthDialog extends Stage {
    private static final Logger logger = LoggerFactory.getLogger(LastFmAuthDialog.class);
    
    private final LastFmAuthService authService;
    private WebView webView;
    private ProgressIndicator progressIndicator;
    private String authToken;
    private Timer pollingTimer; // Для polling

    @Inject
    public LastFmAuthDialog(LastFmAuthService authService) {
        logger.info("Начинаю создание LastFmAuthDialog");
        this.authService = authService;
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Last.fm Authorization");
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Индикатор загрузки
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);
        
        // WebView для отображения страницы авторизации
        webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        // Обработка изменения состояния загрузки
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                progressIndicator.setVisible(false);
                checkForSuccess(); // Вызываем проверку сразу после загрузки
            } else if (newValue == Worker.State.RUNNING) {
                progressIndicator.setVisible(true);
            }
        });
        
        root.setCenter(webView);
        root.setBottom(progressIndicator);
        
        Scene scene = new Scene(root, 600, 500);
        setScene(scene);
    }
    
    public void showAuthDialog() {
        // Загружаем страницу авторизации Last.fm
        authToken = authService.getAuthToken(); // Получаем токен ОДИН раз
        if (authToken != null) {
            webView.getEngine().load(authService.getAuthUrl(authToken));
            startPolling(); // Запускаем polling
        }
        showAndWait();
    }
    private void startPolling() {
        pollingTimer = new Timer();
        pollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> checkForSuccess());
            }
        }, 2000, 2000); // Проверяем каждые 2 сек, начиная через 2 сек
    }
    private void stopPolling() {
        if (pollingTimer != null) {
            pollingTimer.cancel();
            pollingTimer = null;
        }
    }

    private void checkForSuccess() {
        try {
            String content = (String) webView.getEngine().executeScript("document.documentElement.outerHTML");
            if (content.contains("You have granted access") || content.contains("granted permission")) {
                stopPolling(); // Останавливаем polling
                boolean success = authService.authenticate(authToken);
                if (success) {
                    logger.info("Authentication completed successfully");
                    close(); // Автоматически закрываем диалог
                } else {
                    logger.error("Failed to get session key");
                }
            } else if (content.contains("Invalid API key") || content.contains("error")) {
                stopPolling();
                logger.error("Auth page error detected");
                // Показать ошибку в UI, например, alert
            }
        } catch (Exception e) {
            logger.error("Error checking page content", e);
        }
    }
//    private String extractTokenFromUrl(String url) {
//        try {
//            int tokenIndex = url.indexOf("token=");
//            if (tokenIndex == -1) return null;
//
//            String tokenPart = url.substring(tokenIndex + 6);
//            int endIndex = tokenPart.indexOf("&");
//            if (endIndex == -1) {
//                return tokenPart;
//            } else {
//                return tokenPart.substring(0, endIndex);
//            }
//        } catch (Exception e) {
//            logger.error("Failed to extract token from URL", e);
//            return null;
//        }
//    }
}