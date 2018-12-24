package com.gameaholix.coinops.model;

import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.database.Exclude;

import java.util.Map;

public class ToDoItem extends Item {

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

    @Exclude
    public Map<String, Object> getMap() {
        Map<String, Object> map = super.getMap();
        map.put(Fb.DESCRIPTION, getDescription());
        map.put(Fb.PRIORITY, getPriority());

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
