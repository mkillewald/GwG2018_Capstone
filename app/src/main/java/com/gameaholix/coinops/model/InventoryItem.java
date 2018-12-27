package com.gameaholix.coinops.model;

import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class InventoryItem extends Item {

    private String description;
    private int type;
    private int condition;

    /**
     * Default no argument constructor required for calls to DataSnapshot.getValue()
     */
    public InventoryItem() {
        super();
    }

    /**
     * Constructor used to create a new InventoryItem instance
     * @param id the id of the new InventoryItem instance
     * @param name the name of the new InventoryItem instance
     */
    public InventoryItem(String id, String name) {
        super(id, name);
    }

    /**
     * Constructor used to create new instance that is a duplicate copy of another instance. This is
     * used by the ViewModel when editing an existing InventoryItem.
     * @param anotherItem the InventoryItem instance to duplicate
     */
    public InventoryItem(InventoryItem anotherItem) {
        super(anotherItem);
        this.description = anotherItem.getDescription();
        this.type = anotherItem.getType();
        this.condition = anotherItem.getCondition();
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
