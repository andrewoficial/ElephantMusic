package ru.kantser.service;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// WindowTitleService.java
public class WindowTitleService {
    private static final Logger log = LoggerFactory.getLogger(WindowTitleService.class);
    private Stage primaryStage;
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void updateTitle(String status) {
        if(status == null){
            return;
        }
        if (primaryStage != null) {
            Platform.runLater(() -> {
                primaryStage.setTitle("Музыкальный проигрыватель - " + status);
            });
        }else {
            log.warn("Primary stage IS NULL!!");
        }
    }
}