package ru.kantser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    public static void main(String[] args) {
        logger.info("Передаю загрузку из Launcher в MyApplication");
        ru.kantser.MyApplication.main(args);
    }
}