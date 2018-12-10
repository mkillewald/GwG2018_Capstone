package com.gameaholix.coinops.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Item implements Parcelable {
    private String id;
    private String parentId;
    protected String name;
    private long createdAt;
    private long modifiedAt;

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public Item(String parentId) {
        this.parentId = parentId;
    }

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(String id, String parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }


    protected Item(Parcel in) {
        this.id = in.readString();
        this.parentId = in.readString();
        this.name = in.readString();
        this.createdAt = in.readLong();
        this.modifiedAt = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(parentId);
        dest.writeString(name);
        dest.writeLong(createdAt);
        dest.writeLong(modifiedAt);
    }

    public final static Creator<Item> CREATOR = new Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel in) { return new Item(in); }

        @Override
        public Item[] newArray(int size) { return (new Item[size]); }
    };

    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Db.NAME, getName());
        map.put(Db.PARENT_ID, getParentId());

        return map;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreatedAtLong() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
