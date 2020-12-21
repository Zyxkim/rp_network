package com.adzteam.urbook.adapters;

public class Post {
    protected Long mDate;
    private String mCreator;
    protected String mCharacterName;
    protected String mContent;

    public Post(Long mDate, String mCreator, String mCharacterName, String mContent) {
        this.mDate = mDate;
        this.mCreator = mCreator;
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

    public String getContent() {
        return mContent;
    }
}