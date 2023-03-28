package com.example.slide.ui.select_music.provider;

/**
 * Created by LENOVO on 8/2/2016.
 */
public interface ProviderState {
    int NONE = 0;
    int LOADING = 1;
    int LOADED = 2;
    int LOAD_FAIL = 3;
    int getState();
}
