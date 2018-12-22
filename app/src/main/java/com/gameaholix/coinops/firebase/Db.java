package com.gameaholix.coinops.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Db {
    private static final DatabaseReference sDatabaseReference =
            FirebaseDatabase.getInstance().getReference();

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

    public static DatabaseReference getGameRef(@NonNull String uid, @NonNull String gameId) {
        return sDatabaseReference
                .child(Db.GAME)
                .child(uid)
                .child(gameId);
    }

    public static DatabaseReference getUserGameListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.GAME_LIST);
    }

    public static DatabaseReference getRepairRef(@NonNull String uid, @NonNull String gameId) {
        return sDatabaseReference
                .child(Db.REPAIR)
                .child(uid)
                .child(gameId);
    }

    public static DatabaseReference getShopRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.SHOP)
                .child(uid);
    }

    public static DatabaseReference getGameShopListRef(@NonNull String uid, @NonNull String gameId) {
        return sDatabaseReference
                .child(Db.GAME)
                .child(uid)
                .child(gameId)
                .child(Db.SHOP_LIST);
    }

    public static DatabaseReference getUserShopListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.SHOP_LIST);
    }

    public static DatabaseReference getToDoRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.TODO)
                .child(uid);
    }

    public static DatabaseReference getGameToDoListRef(@NonNull String uid, @NonNull String gameId) {
        return sDatabaseReference
                .child(Db.GAME)
                .child(uid)
                .child(gameId)
                .child(Db.TODO_LIST);
    }

    public static DatabaseReference getUserToDoListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.TODO_LIST);
    }

    public static DatabaseReference getInventoryRootRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.INVENTORY)
                .child(uid);
    }

    public static DatabaseReference getInventoryRef(@NonNull String uid, @NonNull String itemId) {
        return getInventoryRootRef(uid)
                .child(itemId);
    }

    public static DatabaseReference getInventoryListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.INVENTORY_LIST);
    }
}
