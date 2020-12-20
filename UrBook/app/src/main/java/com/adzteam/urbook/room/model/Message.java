package com.adzteam.urbook.room.model;

import com.google.firebase.Timestamp;

public class Message {
    protected Long mDate;
    private String mCreator;
    private String mName;
    protected String mContent;

    public Message(Long mDate, String mCreator, String mName, String mContent) {
        this.mDate = mDate;
        this.mCreator = mCreator;
        this.mName = mName;
        this.mContent = mContent;
    }

    public String getDate() {
        return mDate.toString();
    }

    public String getCreator() {
        return mCreator;
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }
}
