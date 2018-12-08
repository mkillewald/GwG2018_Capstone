package com.gameaholix.coinops.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class InventoryItem extends Item implements Parcelable {

    private String description;
    private int type;
    private int condition;

    public InventoryItem() {
        super();
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public InventoryItem(String id, String name) {
        super(id, name);
    }

    private InventoryItem(Parcel in) {
        super(in);
        this.description = in.readString();
        this.type = in.readInt();
        this.condition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        super.writeToParcel(dest, i);
        dest.writeString(description);
        dest.writeInt(type);
        dest.writeInt(condition);
    }

    public final static Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>() {

        @Override
        public InventoryItem createFromParcel(Parcel in) { return new InventoryItem(in); }

        @Override
        public InventoryItem[] newArray(int size) { return (new InventoryItem[size]); }
    };

    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Db.NAME, getName());
        map.put(Db.DESCRIPTION, getDescription());
        map.put(Db.TYPE, getType());
        map.put(Db.CONDITION, getCondition());

        return map;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
}
