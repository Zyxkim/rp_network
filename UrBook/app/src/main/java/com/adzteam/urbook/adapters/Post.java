package com.adzteam.urbook.adapters;

public class Post {
    protected Long mDate;
    private String mId;
    private String mCreator;
    private String mName;
    protected String mCharacterName;
    protected String mContent;
    private Boolean mIsThereImage;

    public Post(String id, Long mDate, String mName, String mCreator, String mCharacterName, String mContent, Boolean isThereImage) {
        this.mId = id;
        this.mDate = mDate;
        this.mCreator = mCreator;
        this.mName = mName;
        this.mCharacterName = mCharacterName;
        this.mContent = mContent;
        this.mIsThereImage = isThereImage;
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

    public String getId() { return mId; }

    public Boolean isThereImage() {
        return mIsThereImage;
    }
}