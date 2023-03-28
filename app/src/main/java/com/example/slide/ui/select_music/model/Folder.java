package com.example.slide.ui.select_music.model;

import java.io.Serializable;

public class Folder implements Serializable {
    private String url;
    private String name;
    private int songNumber;

    public Folder() {
        songNumber=0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongNumber() {
        return songNumber;
    }

    public void setSongNumber(int songNumber) {
        this.songNumber = songNumber;
    }

    public void increaseSongNumber() {
        songNumber++;
    }
}
