package com.bignerdranch.android.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.todoapp.database.ToDoBaseHelper;
import com.bignerdranch.android.todoapp.database.ToDoCursorWrapper;
import com.bignerdranch.android.todoapp.database.ToDoDbSchema.ToDoTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToDoLab {

    private static ToDoLab sToDoLab;


    private Context mContext;
    private SQLiteDatabase mToDoDatabase;

    public static ToDoLab get(Context context) {
        if (sToDoLab == null) {
            sToDoLab = new ToDoLab(context);
        }
        return sToDoLab;
    }

    private ToDoLab(Context context) {
        mContext = context.getApplicationContext();
        mToDoDatabase = new ToDoBaseHelper(mContext).getWritableDatabase();
//        setTestSingletonToDoes();
    }

    public void addToDo(ToDo toDo) {
        ContentValues values = getContentValues(toDo);
        mToDoDatabase.insert(ToDoTable.NAME, null, values);
    }

    public void deleteToDo(ToDo toDo) {
        String uuidString = toDo.getId().toString();
        mToDoDatabase.delete(ToDoTable.NAME, ToDoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteAllToDoWithSuchParent(Headline headline) {
        String uuidString = headline.getId().toString();
        mToDoDatabase.delete(ToDoTable.NAME, ToDoTable.Cols.PARENT_UUID + " = ?", new String[]{uuidString});

    }

    public void deleteAllDoneToDo(Headline headline) {
        List<ToDo> doneToDoes = getDoneToDoesWithSuchParentId(headline.getId());
        for (ToDo toDo : doneToDoes) {
            deleteToDo(toDo);
        }
    }

    public List<ToDo> getToDoes() {
        List<ToDo> toDoes = new ArrayList<>();

        ToDoCursorWrapper cursor = queryTodoes( null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                toDoes.add(cursor.getToDo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return toDoes;

    }

    public ToDo getToDo(UUID uuid) {
        ToDoCursorWrapper cursor = queryTodoes( ToDoTable.Cols.UUID + " = ? ", new String[]{uuid.toString()}, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getToDo();
        } finally {
            cursor.close();
        }

    }

    public void updateToDo(ToDo toDo) {
        String uuidString = toDo.getId().toString();
        ContentValues values = getContentValues(toDo);

        mToDoDatabase.update(ToDoTable.NAME,
                values,
                ToDoTable.Cols.UUID + " = ?",
                new String[]{uuidString}
        );
    }

    private ToDoCursorWrapper queryTodoes( String whereClause, String[] whereArgs, String groupBy) {
        Cursor cursor = mToDoDatabase.query(
                ToDoTable.NAME,
                null,
                whereClause,
                whereArgs,
                groupBy,
                null,
                null
        );
        return new ToDoCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(ToDo toDo) {
        ContentValues values = new ContentValues();
        values.put(ToDoTable.Cols.PARENT_UUID, toDo.getParentId().toString());
        values.put(ToDoTable.Cols.UUID, toDo.getId().toString());
        values.put(ToDoTable.Cols.TITLE, toDo.getTitle());
        values.put(ToDoTable.Cols.DATE, toDo.getDate().getTime());
        values.put(ToDoTable.Cols.DONE, toDo.isDone() ? 1 : 0);

        return values;
    }

    public List<ToDo> getToDoesWithSuchParentId(UUID uuid, String groupBy) {
        List<ToDo> toDoes = new ArrayList<>();

        ToDoCursorWrapper cursor = queryTodoes( ToDoTable.Cols.PARENT_UUID + " = ? ", new String[]{uuid.toString()}, groupBy);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                toDoes.add(cursor.getToDo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return toDoes;
    }

    public List<ToDo> getDoneToDoesWithSuchParentId(UUID uuid) {
        List<ToDo> allToDoes = getToDoesWithSuchParentId(uuid, null);
        List<ToDo> doneToDoes = new ArrayList<>();
        for (ToDo toDo : allToDoes) {
            if (toDo.isDone()) {
                doneToDoes.add(toDo);
            }
        }

        return doneToDoes;
    }

    private void addSingletonToDo(Headline parent, String title) {
        ToDo toDo = new ToDo(parent, title);
        ContentValues values = getContentValues(toDo);
        mToDoDatabase.insert(ToDoTable.NAME, null, values);
    }

    public void setTestSingletonToDoes() {

        List<Headline> headlineList = HeadlineLab.get().getSingletonHeadlines();
        addSingletonToDo(headlineList.get(0), "To  do some Strange thing with my dick");
        addSingletonToDo(headlineList.get(0), "Work///second to do");
        addSingletonToDo(headlineList.get(0), "Work///third to do");
        addSingletonToDo(headlineList.get(1), "Private///1 to do");
        addSingletonToDo(headlineList.get(1), "Private///2 to do");
        addSingletonToDo(headlineList.get(1), "Private///3 to do");
        addSingletonToDo(headlineList.get(1), "Private///4 to do");
        addSingletonToDo(headlineList.get(2), "Shopping///1 to do");
        addSingletonToDo(headlineList.get(2), "Shopping///2 to do");
        addSingletonToDo(headlineList.get(2), "Shopping///3 to do");
        addSingletonToDo(headlineList.get(2), "Shopping///4 to do");
        addSingletonToDo(headlineList.get(2), "Shopping///5 to do");
    }

}
