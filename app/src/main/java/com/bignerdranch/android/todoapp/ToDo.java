package com.bignerdranch.android.todoapp;

import java.util.Date;
import java.util.UUID;

public class ToDo {
    private UUID mParentId;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean isDone;

    public ToDo( UUID id) {
        mId = id;
        mDate = new Date();
    }

    public ToDo(Headline parent) {
        this(UUID.randomUUID());
        mParentId = parent.getId();
    }

    public ToDo() {
        this(UUID.randomUUID());
    }

    public ToDo(UUID parentId, UUID id) {
        mParentId = parentId;
        mId = id;
        mDate = new Date();
    }

    public UUID getParentId() {
        return mParentId;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public ToDo(Headline headline, String title) {
        mParentId = headline.getId();
        mId = UUID.randomUUID();
        mDate = new Date();
        mTitle = title;
    }
}
