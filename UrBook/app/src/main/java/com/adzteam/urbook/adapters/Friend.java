package com.adzteam.urbook.adapters;

public class Friend {
    private String mId;
    private String mName;
    private String mStatus;
    private Boolean mIsThereImage;

    public Friend(String id, String name, String description) {
        mId = id;
        mName = name;
        mStatus = description;
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

}