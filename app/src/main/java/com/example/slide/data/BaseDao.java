package com.example.slide.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.Collections;

public abstract class BaseDao<DOMAIN,ID> extends DatabaseHelper {
    protected String tableName;
    protected SQLiteDatabase database;

    public BaseDao(Context context, String tableName) {
        super(context);
        this.tableName = tableName;
    }

    public int count() {

        SQLiteDatabase database = getReadableDatabase();

        String sql = "SELECT * FROM " +tableName;
        Cursor cursor = database.rawQuery(sql, null);
        int recordCount = cursor.getCount();
        cursor.close();
        database.close();

        return recordCount;

    }

    public abstract boolean create(DOMAIN newItem);
    public abstract boolean update(DOMAIN currentItem);
    public boolean delete(ID id) {
        boolean deleteSuccessful = false;

        SQLiteDatabase db = getWritableDatabase();
        deleteSuccessful = db.delete(tableName, "id ='" + id + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

    public boolean deleteMuti(String[] ids) {
        String where = String.format("id in (%s)", TextUtils.join(",", Collections.nCopies(ids.length, "?")));

        database = getWritableDatabase();

        boolean deleteSuccessful = database.delete(tableName, where, ids) > 0;
        database.close();

        return deleteSuccessful;
    }

    public abstract DOMAIN findById(ID id);
}
