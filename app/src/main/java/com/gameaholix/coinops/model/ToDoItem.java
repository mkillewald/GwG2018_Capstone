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

    /**
     * Default no argument constructor required for calls to DataSnapshot.getValue()
     */
    public ToDoItem() {
        super();
    }

    /**
     * Constructor used to create a new ToDoItem instance
     * @param parentId the ID of the object which owns this instance
     */
    public ToDoItem(String parentId) {
        super(parentId);
    }

    /**
     * Constructor used to create a new ToDoItem instance
     * @param id the ID of the new ToDoItem instance
     * @param parentId the ID of the object which owns this instance
     * @param name the name of the new ToDoItem instance
     */
    public ToDoItem(String id, String parentId, String name) {
        super(id, parentId, name);
    }

    /**
     * Constructor used to create new instance that is a duplicate copy of another instance. This is
     * used by the ViewModel when editing an existing ToDoItem.
     * @param anotherItem the ToDoItem instance to duplicate
     */
    public ToDoItem(ToDoItem anotherItem) {
        super(anotherItem);
        this.description = anotherItem.getDescription();
        this.priority = anotherItem.getPriority();
    }

    /**
     * Converts this instance to a Map containing the instance fields. This is useful for updating
     * Firebase without overwriting the entire node.
     * @return the Map containing the instance fields
     */
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
