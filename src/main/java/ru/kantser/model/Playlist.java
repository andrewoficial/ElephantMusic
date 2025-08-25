package ru.kantser.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private ObservableList<Track> tracks = FXCollections.observableArrayList();
    private String name;
    
    public Playlist(String name) {
        this.name = name;
    }
    
    public ObservableList<Track> getTracks() {
        return tracks;
    }
    
    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    public void removeTrack(Track track) {
        tracks.remove(track);
    }
    
    public void clear() {
        tracks.clear();
    }
    
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}