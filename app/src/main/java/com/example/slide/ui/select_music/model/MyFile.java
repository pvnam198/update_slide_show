package com.example.slide.ui.select_music.model;

public class MyFile {

    private String url;

    private String fileName;

    private boolean isFolder;

    private boolean isSongFolder;

    public MyFile(String url) {
        this.url = url;
    }

    public MyFile(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public MyFile(String url, String fileName, boolean isFolder) {
        this.url = url;
        this.fileName = fileName;
        this.isFolder = isFolder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isSongFolder() {
        return isSongFolder;
    }

    public void setSongFolder(boolean songFolder) {
        isSongFolder = songFolder;
    }
}
