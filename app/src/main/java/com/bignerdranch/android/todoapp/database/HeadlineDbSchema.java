package com.bignerdranch.android.todoapp.database;

public class HeadlineDbSchema {

    public static final class HeadlineTable {
        public static final String NAME = "headlines";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String COUNTER = "counter";
        }

    }
}
