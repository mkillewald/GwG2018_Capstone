package com.gameaholix.coinops.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.database.Exclude;

import java.util.Map;

public class ToDoItem extends Item implements Parcelable {

    private String description;
    private int priority;
//    private boolean reminder;
//    private boolean repeat;
//    private long remindAt;

    public ToDoItem() {
        super();
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public ToDoItem(String parentId) {
        super(parentId);
    }

    public ToDoItem(String id, String parentId, String name) {
        super(id, parentId, name);
    }

    private ToDoItem(Parcel in) {
        super(in);
        this.description = in.readString();
        this.priority = in.readInt();
//        this.reminder = (boolean) in.readValue(getClass().getClassLoader());
//        this.repeat = (boolean) in.readValue(getClass().getClassLoader());
//        this.remindAt = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        super.writeToParcel(dest, i);
        dest.writeString(description);
        dest.writeInt(priority);
//        dest.writeValue(reminder);
//        dest.writeValue(repeat);
//        dest.writeLong(remindAt);
    }

    public final static Parcelable.Creator<ToDoItem> CREATOR = new Parcelable.Creator<ToDoItem>() {

        @Override
        public ToDoItem createFromParcel(Parcel in) { return new ToDoItem(in); }

        @Override
        public ToDoItem[] newArray(int size) { return (new ToDoItem[size]); }
    };

    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = super.getMap();
        map.put(Db.DESCRIPTION, getDescription());
        map.put(Db.PRIORITY, getPriority());

        return map;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

//    public boolean isReminder() {
//        return reminder;
//    }
//
//    public void setReminder(boolean reminder) {
//        this.reminder = reminder;
//    }
//
//    public boolean isRepeat() {
//        return repeat;
//    }
//
//    public void setRepeat(boolean repeat) {
//        this.repeat = repeat;
//    }
//
//    public long getRemindAt() {
//        return remindAt;
//    }
//
//    public void setRemindAt(long remindAt) {
//        this.remindAt = remindAt;
//    }
}
