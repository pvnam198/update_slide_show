package com.example.slide.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharkVideoDao extends BaseDao<SharkVideo, Integer> implements DatabaseConstants {

    private static SharkVideoDao instance;

    public static synchronized SharkVideoDao getInstance(Context context) {
        return instance == null ? instance = new SharkVideoDao(context) : instance;
    }

    public SharkVideoDao(Context context) {
        super(context, TABLE_VIDEO);
    }

    @Override
    public boolean create(SharkVideo newItem) {
        ContentValues values = new ContentValues();
        values.put(VideoColumns.VIDEO_ID, newItem.getVideoId());
        database = getWritableDatabase();
        boolean createSuccessful = database.insert(tableName, null, values) > 0;
        database.close();
        return createSuccessful;
    }

    public boolean create(Long videoId) {
        Log.d("kimkakadata","videoId id: "+videoId);
        ContentValues values = new ContentValues();
        values.put(VideoColumns.VIDEO_ID, videoId);
        database = getWritableDatabase();
        boolean createSuccessful = database.insert(tableName, null, values) > 0;
        database.close();
        return createSuccessful;
    }

    @Override
    public boolean update(SharkVideo currentItem) {
        return false;
    }

    @Override
    public SharkVideo findById(Integer integer) {
        return null;
    }

    public List<String> getList() {
        Log.d("kimkakadata","getList: ");
        List<String> recordsList = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " ORDER BY id ASC";
        database = getWritableDatabase();
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                long videoId = cursor.getLong(cursor.getColumnIndex(VideoColumns.VIDEO_ID));
                recordsList.add("" + videoId);
                Log.d("kimkakadata","list id: "+videoId);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return recordsList;
    }

    public boolean deleteByVideoId(long videoId) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = getWritableDatabase();
        deleteSuccessful = db.delete(tableName, VideoColumns.VIDEO_ID + " = ?", new String[]{"" + videoId}) > 0;
        db.close();
        return deleteSuccessful;
    }

    public boolean deleteMultiByVideoId(ArrayList<Long> ids) {
        String[] songIds = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            songIds[i] = "" + ids.get(i);
        }
        String where = String.format(VideoColumns.VIDEO_ID + " in (%s)", TextUtils.join(",", Collections.nCopies(ids.size(), "?")));
        database = getWritableDatabase();
        boolean deleteSuccessful = database.delete(tableName, where, songIds) > 0;
        database.close();
        return deleteSuccessful;
    }

    public boolean deleteByVideoId(Context context, long videoId) {
        boolean deleteSuccessful;
        SQLiteDatabase db = getWritableDatabase();
        deleteSuccessful = db.delete(tableName, VideoColumns.VIDEO_ID + " ='" + videoId + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }
}
