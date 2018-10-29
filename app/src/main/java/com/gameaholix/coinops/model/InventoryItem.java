package com.gameaholix.coinops.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem extends Item implements Parcelable {

    private int type;
    private int condition;

    public InventoryItem() {
        super();
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public InventoryItem(String name) {
        super(name);
    }

    private InventoryItem(Parcel in) {
        super(in);
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
        dest.writeInt(type);
        dest.writeInt(condition);
    }

    public final static Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>() {

        @Override
        public InventoryItem createFromParcel(Parcel in) { return new InventoryItem(in); }

        @Override
        public InventoryItem[] newArray(int size) { return (new InventoryItem[size]); }
    };

    // checks if two InventoryItem objects are equal by comparing gameId which should be unique
    // as id is generated by firebase
    public boolean equals(InventoryItem inventoryItem) {
        return getId().equals(inventoryItem.getId());
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
