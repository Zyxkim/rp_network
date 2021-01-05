package com.adzteam.urbook.adapters;

public class Characters {
    protected Long mDate;
    private String mCreator;
    private String mName;
    private String mId;
    private String mFandom;
    protected String mCharacterName;
    protected String mCharacterSurname;
    protected String mContent;
    private Boolean mIsThereImage;
    private String mImageUri;

    public Characters(String mId, Long mDate, String mName, String mCreator, String mFandom, String mCharacterName, String mCharacterSurname, String mContent, Boolean isThereImage, String mImageUri) {
        this.mId = mId;
        this.mDate = mDate;
        this.mCreator = mCreator;
        this.mFandom = mFandom;
        this.mName = mName;
        this.mCharacterName = mCharacterName;
        this.mCharacterSurname = mCharacterSurname;
        this.mContent = mContent;
        this.mIsThereImage = isThereImage;
        this.mImageUri = mImageUri;
    }

    public String getDate() {
        return mDate.toString();
    }

    public String getCreator() {
        return mCreator;
    }

    public String getFandom() {
        return mFandom;
    }

    public String getCharacterName() {
        return mCharacterName;
    }

    public String getCharacterSurname() {
        return mCharacterSurname;
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }

    public String getId() {
        return mId;
    }

    public Boolean isThereImage() {
        return mIsThereImage;
    }

    public String getCharacterImg() { return mImageUri; }
}