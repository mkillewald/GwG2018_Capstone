package com.gameaholix.coinops.utility;

public class Db {

    // Database nodes
    public static final String USER = "user";
    public static final String GAME = "game";
    public static final String GAME_LIST = "game_list";

    public static final String INVENTORY = "inventory";
    public static final String INVENTORY_LIST = "inventory_list";

    public static final String SHOP = "shop";
    public static final String SHOP_LIST = "shop_list";

    public static final String TO_DO = "todo";
    public static final String TO_DO_LIST = "todo_list";

    public static final String REPAIR = "repair";
    public static final String REPAIR_LIST = "repair_list";

    // Database keys
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String CABINET = "cabinet";
    public static final String CONDITION = "condition";
    public static final String WORKING = "working";
    public static final String OWNERSHIP = "ownership";
    public static final String MONITOR_SIZE = "monitorSize";
    public static final String MONITOR_PHOSPHER = "monitorPhospher";
    public static final String MONITOR_TECH = "monitorTech";
    public static final String MONITOR_TYPE = "monitorType";
    public static final String DESCRIPTION = "description";

    public static final String[] GAME_STRINGS = { Db.NAME };
    public static final String[] GAME_INTS = {
            Db.TYPE,
            Db.CABINET,
            Db.WORKING,
            Db.OWNERSHIP,
            Db.CONDITION,
            Db.MONITOR_SIZE,
            Db.MONITOR_PHOSPHER,
            Db.MONITOR_TYPE,
            Db.MONITOR_TECH };

    public static final String[] INVENTORY_STRINGS = { Db.NAME, Db.DESCRIPTION };
    public static final String[] INVENTORY_INTS = { Db.TYPE, Db.CONDITION };

    // Get Database paths
    public static String getGamePath(String uid, String gameId) {
        return "/" + Db.GAME + "/" + uid + "/" + gameId + "/";
    }

    public static String getUserGamePath(String uid, String gameId) {
        return  "/" + Db.USER + "/" + uid + "/" + Db.GAME_LIST + "/" + gameId + "/";
    }

    public  static String getInventoryPath(String uid, String id) {
        return "/" + Db.INVENTORY +  "/" + uid + "/" + id + "/";
    }

    public static String getUserInventoryPath(String uid, String id) {
        return  "/" + Db.USER + "/" + uid + "/" + Db.INVENTORY_LIST + "/" + id + "/";
    }
}
