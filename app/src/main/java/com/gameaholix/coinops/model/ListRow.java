package com.gameaholix.coinops.model;

public class ListRow {
    private String mId;
    private String mName;

    /**
     * Constructor used to create a new ListRow instance
     * @param id the ID of the new ListRow instance
     * @param name the name of the new ListRow instance
     */
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
