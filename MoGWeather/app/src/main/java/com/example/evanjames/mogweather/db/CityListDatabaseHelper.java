package com.example.evanjames.mogweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EvanJames on 2015/9/3.
 */
public class CityListDatabaseHelper extends SQLiteOpenHelper {

    private final Context mContext;
    public static final String CREAT_CITYLIST = "create table CityList (" +
            "id integer primary key autoincrement, " +
            "cityname text)";

    public CityListDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_CITYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
