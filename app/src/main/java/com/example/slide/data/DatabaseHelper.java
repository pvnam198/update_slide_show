package com.example.slide.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SEV_USER on 1/25/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseConstants {

    private static final int DATABASE_VERSION = 1;

    protected static final String DATABASE_NAME = "sharkdatabase";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("kimkakadatabase", "oncreate");
        String sql = "CREATE TABLE " + TABLE_VIDEO +
                " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideoColumns.VIDEO_ID + " INTEGER); ";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_VIDEO;
        db.execSQL(sql);
        onCreate(db);
    }

}