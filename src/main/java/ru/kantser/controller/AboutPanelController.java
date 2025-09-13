package ru.kantser.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutPanelController {
    private static final Logger logger = LoggerFactory.getLogger(AboutPanelController.class);

    @FXML
    private Label aboutVersion;

    @FXML
    public void initialize() {
        aboutVersion.setText("Версия " + getClass().getPackage().getImplementationVersion() +" ");
    }
}
