package com.gameaholix.coinops.model;

public class ListRow {
    private String mId;
    private String mName;

    public ListRow(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
