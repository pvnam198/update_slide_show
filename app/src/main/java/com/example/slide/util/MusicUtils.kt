package com.example.slide.util

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import java.io.File
import java.util.zip.CRC32

object MusicUtils {

    fun getTrackFromPath(context: Context, path: String): Track {
        var track: Track? = null
        var cursor: Cursor? = null
        val file = File(path)
        val uri = Uri.fromFile(file)
        if (uri == null) {
            track = Track(file.absolutePath.hashCode(), file.absolutePath)
            track.setTitle(file.name)
            return track
        }

        val SONG_ID = MediaStore.Audio.Media._ID
        val SONG_TITLE = MediaStore.Audio.Media.TITLE
        val SONG_ARTIST = MediaStore.Audio.Media.ARTIST
        val SONG_ALBUM = MediaStore.Audio.Media.ALBUM
        val ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID
        val SONG_YEAR = MediaStore.Audio.Media.YEAR
        val SONG_TRACK_NO = MediaStore.Audio.Media.TRACK
        val SONG_FILEPATH = MediaStore.Audio.Media.DATA
        val SONG_DURATION = MediaStore.Audio.Media.DURATION
        val columns = arrayOf(
            SONG_ID,
            SONG_TITLE,
            SONG_ARTIST,
            SONG_ALBUM,
            ALBUM_ID,
            SONG_YEAR,
            SONG_TRACK_NO,
            SONG_FILEPATH,
            SONG_DURATION
        )


        if (uri.scheme == "content") try {
            cursor = context.contentResolver.query(uri, columns, null, null, null)
        } catch (e: SecurityException) {
            // we do not have read permission - just return a null cursor
        }
        if (uri.scheme == "file" && uri.path!=null) {
            cursor = getCursorForFileQuery(uri.path!!, columns)
        }

        if (cursor != null) {
            if (cursor.moveToNext()) {
                track = Track(
                    cursor.getInt(cursor.getColumnIndex(SONG_ID)),
                    cursor.getString(cursor.getColumnIndex(SONG_FILEPATH))
                )
                track.setTitle(cursor.getString(cursor.getColumnIndex(SONG_TITLE)))
                track.setArtist(cursor.getString(cursor.getColumnIndex(SONG_ARTIST)))
                track.setAlbum(cursor.getString(cursor.getColumnIndex(SONG_ALBUM)))
                track.setAlbumId(cursor.getLong(cursor.getColumnIndex(ALBUM_ID)))
                track.setDuration(cursor.getInt(cursor.getColumnIndex(SONG_DURATION)))
                track.setAlbumArt(LocalMusicProvider.getInstance().getAlbumArtOfSong(track))
            }
            cursor.close()
        }
        if (track == null) {
            track = Track(file.absolutePath.hashCode(), file.absolutePath)
            track.title = file.name
        }
        return track
    }

    private fun getCursorForFileQuery(
        path: String,
        columns: Array<String>
    ): Cursor? {
        val matrixCursor = MatrixCursor(columns)
        val data = MediaMetadataRetriever()
        try {
            data.setDataSource(path)
        } catch (e: Exception) {
        }
        val title = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val album = data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        val artist =
            data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val duration =
            data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        if (duration != null) { // looks like we will be able to play this file
            val crc = CRC32()
            crc.update(path.toByteArray())
            val songId =
                (2 + crc.value) * -1 // must at least be -2 (-1 defines Song-Object to be empty)
            val objData =
                arrayOf(songId, null, "", "", 0, 0, 0, path, 0)
            if (title != null) objData[1] = title
            if (artist != null) objData[2] = artist
            if (album != null) objData[3] = album
            if (duration != null) objData[8] = duration.toLong(10)
            matrixCursor.addRow(objData)
        }
        return matrixCursor
    }
}