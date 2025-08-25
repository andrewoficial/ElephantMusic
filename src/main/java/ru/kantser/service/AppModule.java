package ru.kantser.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;


public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlaylistService.class).in(Singleton.class);
        bind(AudioPlayerService.class).in(Singleton.class);
        bind(WindowTitleService.class).in(Singleton.class);
    }
}