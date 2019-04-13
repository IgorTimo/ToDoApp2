package com.bignerdranch.android.todoapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.todoapp.Headline;

import java.util.UUID;

public class HeadlineCursorWrapper extends CursorWrapper {

    public HeadlineCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Headline getHeadline() {

        String uuidString = getString(getColumnIndex(HeadlineDbSchema.HeadlineTable.Cols.UUID));
        String title = getString(getColumnIndex(HeadlineDbSchema.HeadlineTable.Cols.TITLE));
        int counter = getInt(getColumnIndex(HeadlineDbSchema.HeadlineTable.Cols.COUNTER));

        Headline headline = new Headline(UUID.fromString(uuidString));
        headline.setTitle(title);
        headline.setCounter(counter);

        return headline;
    }
}
