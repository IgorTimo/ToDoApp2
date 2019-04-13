package com.bignerdranch.android.todoapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.todoapp.ToDo;
import com.bignerdranch.android.todoapp.database.ToDoDbSchema.ToDoTable;

import java.util.Date;
import java.util.UUID;

public class ToDoCursorWrapper extends CursorWrapper {
    public ToDoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ToDo getToDo() {
        String uuidParentString = getString(getColumnIndex(ToDoTable.Cols.PARENT_UUID));
        String uuidString = getString(getColumnIndex(ToDoTable.Cols.UUID));
        String title = getString(getColumnIndex(ToDoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ToDoTable.Cols.DATE));
        int isDone = getInt(getColumnIndex(ToDoTable.Cols.DONE));

        ToDo toDo = new ToDo(UUID.fromString(uuidParentString), UUID.fromString(uuidString));
        toDo.setTitle(title);
        toDo.setDate(new Date(date));
        toDo.setDone(isDone != 0);

        return toDo;
    }
}
