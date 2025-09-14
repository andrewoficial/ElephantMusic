package ru.kantser.elephantmusic.service;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class FxmlLoaderHelper {
    private static Injector injector;
    
    public static void setInjector(Injector injector) {
        FxmlLoaderHelper.injector = injector;
    }
    
    public static <T> T load(String fxmlPath) throws IOException {
        //getClass().getResource("/ru/igm/view/about_panel.fxml")
        //FXMLLoader loader = new FXMLLoader(FxmlLoaderHelper.getCl.getResource(fxmlPath));
        //URL resource = Main.class.getClassLoader().getResource("GUI_Images/Pic.png");

        //URL resourceUrl = FxmlLoaderHelper.class.getClassLoader().getResource("ru/igm/view/about_panel.fxml");
        URL resourceUrl = FxmlLoaderHelper.class.getResource(fxmlPath);
        System.out.println("LINK " + resourceUrl);

        return load(resourceUrl);
    }
    
    public static <T> T load(URL fxmlUrl) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setControllerFactory(type -> injector.getInstance(type));
        return loader.load();
    }
}