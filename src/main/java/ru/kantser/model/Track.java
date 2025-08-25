package ru.kantser.model;

import java.nio.file.Path;
import java.time.Duration;

public class Track {
    private String title;
    private String artist;
    private Path filePath;
    private Duration duration;
    
    public Track(String title, String artist, Path filePath, Duration duration) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.duration = duration;
    }
    
    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public Path getFilePath() { return filePath; }
    public void setFilePath(Path filePath) { this.filePath = filePath; }
    
    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }
    
    @Override
    public String toString() {
        return artist + " - " + title;
    }
}