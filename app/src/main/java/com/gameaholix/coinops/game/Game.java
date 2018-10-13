package com.gameaholix.coinops.game;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {

    private String gameId;
    private String name;
    private String manufacturer;
    private int year;

    private String type;
    private String cabinet;
    private String condition;
    private String working;

    private String monitorSize;
    private String monitorType;
    private String monitorChassis;
    private String tubeModel;

    private String serialNumber;
    private String highScore;
    private String comment;
    private String status;

    private Boolean forSale;
    private Double boughtPrice;
    private Double forSalePrice;
    private Double soldPrice;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

    public Game(String name) {
        this.name = name;
    }

    private Game(Parcel in) {
        this.gameId = in.readString();
        this.name = in.readString();
        this.manufacturer = in.readString();
        this.year = in.readInt();
        this.type = in.readString();
        this.cabinet = in.readString();
        this.condition = in.readString();
        this.working = in.readString();
        this.monitorSize = in.readString();
        this.monitorType = in.readString();
        this.monitorChassis = in.readString();
        this.tubeModel = in.readString();
        this.serialNumber = in.readString();
        this.highScore = in.readString();
        this.comment = in.readString();
        this.status = in.readString();
//        this.forSale = (Boolean) in.readValue(null);
//        this.boughtPrice = in.readDouble();
//        this.forSalePrice = in.readDouble();
//        this.soldPrice = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(gameId);
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeInt(year);
        dest.writeString(type);
        dest.writeString(cabinet);
        dest.writeString(condition);
        dest.writeString(working);
        dest.writeString(monitorSize);
        dest.writeString(monitorType);
        dest.writeString(monitorChassis);
        dest.writeString(tubeModel);
        dest.writeString(serialNumber);
        dest.writeString(highScore);
        dest.writeString(comment);
        dest.writeString(status);
//        dest.writeValue(forSale);
//        dest.writeDouble(boughtPrice);
//        dest.writeDouble(forSalePrice);
//        dest.writeDouble(soldPrice);
    }

    public final static Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {

        @Override
        public Game createFromParcel(Parcel in) { return new Game(in); }

        @Override
        public Game[] newArray(int size) { return (new Game[size]); }
    };

    // checks if two Game objects are equal by comparing gameId which should be unique
    // as gameId is generated by firebase
    public boolean equals(Object object) {
        if (object instanceof Game) {
            Game game = (Game) object;
            return getGameId() == game.getGameId();
        } else {
            return false;
        }
    }

    public String getGameId() { return gameId; }

    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public String getMonitorSize() {
        return monitorSize;
    }

    public void setMonitorSize(String monitorSize) {
        this.monitorSize = monitorSize;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    public String getMonitorChassis() {
        return monitorChassis;
    }

    public void setMonitorChassis(String monitorChassis) {
        this.monitorChassis = monitorChassis;
    }

    public String getTubeModel() {
        return tubeModel;
    }

    public void setTubeModel(String tubeModel) {
        this.tubeModel = tubeModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHighScore() {
        return highScore;
    }

    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(Double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public Double getForSalePrice() {
        return forSalePrice;
    }

    public void setForSalePrice(Double forSalePrice) {
        this.forSalePrice = forSalePrice;
    }

    public Double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Double soldPrice) {
        this.soldPrice = soldPrice;
    }
}
