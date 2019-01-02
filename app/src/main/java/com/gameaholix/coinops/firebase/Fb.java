package com.gameaholix.coinops.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Fb {
    private static final DatabaseReference sDatabaseReference =
            FirebaseDatabase.getInstance().getReference();
    private static final StorageReference sStorageReference =
            FirebaseStorage.getInstance().getReference();

    // Database nodes
    public static final String USER = "user";
    public static final String GAME = "game";
    public static final String GAME_LIST = "game_list";
    private static final String INVENTORY = "inventory";
    private static final String INVENTORY_LIST = "inventory_list";
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

    // Storage keys
    private static final String THUMB = "thumb_";

    public static DatabaseReference getDatabaseReference() {
        return sDatabaseReference;
    }

    public static DatabaseReference getGameRootRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.GAME)
                .child(uid);
    }

    public static DatabaseReference getGameRef(@NonNull String uid, @NonNull String gameId) {
        return getGameRootRef(uid)
                .child(gameId);
    }

    public static DatabaseReference getGameImageRef(@NonNull String uid,
                                                    @NonNull String gameId,
                                                    @NonNull String filename) {
        return getGameRef(uid, gameId)
                .child(Fb.IMAGE);
    }

    public static DatabaseReference getGameListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.USER)
                .child(uid)
                .child(Fb.GAME_LIST);
    }

    public static DatabaseReference getRepairRef(@NonNull String uid, @NonNull String gameId) {
        return sDatabaseReference
                .child(Fb.REPAIR)
                .child(uid)
                .child(gameId);
    }

    public static DatabaseReference getShopRootRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.SHOP)
                .child(uid);
    }

    public static DatabaseReference getShopRef(@NonNull String uid, @NonNull String itemId) {
        return getShopRootRef(uid)
                .child(itemId);
    }

    public static DatabaseReference getGameShopListRef(@NonNull String uid, @NonNull String gameId) {
        return getGameRootRef(uid)
                .child(gameId)
                .child(Fb.SHOP_LIST);
    }

    public static DatabaseReference getUserShopListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.USER)
                .child(uid)
                .child(Fb.SHOP_LIST);
    }

    public static DatabaseReference getToDoRootRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.TODO)
                .child(uid);
    }

    public static DatabaseReference getToDoRef(@NonNull String uid, @NonNull String itemId) {
        return getToDoRootRef(uid)
                .child(itemId);
    }

    public static DatabaseReference getGameToDoListRef(@NonNull String uid, @NonNull String gameId) {
        return getGameRootRef(uid)
                .child(gameId)
                .child(Fb.TODO_LIST);
    }

    public static DatabaseReference getUserToDoListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.USER)
                .child(uid)
                .child(Fb.TODO_LIST);
    }

    public static DatabaseReference getInventoryRootRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.INVENTORY)
                .child(uid);
    }

    public static DatabaseReference getInventoryRef(@NonNull String uid, @NonNull String itemId) {
        return getInventoryRootRef(uid)
                .child(itemId);
    }

    public static DatabaseReference getInventoryListRef(@NonNull String uid) {
        return sDatabaseReference
                .child(Fb.USER)
                .child(uid)
                .child(Fb.INVENTORY_LIST);
    }

    public static StorageReference getImageRootRef(@NonNull String uid, @NonNull String gameId) {
        return sStorageReference
                .child(uid)
                .child(gameId);
    }

    public static StorageReference getImageRef(@NonNull String uid,
                                               @NonNull String gameId,
                                               @NonNull String filename) {
        return getImageRootRef(uid, gameId)
                .child(filename);
    }

    public static StorageReference  getImageThumbRef(@NonNull String uid,
                                                     @NonNull String gameId,
                                                     @NonNull String filename) {
        return getImageRootRef(uid, gameId)
                .child(Fb.THUMB + filename);
    }
}
