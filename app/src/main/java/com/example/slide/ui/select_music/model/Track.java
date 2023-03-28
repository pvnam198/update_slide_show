package com.example.slide.ui.select_music.model;

import java.io.File;
import java.io.Serializable;

public class Track implements Serializable {
    protected String title;
    protected int id;
    protected String url;
    protected int duration;

    protected String albumArt;

    private String genre;

    private String album = "";
    private String artist = "";
    private long albumId;

    private int artistId;

    private long dateAdded;
    private boolean isMusic;
    private boolean isSelected;
    private boolean isExists;

    public Track() {
        isSelected = false;
    }

    public Track(int id, String url) {
        this.id = id;
        this.url = url;
        isSelected = false;
    }

    public void updateExists() {
        isExists = (new File(url)).exists();
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    public String getTitle() {
        return title;
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

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", isExists=" + isExists +
                '}';
    }
}
