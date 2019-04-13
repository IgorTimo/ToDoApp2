package com.bignerdranch.android.todoapp;

import java.util.UUID;

public class Headline {
    private UUID mId;
    private String mTitle;
    private int mCounter;

    public Headline() {
        this(UUID.randomUUID());
    }

    public Headline(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getCounter() {
        return mCounter;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

    public Headline(String title, int counter) {
        mId = UUID.randomUUID();
        mTitle = title;
        mCounter = counter;
    }
}
