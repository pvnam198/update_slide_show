package com.example.slide.ui.select_music.provider.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.slide.R;
import com.example.slide.ui.select_music.event.SongLoadedEvent;
import com.example.slide.ui.select_music.event.SongLoadingEvent;
import com.example.slide.ui.select_music.model.Artist;
import com.example.slide.ui.select_music.model.Folder;
import com.example.slide.ui.select_music.model.MusicAlbum;
import com.example.slide.ui.select_music.model.Track;
import com.example.slide.ui.select_music.provider.ILocalMusicProvider;
import com.example.slide.ui.select_music.provider.MusicConstants;
import com.example.slide.ui.select_music.provider.ProviderState;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class LocalMusicProvider implements ILocalMusicProvider, ProviderState {

    private int state = NONE;

    public static LocalMusicProvider instance;

    private Context context;

    private List<MusicAlbum> albums = new ArrayList<>();

    //all
    private List<Track> tracks = new ArrayList<>();
    //search
    private List<Track> searchTracks = new ArrayList<>();
    //music
    private List<Track> songs = new ArrayList<>();

    private List<Folder> folders = new ArrayList<>();

    public List<Artist> artists = new ArrayList<>();

    public LocalMusicProvider(Context context) {
        this.context = context;
    }

    public static synchronized void createInstance(Context context) {
        synchronized (LocalMusicProvider.class) {
            if (instance == null) {
                instance = new LocalMusicProvider(context);
            }
        }
    }

    public void init() {
        if (state == LOADED || state == LOADING) {
            return;
        }
        state = LOADING;
        EventBus.getDefault().post(new SongLoadingEvent());
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver resolver = context.getContentResolver();
        try {
            scanAlbum(resolver);
            scanSongs(resolver, musicUri);
            scanArtist(resolver);
            scanFolder();
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d("kimkakatest", "e" + e.getMessage());
            state = LOAD_FAIL;
            return;
        } catch (Exception e) {
            Log.d("kimkakatest", "e2" + e.getMessage());
            e.printStackTrace();
        }
        state = LOADED;
        EventBus.getDefault().post(new SongLoadedEvent());
    }

    public void scanArtist(ContentResolver resolver) {
        artists.clear();
        String[] mProjection =
                {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                };

        Cursor cursor = resolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                mProjection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int songNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                boolean haveSong = false;
                String art = "";
                for (MusicAlbum album : albums) {
                    if (album.getArtist() != null) {
                        if (album.getArtist().equals(artistName)) {
                            haveSong = true;
                            if (!TextUtils.isEmpty(album.getArlUrl())) art = album.getArlUrl();
                        }
                    }
                }

                if (haveSong) {
                    Artist artist = new Artist(artistName, songNumber, art);
                    artists.add(artist);
                }
            } while (cursor.moveToNext());
        } else {
            //TODO: dont find any artist
        }

        if (cursor != null) {
            cursor.close();
        }

    }

    public void reScan() {
        if (state == LOADING) {
            return;
        }
        state = LOADING;
        EventBus.getDefault().post(new SongLoadingEvent());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {

        }
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        try {
            scanAlbum(resolver);
            scanSongs(resolver, musicUri);
            scanArtist(resolver);
            scanFolder();
        } catch (SecurityException e) {
            Log.d("kimkakatest", "e" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("kimkakatest", "e2" + e.getMessage());
        }
        state = LOADED;
        EventBus.getDefault().post(new SongLoadedEvent());
    }

    private void scanAlbum(ContentResolver resolver) {
        albums.clear();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String id = MediaStore.Audio.Albums._ID;
        String name = MediaStore.Audio.Albums.ALBUM;
        String art = MediaStore.Audio.Albums.ALBUM_ART;
        String artist = MediaStore.Audio.AlbumColumns.ARTIST;
        String songsNumber = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        String[] projection = {id, artist, name, art, songsNumber};
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        int index = 1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long albumId = cursor.getLong(cursor.getColumnIndex(id));
                Log.d("kimkakaid", "songid" + albumId);
                String albumName = cursor.getString(cursor.getColumnIndex(name));
                if (TextUtils.isEmpty(albumName))
                    albumName = context.getString(R.string.unknown_album) + "[" + index++ + "]";
                int songNumber = cursor.getInt(cursor.getColumnIndex(songsNumber));
                String albumArt = cursor.getString(cursor.getColumnIndex(art));
                String artistString = cursor.getString(cursor.getColumnIndex(artist));
                MusicAlbum album = new MusicAlbum(albumId, albumName, albumArt, 0, artistString);
                albums.add(album);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

    }

    private void scanSongs(ContentResolver resolver, Uri musicUri) {
        Cursor cursor;
        String SONG_ID = MediaStore.Audio.Media._ID;
        String SONG_TITLE = MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
        String SONG_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;
        String SONG_FILEPATH = MediaStore.Audio.Media.DATA;
        String SONG_DURATION = MediaStore.Audio.Media.DURATION;
        String SONG_DATE_ADDED = MediaStore.Audio.Media.DATE_ADDED;
        String SONG_ARTIST_ID = MediaStore.Audio.AudioColumns.ARTIST_ID;
        String IS_MUSIC = MediaStore.Audio.Media.IS_MUSIC;

        String[] columns = {
                SONG_ID,
                SONG_TITLE,
                SONG_ALBUM,
                SONG_ALBUM_ID,
                SONG_ARTIST,
                SONG_FILEPATH,
                SONG_DATE_ADDED,
                SONG_DURATION,
                SONG_ARTIST_ID,
                IS_MUSIC
        };

        final String durationHigh = SONG_DURATION + ">1000";

        // Actually querying the system
        cursor = resolver.query(musicUri, columns, durationHigh, null, SONG_TITLE + " ASC");
        tracks.clear();
        songs.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String filePath = cursor.getString(cursor.getColumnIndex(SONG_FILEPATH));
                if (filePath == null) continue;
                if (!filePath.contains("/")) continue;
                if (filePath.length() < 2) continue;
                String prefix = filePath.substring(filePath.lastIndexOf("."));
                if (!MusicConstants.INSTANCE.getMUSIC_TYPE().contains(prefix.toLowerCase()))
                    continue;
                // Creating a track from the values on the row
                Track track = new Track(cursor.getInt(cursor.getColumnIndex(SONG_ID)), filePath);

                track.setTitle(cursor.getString(cursor.getColumnIndex(SONG_TITLE)));
                track.setAlbum(cursor.getString(cursor.getColumnIndex(SONG_ALBUM)));
                track.setArtist(cursor.getString(cursor.getColumnIndex(SONG_ARTIST)));
                track.setAlbumId(cursor.getLong(cursor.getColumnIndex(SONG_ALBUM_ID)));
                track.setDateAdded(cursor.getLong(cursor.getColumnIndex(SONG_DATE_ADDED)));
                track.setDuration(cursor.getInt(cursor.getColumnIndex(SONG_DURATION)));
                track.setArtistId(cursor.getInt(cursor.getColumnIndex(SONG_ARTIST_ID)));
                track.setMusic(cursor.getInt(cursor.getColumnIndex(IS_MUSIC)) == 1);
                for (MusicAlbum album : albums) {
                    if (track.getAlbumId() == album.getId()) {
                        Log.d("kimkakascan", album.getName() + ": " + track.getTitle() + "increase");
                        track.setAlbumArt(album.getArlUrl());
                        album.setHasSongs(true);
                        album.increaseSongNumber();
                    }
                }
                if (track.isMusic()) {
                    songs.add(track);
                }
                tracks.add(track);

                Log.d("kimkakatest", "song: " + track.toString());

            }

            while (cursor.moveToNext());

            for (Iterator<MusicAlbum> it = albums.iterator(); it.hasNext(); ) {
                MusicAlbum album = it.next();
                if (!album.isHasSongs()) {
                    it.remove();
                }
            }
        } else {
            //TODO: dont find any song
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    public void scanFolder() {
        List<Track> TracksCopy = new ArrayList<>();
        TracksCopy.addAll(tracks);
        sortSongListAccordingFolder(TracksCopy);
        folders.clear();
        String lastFolder = "";
        int fIndex = -1;
        for (int i = 0; i < TracksCopy.size(); i++) {
            Track song = TracksCopy.get(i);
            File f = new File(song.getUrl());
            String path = f.getPath();
            if (path == null) continue;
            path = path.substring(1, path.lastIndexOf(f.getName()) - 1);
            if (path == null) continue;
            if (!lastFolder.equalsIgnoreCase(path)) {
                lastFolder = path;
                Folder folder = new Folder();
                int p = path.lastIndexOf("/") + 1;
                if (p < 0)
                    p = 0;
                folder.setName(path.substring(p, path.length()));

                folder.setUrl(path);
                folder.increaseSongNumber();
                folders.add(folder);
                fIndex++;
            } else {
                folders.get(fIndex).increaseSongNumber();
            }

        }

    }

    private void sortSongListAccordingFolder(List<Track> list) {
        if (list == null || list.size() == 0) return;

        if (list == null || list.size() == 0) return;
        Track[] l = new Track[list.size()];
        list.toArray(l);
        ArraySorter.sort(l, 0, list.size(), new ArraySorter.Comparer<Track>() {
            @Override
            public int compare(Track lhs, Track rhs) {
                if (lhs == null || rhs == null) return 0;
                String p1 = lhs.getUrl().substring(0, lhs.getUrl().lastIndexOf("/"));
                String p2 = rhs.getUrl().substring(0, rhs.getUrl().lastIndexOf("/"));
                if (p1.contains(p2)) {
                    return -1;
                } else if (p2.contains(p1)) {
                    return 1;
                }
                return lhs.getUrl().compareToIgnoreCase(rhs.getUrl());
            }
        });
        list.clear();
        Collections.addAll(list, l);
    }

    @Override
    public List<Track> getAllSong() {
        return songs;
    }

    @Override
    public List<Track> getAll() {
        return tracks;
    }

    public List<Track> getSearchTracks() {
        return searchTracks;
    }

    @Override
    public ArrayList<Track> getSongsByFolder(String desiredFolder) {
        ArrayList<Track> songsByFolder = new ArrayList<>();

        for (Track song : tracks) {
            String filePath = song.getUrl();
            if (filePath.substring(1, filePath.lastIndexOf("/")).equals(desiredFolder))
                songsByFolder.add(song);
        }

        return songsByFolder;
    }

    public ArrayList<Track> getSongsByAlbum(String desiredAlbum) {
        ArrayList<Track> songsByAlbum = new ArrayList<>();

        for (Track song : tracks) {
            if (song.getAlbum() != null && song.getAlbum().equals(desiredAlbum))
                songsByAlbum.add(song);
        }

        return songsByAlbum;
    }

    public ArrayList<Track> getSongsByAlbum(long albumId) {
        ArrayList<Track> songsByAlbum = new ArrayList<>();

        for (Track song : tracks) {
            if (song.getAlbumId() == albumId)
                songsByAlbum.add(song);
        }

        return songsByAlbum;
    }

    @Override
    public String getAlbumArtOfSong(Track song) {
        if (song == null || albums == null || albums.size() == 0) return null;
        else {
            for (int i = 0; i < albums.size(); i++) {
                MusicAlbum a = albums.get(i);
                if (a.getId() == song.getAlbumId()) {
                    return a.getArlUrl();
                }
            }
            return null;
        }
    }

    public String getAlbumArtOfAlbum(String albumName) {
        if (TextUtils.isEmpty(albumName) || albums == null || albums.size() == 0) return null;

        for (int i = 0; i < albums.size(); i++) {
            MusicAlbum a = albums.get(i);
            if (a.getName() != null) {
                if (a.getName().equalsIgnoreCase(albumName)) {
                    return a.getArlUrl();
                }
            }
        }
        return null;

    }

    public ArrayList<Track> getSongsByArtist(String desiredArtist) {
        ArrayList<Track> songsByArtist = new ArrayList<>();

        for (Track song : tracks) {
            if (song.getArtist() != null && song.getArtist().toLowerCase().equals(desiredArtist.toLowerCase()))
                songsByArtist.add(song);
        }

        // Sorting resulting list by Album
        Collections.sort(songsByArtist, new Comparator<Track>() {
            public int compare(Track a, Track b) {
                if (a.getAlbum() != null && b.getAlbum() != null) {
                    return a.getAlbum().compareTo(b.getAlbum());
                }
                return 0;
            }
        });

        return songsByArtist;
    }

    public ArrayList<Track> getSongsByGenre(String genre) {
        ArrayList<Track> songsByGenre = new ArrayList<>();

        for (Track song : tracks) {
            if (genre.equalsIgnoreCase(song.getGenre()))
                songsByGenre.add(song);
        }
        return songsByGenre;
    }

    public String getAlbumArtOfAlbum(int albumId) {
        if (albums == null || albums.size() == 0) return null;

        for (int i = 0; i < albums.size(); i++) {
            MusicAlbum a = albums.get(i);
            if (a.getName() != null) {
                if (a.getId() == albumId) {
                    return a.getArlUrl();
                }
            }
        }
        return null;

    }

    public List<MusicAlbum> getAlbums() {
        return albums;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    @Override
    public int getState() {
        return state;
    }

    public boolean isSongLoaded() {
        return state == LOADED;
    }

    public static synchronized LocalMusicProvider getInstance() {
        return instance;
    }

    public List<String> getFolderUrls() {
        List<String> folderUrls = new ArrayList<>();
        for (Folder folder : folders) {
            folderUrls.add(folder.getUrl());
        }
        return folderUrls;
    }

    public void searchSongs(String songName) {
        if (TextUtils.isEmpty(songName)) return;
        searchTracks.clear();
        for (Track song : tracks) {
            if (song.getTitle().toLowerCase().contains(songName.toLowerCase())) {
                searchTracks.add(song);
            } else {
                if (song.getArtist() != null && song.getArtist().toLowerCase().contains(songName.toLowerCase())) {
                    searchTracks.add(song);
                }
            }
        }
    }
}
