package com.gameaholix.coinops.model;

import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private String id;
    private String name;
    private String image;

    private int type;
    private int cabinet;
    private int condition;
    private int working;
    private int ownership;
    private int monitorSize;
    private int monitorPhospher;
    private int monitorTech;
    private int monitorBeam;

//    private String monitorChassis;
//    private String tubeModel;
//    private String serialNumber;
//    private String highScore;
//    private String comment;
//    private String manufacturer;

//    private Boolean forSale;
//
//    private Double boughtPrice;
//    private Double forSalePrice;
//    private Double soldPrice;

    /**
     * Default no argument constructor required for calls to DataSnapshot.getValue()
     */
    public Game() {
    }

    /**
     * Constructor used to create a new Game instance
     * @param id the id of the new Game instance
     * @param name the name of the new Game instance
     */
    public Game(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor used to create new instance that is a duplicate copy of another instance. This is
     * used by the ViewModel when editing an existing Game.
     * @param anotherGame the Game instance to duplicate
     */
    public Game(Game anotherGame) {
        this.id = anotherGame.getId();
        this.name = anotherGame.getName();
        this.image = anotherGame.getImage();
        this.type = anotherGame.getType();
        this.cabinet = anotherGame.getCabinet();
        this.condition = anotherGame.getCondition();
        this.working = anotherGame.getWorking();
        this.ownership = anotherGame.getOwnership();
        this.monitorSize = anotherGame.getMonitorSize();
        this.monitorPhospher = anotherGame.getMonitorPhospher();
        this.monitorTech = anotherGame.getMonitorTech();
        this.monitorBeam = anotherGame.getMonitorBeam();
    }

    /**
     * Converts this instance to a Map containing the instance fields. This is useful for updating
     * Firebase without overwriting the entire node.
     * @return the Map containing the instance fields
     */
    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Fb.NAME, getName());
        map.put(Fb.TYPE, getType());
        map.put(Fb.CABINET, getCabinet());
        map.put(Fb.CONDITION, getCondition());
        map.put(Fb.WORKING, getWorking());
        map.put(Fb.OWNERSHIP, getOwnership());
        map.put(Fb.MONITOR_SIZE, getMonitorSize());
        map.put(Fb.MONITOR_PHOSPHER, getMonitorPhospher());
        map.put(Fb.MONITOR_BEAM, getMonitorBeam());
        map.put(Fb.MONITOR_TECH, getMonitorTech());

        return map;
    }

    // Exclude id from being written to database as a field (id is already the parent node)
    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
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

    public int getMonitorBeam() {
        return monitorBeam;
    }

    public void setMonitorBeam(int monitorBeam) {
        this.monitorBeam = monitorBeam;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

//    public String getMonitorChassis() {
//        return monitorChassis;
//    }
//
//    public void setMonitorChassis(String monitorChassis) {
//        this.monitorChassis = monitorChassis;
//    }
//
//    public String getTubeModel() {
//        return tubeModel;
//    }
//
//    public void setTubeModel(String tubeModel) {
//        this.tubeModel = tubeModel;
//    }
//
//    public String getSerialNumber() {
//        return serialNumber;
//    }
//
//    public void setSerialNumber(String serialNumber) {
//        this.serialNumber = serialNumber;
//    }
//
//    public String getHighScore() {
//        return highScore;
//    }
//
//    public void setHighScore(String highScore) {
//        this.highScore = highScore;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    public String getManufacturer() {
//        return manufacturer;
//    }
//
//    public void setManufacturer(String manufacturer) {
//        this.manufacturer = manufacturer;
//    }
//    public Boolean getForSale() {
//        return forSale;
//    }
//
//    public void setForSale(Boolean forSale) {
//        this.forSale = forSale;
//    }
//
//    public Double getBoughtPrice() {
//        return boughtPrice;
//    }
//
//    public void setBoughtPrice(Double boughtPrice) {
//        this.boughtPrice = boughtPrice;
//    }
//
//    public Double getForSalePrice() {
//        return forSalePrice;
//    }
//
//    public void setForSalePrice(Double forSalePrice) {
//        this.forSalePrice = forSalePrice;
//    }
//
//    public Double getSoldPrice() {
//        return soldPrice;
//    }
//
//    public void setSoldPrice(Double soldPrice) {
//        this.soldPrice = soldPrice;
//    }
}
