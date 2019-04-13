package com.bignerdranch.android.todoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.bignerdranch.android.todoapp.database.HeadlineDbSchema.HeadlineTable;

public class HeadlineBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "headlineBase.db";


    public HeadlineBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + HeadlineTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                HeadlineTable.Cols.UUID + ", " +
                HeadlineTable.Cols.TITLE + ", " +
                HeadlineTable.Cols.COUNTER + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
