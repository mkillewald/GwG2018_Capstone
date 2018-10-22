package com.gameaholix.coinops.repair;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class RepairStep implements Parcelable {

    private String id;
    private String logId;
    private String entry;
    private long createdAt;
    private long modifiedAt;

    public RepairStep() {
        // required empty constructor
    }

    private RepairStep(Parcel in) {
        this.id = in.readString();
        this.logId = in.readString();
        this.entry = in.readString();
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
        dest.writeString(logId);
        dest.writeString(entry);
        dest.writeLong(createdAt);
        dest.writeLong(modifiedAt);
    }

    public final static Parcelable.Creator<RepairStep> CREATOR = new Parcelable.Creator<RepairStep>() {

        @Override
        public RepairStep createFromParcel(Parcel in) { return new RepairStep(in); }

        @Override
        public RepairStep[] newArray(int size) { return (new RepairStep[size]); }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreatedAtLong() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
