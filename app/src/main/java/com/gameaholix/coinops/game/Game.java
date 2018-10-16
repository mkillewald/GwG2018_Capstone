package com.gameaholix.coinops.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Game implements Parcelable {

    private String gameId;
    private String name;

    private int type;
    private int cabinet;
    private int condition;
    private int working;
    private int ownership;
    private int monitorSize;
    private int monitorPhospher;
    private int monitorTech;
    private int monitorType;

    private String monitorChassis;
    private String tubeModel;
    private String serialNumber;
    private String highScore;
    private String comment;
    private String manufacturer;

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
        this.type = in.readInt();
        this.cabinet = in.readInt();
        this.condition = in.readInt();
        this.working = in.readInt();
        this.ownership = in.readInt();
        this.monitorSize = in.readInt();
        this.monitorPhospher = in.readInt();
        this.monitorTech = in.readInt();
        this.monitorType = in.readInt();
        this.monitorChassis = in.readString();
        this.tubeModel = in.readString();
        this.serialNumber = in.readString();
        this.highScore = in.readString();
        this.comment = in.readString();
        this.manufacturer = in.readString();
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
        dest.writeInt(type);
        dest.writeInt(cabinet);
        dest.writeInt(condition);
        dest.writeInt(working);
        dest.writeInt(ownership);
        dest.writeInt(monitorSize);
        dest.writeInt(monitorPhospher);
        dest.writeInt(monitorTech);
        dest.writeInt(monitorType);
        dest.writeString(monitorChassis);
        dest.writeString(tubeModel);
        dest.writeString(serialNumber);
        dest.writeString(highScore);
        dest.writeString(comment);
        dest.writeString(manufacturer);
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

    /**
     * Checks if two Game objects are equal by comparing gameId of each, which must
     * be a unique value (gameId is generated by firebase push()).
     *
     * @param game the Game object that will be compared to this instance
     * @return true if gameID's match, false if they don't.
     */
    public boolean equals(Game game) {
        return getGameId().equals(game.getGameId());
    }

    /**
     * Returns a Map<String, Object> of property names and values for updating firebase.
     * @return the Map<String, Object> object
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", getName());
        result.put("type", getType());
        result.put("cabinet", getCabinet());
        result.put("condition", getCondition());
        result.put("working", getWorking());
        result.put("ownership", getOwnership());
        result.put("monitorSize", getMonitorSize());
        result.put("monitorPhospher", getMonitorPhospher());
        result.put("monitorTech", getMonitorTech());
        result.put("monitorType", getMonitorType());
        return result;
    }

    // Exclude gameId from being written to database as a field (gameId is already the parent node)
    @Exclude
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCabinet() {
        return cabinet;
    }

    public void setCabinet(int cabinet) {
        this.cabinet = cabinet;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getWorking() {
        return working;
    }

    public void setWorking(int working) {
        this.working = working;
    }

    public int getOwnership() {
        return ownership;
    }

    public void setOwnership(int ownership) {
        this.ownership = ownership;
    }

    public int getMonitorSize() {
        return monitorSize;
    }

    public void setMonitorSize(int monitorSize) {
        this.monitorSize = monitorSize;
    }

    public int getMonitorPhospher() {
        return monitorPhospher;
    }

    public void setMonitorPhospher(int monitorPhospher) {
        this.monitorPhospher = monitorPhospher;
    }

    public int getMonitorTech() {
        return monitorTech;
    }

    public void setMonitorTech(int monitorTech) {
        this.monitorTech = monitorTech;
    }

    public int getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(int monitorType) {
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
