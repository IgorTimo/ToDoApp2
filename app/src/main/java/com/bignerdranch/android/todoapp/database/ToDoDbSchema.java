package com.bignerdranch.android.todoapp.database;

public class ToDoDbSchema {
    public static final class ToDoTable {
        public static final String NAME = "todoes";

        public static final class Cols {
            public static final String PARENT_UUID = "parent";
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DONE = "done";
        }

    }
}
