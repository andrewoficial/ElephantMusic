package ru.kantser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class AppSettings {
    @JsonProperty("language")
    @Getter @Setter
    private String language;
    
    @JsonProperty("lastFmName")
    @Getter @Setter
    private String lastFmName;
    
    @JsonProperty("lastFmToken")
    @Getter @Setter
    private String lastFmToken;

}