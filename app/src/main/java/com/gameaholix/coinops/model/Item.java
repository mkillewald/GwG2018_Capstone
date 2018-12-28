package com.gameaholix.coinops.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gameaholix.coinops.firebase.Fb;
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

    /**
     * Default no argument constructor required for calls to DataSnapshot.getValue()
     */
    public Item() {
    }

    /**
     * Constructor used to create a new Item instance
     * @param parentId the ID of the object which owns the new Item instance
     */
    public Item(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Constructor used to create a new Item instance
     * @param id the ID of the new Item instance
     * @param name the name of the new Item instance
     */
    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor used to create a new Item instance
     * @param id the ID if the new Item instance
     * @param parentId the ID of the object which owns the new Item instance
     * @param name the name of the new Item instance
     */
    public Item(String id, String parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    /**
     * Constructor used to create new instance that is a duplicate copy of another instance. This is
     * used by the ViewModel when editing an existing Item.
     * @param anotherItem the Item instance to duplicate
     */
    public Item(Item anotherItem) {
        this.id = anotherItem.getId();
        this.parentId = anotherItem.getParentId();
        this.name = anotherItem.getName();
        this.createdAt = anotherItem.getCreatedAtLong();
        this.modifiedAt = anotherItem.getModifiedAt();
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

    /**
     * Converts this instance to a Map containing the instance fields. This is useful for updating
     * Firebase without overwriting the entire node.
     * @return the Map containing the instance fields
     */
    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Fb.NAME, getName());
        map.put(Fb.PARENT_ID, getParentId());

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

    /**
     * Returns a Map containing the current timestamp from Firebase
     * @return a Map object containing the Firebase timestamp
     */
    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    /**
     * Returns the createdAt timestamp as a Long
     * @return the createdAt timestamp as a Long
     */
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
