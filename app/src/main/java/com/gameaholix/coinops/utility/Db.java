package com.gameaholix.coinops.utility;

public class Db {

    // Database nodes
    public static final String USER = "user";
    public static final String GAME = "game";
    public static final String GAME_LIST = "game_list";
    public static final String INVENTORY = "inventory";
    public static final String INVENTORY_LIST = "inventory_list";
    public static final String REPAIR = "repair";
    public static final String REPAIR_LIST = "repair_list";
    public static final String STEPS = "steps";
    public static final String SHOP = "shop";
    public static final String SHOP_LIST = "shop_list";
    public static final String TODO = "todo";
    public static final String TODO_LIST = "todo_list";

    // Database keys
    public static final String PARENT_ID = "parentId";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String TYPE = "type";
    public static final String CABINET = "cabinet";
    public static final String CONDITION = "condition";
    public static final String WORKING = "working";
    public static final String OWNERSHIP = "ownership";
    public static final String MONITOR_SIZE = "monitorSize";
    public static final String MONITOR_PHOSPHER = "monitorPhospher";
    public static final String MONITOR_TECH = "monitorTech";
    public static final String MONITOR_BEAM = "monitorBeam";
    public static final String DESCRIPTION = "description";
    public static final String PRIORITY = "priority";

    // Cloud Storage Buckets
    public static final String THUMB = "thumb";

    public static final String[] GAME_STRINGS = { Db.NAME };
    public static final String[] GAME_INTS = {
            Db.TYPE,
            Db.CABINET,
            Db.WORKING,
            Db.OWNERSHIP,
            Db.CONDITION,
            Db.MONITOR_SIZE,
            Db.MONITOR_PHOSPHER,
            Db.MONITOR_BEAM,
            Db.MONITOR_TECH };

    public static final String[] INVENTORY_STRINGS = {Db.NAME, Db.DESCRIPTION};
    public static final String[] INVENTORY_INTS = {Db.TYPE, Db.CONDITION };

    public static final String[] TO_DO_STRINGS = {Db.NAME, Db.DESCRIPTION};
    public static final String[] TO_DO_INTS = {Db.PRIORITY};
}
