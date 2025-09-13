package ru.kantser.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Playlist {

    private ObservableList<Track> tracks = FXCollections.observableArrayList();
    private String name;


    // Конструктор без аргументов для Jackson
    public Playlist() {
        this.name = "Без названия";
    }

    // Конструктор с параметром
    @JsonCreator
    public Playlist(@JsonProperty("name") String name,
                    @JsonProperty("tracks") List<Track> tracks) {
        this.name = name;
        this.tracks = FXCollections.observableArrayList(tracks);
    }

    // Сеттер для tracks, который принимает List и преобразует в ObservableList
    @JsonSetter
    public void setTracks(List<Track> tracks) {
        this.tracks = FXCollections.observableArrayList(tracks);
    }

    @JsonIgnore
    public void setTracks(ObservableList<Track> tracks) {
        this.tracks = tracks;
    }

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

    public boolean isEmpty(){
        return tracks.isEmpty();
    }
}