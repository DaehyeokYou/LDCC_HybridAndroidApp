package com.ydh.example;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by n on 2016-01-22.
 */
public class dbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "myDB.db";
    private static final String TABLE_NAME = "myTable";
    private static final int DB_VERSION = 1;

    public dbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS "+TABLE_NAME+" ( id INTEGER PRIMARY KEY AUTOINCREMENT, market_idx INTEGER not null, lat REAL null, lng REAL null, flag INTEGER not null, product_idx INTEGER not null); ");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists myTable");
        db.execSQL("create table IF NOT EXISTS "+TABLE_NAME+" ( id INTEGER PRIMARY KEY AUTOINCREMENT, market_idx INTEGER not null, lat REAL null, lng REAL null, flag INTEGER not null, product_idx INTEGER not null); ");
    }
}
