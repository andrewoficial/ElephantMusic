package ru.kantser.elephantmusic.model;

public class LastFmAuth {
    private String sessionKey;
    private String username;
    
    // Конструкторы, геттеры и сеттеры
    public LastFmAuth() {}
    
    public LastFmAuth(String sessionKey, String username) {
        this.sessionKey = sessionKey;
        this.username = username;
    }
    
    public String getSessionKey() { return sessionKey; }
    public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public boolean isValid() {
        return sessionKey != null && !sessionKey.isEmpty() && 
               username != null && !username.isEmpty();
    }
}