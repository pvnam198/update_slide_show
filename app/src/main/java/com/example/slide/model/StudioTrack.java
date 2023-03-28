package com.example.slide.model;


import java.io.Serializable;

public class StudioTrack implements Serializable {
    protected int id;
    protected String title;
    protected String url;
    protected int duration;


    private long dateAdded;
    private boolean isSelected;
    private boolean isMusic;

    protected String albumArt;

    private String album = "";
    private String artist = "";
    private long albumId;


    public StudioTrack() {
        isSelected = false;
    }

    public StudioTrack(int id, String url) {
        this.id = id;
        this.url = url;
        isSelected = false;
    }


    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getTitle() {
        return title;
    }

    public String getExtension() {
        if (url.lastIndexOf(".") != -1)
            return url.substring(url.lastIndexOf("."));
        else return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }
}
