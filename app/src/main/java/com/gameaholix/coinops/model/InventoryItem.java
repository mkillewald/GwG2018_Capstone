package com.gameaholix.coinops.model;

import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class InventoryItem extends Item {

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

    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Fb.NAME, getName());
        map.put(Fb.DESCRIPTION, getDescription());
        map.put(Fb.TYPE, getType());
        map.put(Fb.CONDITION, getCondition());

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
