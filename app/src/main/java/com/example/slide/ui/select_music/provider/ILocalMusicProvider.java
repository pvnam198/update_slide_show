package com.example.slide.ui.select_music.provider;


import com.example.slide.ui.select_music.model.Folder;
import com.example.slide.ui.select_music.model.MusicAlbum;
import com.example.slide.ui.select_music.model.Track;

import java.util.ArrayList;
import java.util.List;

public interface ILocalMusicProvider {

    List<Track> getAllSong();

    List<Track> getAll();

    List<Folder> getFolders();

    List<MusicAlbum> getAlbums();

    ArrayList<Track> getSongsByFolder(String desiredFolder);

    ArrayList<Track> getSongsByAlbum(String desiredAlbum);

    String getAlbumArtOfSong(Track song);

}
