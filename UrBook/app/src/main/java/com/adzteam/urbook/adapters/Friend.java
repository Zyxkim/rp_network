package com.adzteam.urbook.adapters;

public class Friend {
    private String mId;
    private String mName;
    private String mStatus;
    private Boolean mIsThereImage;
    private String mImageUri;

    public Friend(String id, String name, String description, String imageUri) {
        mId = id;
        mName = name;
        mStatus = description;
        mImageUri = imageUri;
    }

    public String getName() {
        return mName;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getId() {
        return mId;
    }

    public String getFriendImg() {
        return mImageUri;
    }

}