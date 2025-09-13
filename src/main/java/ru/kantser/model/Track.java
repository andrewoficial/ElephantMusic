package ru.kantser.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.time.Duration;

public class Track {
    // Getters and setters
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String artist;
    @Getter
    @Setter
    private Path filePath;
    @Getter
    @Setter
    private Duration duration;

    // Конструктор без аргументов для Jackson
    public Track() {}

    public Track(String title, String artist, Path filePath, Duration duration) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return artist + " - " + title;
    }
}