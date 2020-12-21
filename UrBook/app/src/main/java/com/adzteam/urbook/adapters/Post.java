package com.adzteam.urbook.adapters;

public class Post {
    protected Long mDate;
    private String mCreator;
    private String mName;
    protected String mCharacterName;
    protected String mContent;

    public Post(Long mDate, String mName, String mCreator, String mCharacterName, String mContent) {
        this.mDate = mDate;
        this.mCreator = mCreator;
        this.mName = mName;
        this.mCharacterName = mCharacterName;
        this.mContent = mContent;
    }

    public String getDate() {
        return mDate.toString();
    }

    public String getCreator() {
        return mCreator;
    }

    public String getCharacterName() {
        return mCharacterName;
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }
}