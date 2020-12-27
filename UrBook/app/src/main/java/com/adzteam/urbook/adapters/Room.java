package com.adzteam.urbook.adapters;

import android.net.Uri;

public class Room {
    private String mId;
    private String mName;
    private String mDescription;
    private String mCreator;
    private String mDateOfCreating;
    private Boolean mIsThereImage;
    private String mImageUri;

    public Room(String id, String name, String description, String creator, String date, Boolean isThereImage, String imageUri) {
        mId = id;
        mName = name;
        mDescription = description;
        mCreator = creator;
        mDateOfCreating = date;
        mIsThereImage = isThereImage;
        mImageUri = imageUri;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCreator() {
        return mCreator;
    }

    public String getDate() {
        return mDateOfCreating;
    }

    public String getId() {
        return mId;
    }

    public Boolean isThereImage() {
        return mIsThereImage;
    }

    public String getRoomImg() { return mImageUri; }
}