package com.bignerdranch.android.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.todoapp.database.HeadlineBaseHelper;
import com.bignerdranch.android.todoapp.database.HeadlineCursorWrapper;
import com.bignerdranch.android.todoapp.database.HeadlineDbSchema;
import com.bignerdranch.android.todoapp.database.HeadlineDbSchema.HeadlineTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeadlineLab {

    private static HeadlineLab sHeadlineLab;

    private  List<Headline> mSingletonHeadlines;


    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static HeadlineLab get(Context context) {
        if (sHeadlineLab == null) {
            sHeadlineLab = new HeadlineLab(context);
        }
        return sHeadlineLab;
    }

    private HeadlineLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new HeadlineBaseHelper(mContext).getWritableDatabase();

//        setTestSingletonHeadlines();
    }

    public void addHeadline(Headline headline) {
        ContentValues values = getContentValues(headline);
        mDatabase.insert(HeadlineTable.NAME, null, values);
    }

    public void deleteHeadline(Headline headline) {
        ToDoLab toDoLab = ToDoLab.get(mContext);
        List<ToDo> toDoes = toDoLab.getToDoesWithSuchParentId(headline.getId(), null);
        for (ToDo toDo : toDoes) {
            toDoLab.deleteToDo(toDo);
        }
        String uuidString = headline.getId().toString();
        mDatabase.delete(HeadlineTable.NAME, HeadlineTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteAll() {
        List<Headline> allHeadlines = getHeadlines();
        for (Headline headline : allHeadlines) {
            deleteHeadline(headline);
        }
    }

    public void updateHeadline(Headline headline) {
        String uuidHeadline = headline.getId().toString();
        ContentValues values = getContentValues(headline);

        mDatabase.update(HeadlineTable.NAME, values, HeadlineTable.Cols.UUID + " = ?", new String[]{uuidHeadline});
    }

    private HeadlineCursorWrapper queryHeadlines(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                HeadlineTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new HeadlineCursorWrapper(cursor);
    }

    public List<Headline> getHeadlines() {
        List<Headline> headlines = new ArrayList<>();

        HeadlineCursorWrapper cursorWrapper = queryHeadlines(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                headlines.add(cursorWrapper.getHeadline());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }

        return headlines;
    }


    public Headline getHeadline(UUID uuid) {
        HeadlineCursorWrapper cursorWrapper = queryHeadlines(
                HeadlineTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );

        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }

            cursorWrapper.moveToFirst();
            return cursorWrapper.getHeadline();
        }finally {
            cursorWrapper.close();
        }
    }

    public static HeadlineLab get() {
        if (sHeadlineLab == null) {
            sHeadlineLab = new HeadlineLab();
        }
        return sHeadlineLab;
    }

    private HeadlineLab() {

    }

    private static ContentValues getContentValues(Headline headline) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HeadlineTable.Cols.UUID, headline.getId().toString());
        contentValues.put(HeadlineTable.Cols.TITLE, headline.getTitle());
        contentValues.put(HeadlineTable.Cols.COUNTER, headline.getCounter());
        return contentValues;
    }

    private Headline addSingletonHeadline(String title, int counter) {
        Headline headline = new Headline();
        headline.setTitle(title);
        headline.setCounter(counter);
        ContentValues values = getContentValues(headline);
        mDatabase.insert(HeadlineTable.NAME, null, values);

        return headline;

    }

    public void setTestSingletonHeadlines() {
        mSingletonHeadlines = new ArrayList<>();
        mSingletonHeadlines.add(addSingletonHeadline("Work", 3));
        mSingletonHeadlines.add(addSingletonHeadline("Private", 4));
        mSingletonHeadlines.add(addSingletonHeadline("Shopping", 5));
        Preferences.setSingletonArray(mContext, mSingletonHeadlines);

    }

    public List<Headline> getSingletonHeadlines() {
        return mSingletonHeadlines;
    }

    public void deleteTestSingletonHeadlines() {
        List<Headline> headlines = Preferences.getSingletonArray(mContext);
        for (Headline headline : headlines) {
            try {
                deleteHeadline(headline);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





}
