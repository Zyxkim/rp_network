package com.adzteam.urbook.adapters;

public class Room {
    private String mName;
    private String mDescription;
    private String mCreator;
    private String mDateOfCreating;

    public Room(String name, String description, String creator, String date) {
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
}