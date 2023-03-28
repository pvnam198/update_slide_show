package com.example.slide.ui.select_music.model;

import java.io.Serializable;

public class MusicAlbum implements Serializable {

    private long id;

    private String name;

    private String arlUrl;

    private int songNumber;

    private boolean hasSongs;

    private String artist;

    public MusicAlbum(long id, String name, String arlUrl, int songNumber, String artist) {
        this.id = id;
        this.name = name;
        this.arlUrl = arlUrl;
        this.songNumber = songNumber;
        this.artist = artist;
        hasSongs = false;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArlUrl() {
        return arlUrl;
    }

    public void setArlUrl(String arlUrl) {
        this.arlUrl = arlUrl;
    }

    public void setSongNumber(int songNumber) {
        this.songNumber = songNumber;
    }

    public String getName() {
        return name;
    }

    public int getSongNumber() {
        return songNumber;
    }

    public synchronized void increaseSongNumber() {
        songNumber++;
    }

    public boolean isHasSongs() {
        return hasSongs;
    }

    public void setHasSongs(boolean hasSongs) {
        this.hasSongs = hasSongs;
    }

}
