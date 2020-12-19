package com.adzteam.urbook.adapters;
import java.util.Date;

public class Room {
    private String mId;
    private String mName;
    private String mDescription;
    private String mCreator;
    private String mDateOfCreating;

    public Room(String id, String name, String description, String creator, String date) {
        mId = id;
        mName = name;
        mDescription = description;
        mCreator = creator;
        mDateOfCreating = date;
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
}