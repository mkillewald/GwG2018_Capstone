package com.gameaholix.coinops.game.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameRepository {
    private static final String TAG = GameRepository.class.getSimpleName();
    private LiveData<Game> mGameLiveData;
    private String mGameId;

    /**
     * Constructor used for adding a new or retrieving an existing Game
     * @param gameId the ID of the existing Game to retrieve. This will be null if
     *               we are adding a new Game.
     */
    public GameRepository(@Nullable String gameId) {
        if (gameId == null) {
            // we are adding a new Game
            mGameLiveData = new MutableLiveData<>();
            ((MutableLiveData<Game>) mGameLiveData).setValue(new Game());
        } else {
            // we are retrieving an existing InventoryItem
            mGameId = gameId;
            mGameLiveData = fetchData();
        }
    }

    /**
     * Fetch the Game data from Firebase
     * @return a LiveData<> object containing the Game retrieved from firebase
     */
    private LiveData<Game> fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(Fb.getGameRef(uid, mGameId));

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            ((MutableLiveData<Game>) mGameLiveData).setValue(new Game());
            return mGameLiveData;
        }
    }

    private class Deserializer implements Function<DataSnapshot, Game> {
        @Override
        public Game apply(DataSnapshot dataSnapshot) {
            Game game = dataSnapshot.getValue(Game.class);
            if (game != null) {
                game.setId(mGameId);
            } else {
                Log.e(TAG, "Failed to read item details from database, the returned item is null!");
            }
            return game;
        }

    }

    @NonNull
    public LiveData<Game> getGameLiveData() {
        return mGameLiveData;
    }

    /**
     * Add a new Game to Firebase
     * @param newGame the new Game to add
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add(Game newGame) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            mGameId = Fb.getGameRootRef(uid).push().getKey();

            if (TextUtils.isEmpty(mGameId)) return false;

            DatabaseReference gameRef = Fb.getGameRef(uid, mGameId);
            DatabaseReference gameListRef = Fb.getGameListRef(uid);

            Map<String, Object> valuesWithPath = new HashMap<>();
            valuesWithPath.put(gameRef.getPath().toString(), newGame);
            valuesWithPath.put(gameListRef.child(mGameId).getPath().toString(),
                    newGame.getName());

            // perform atomic update to firebase using Map with database paths as keys
            Fb.getDatabaseReference().updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            return true;
        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Update an existing Game to Firebase
     * @param game the existing Game instance to update
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update(Game game) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            DatabaseReference gameRef = Fb.getGameRef(uid, mGameId);
            DatabaseReference gameListRef = Fb.getGameListRef(uid);

            // convert item to Map so it can be iterated
            Map<String, Object> currentValues = game.getMap();

            // create new Map with full database paths as keys using values from item Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(gameRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Fb.NAME)) {
                    valuesWithPath.put(gameListRef.child(game.getId()).getPath().toString(),
                            currentValues.get(key));
                }
            }

            // perform atomic update to firebase using Map with database paths as keys
            Fb.getDatabaseReference().updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            return true;

        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Delete a Game from Firebase
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            final String uid = user.getUid();
            Game game = mGameLiveData.getValue();

            if (game == null) return false;

            if (!TextUtils.isEmpty(game.getImage())) {
                deleteImagesFromFirebase(uid, mGameId, game.getImage());
            }

            // delete repair logs
            Fb.getRepairRef(uid, mGameId).removeValue();

            // delete to do items
            Query toDoQuery = Fb.getToDoRootRef(uid)
                    .orderByChild(Fb.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(toDoQuery, Fb.getUserToDoListRef(uid));

            // delete shopping items
            Query shopQuery = Fb.getShopRootRef(uid)
                    .orderByChild(Fb.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(shopQuery, Fb.getUserShopListRef(uid));

            // delete game details
            Fb.getGameRef(uid, mGameId).removeValue();

            // remove user game_list entry
            Fb.getGameListRef(uid).child(mGameId).removeValue();

            return false;
        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Deletes items matching a Firebase Query, and also removes the items from a list if provided.
     * @param query the Firebase realtime database query to perform
     * @param listRef the list reference to also remove items from
     */
    private void deleteQueryResults(Query query, @Nullable final DatabaseReference listRef) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (listRef != null && !TextUtils.isEmpty(item.getKey())) {
                            // remove item from list
                            listRef.child(item.getKey()).removeValue();
                        }

                        // remove item
                        item.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Deletes full and thumbnail images from Firebase
     * @param uid the ID of the current user
     * @param gameId the ID of the parent Game entity
     * @param filename the base name of the file to remove.
     */
    private void deleteImagesFromFirebase(@NonNull String uid,
                                          @NonNull String gameId,
                                          @NonNull String filename) {
        // Delete thumbnail image
        Fb.getImageThumbRef(uid, gameId, filename).delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete previous thumbnail image -> ", e);
                    }
                });

        // Delete full size  image
        Fb.getImageRef(uid, gameId, filename).delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete previous full image -> ", e);
                    }
                });
    }

    /**
     * Returns a Firebase StorageReference of the thumbnail image
     * @param gameId the parent Game entity
     * @param filename the base filename of the image
     * @return the thumbnail image reference
     */
    public StorageReference getThumbRef(String gameId, String filename) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            return Fb.getImageThumbRef(uid, gameId, filename);
        } else {
            // user is not signed in
            return null;
        }
    }

    /**
     * Uploads full image and thumbnail image
     * @param tempFilePath the full path of the locally stored Bitmap image
     * @param existingFilename the filename of any existing photo
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean uploadImage(final String tempFilePath, @Nullable final String existingFilename) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // get full image and create thumbnail
            Bitmap fullBitmap = BitmapFactory.decodeFile(tempFilePath);
            if (fullBitmap == null) {
                Log.e(TAG, "Error: full bitmap file not found");
                return false;
            }

            Bitmap thumbBitmap = ImageUtils.scaleBitmap(tempFilePath, 140, 105);
            if (thumbBitmap == null) {
                Log.e(TAG, "Error: failed to generate thumbnail bitmap");
                return false;
            }

            // convert Bitmaps to JPEG
            byte[] fullData = ImageUtils.bitmapToJpeg(fullBitmap, 80);
            byte[] thumbData = ImageUtils.bitmapToJpeg(thumbBitmap, 80);

            // Upload the full image
            final String newFilename = tempFilePath.substring(tempFilePath.lastIndexOf('/') + 1);
            final StorageReference imageRef = Fb.getImageRef(uid, mGameId, newFilename);
            uploadImageToFirebase(imageRef, fullData, null, null);

            // Upload the thumbnail image
            final StorageReference thumbRef = Fb.getImageThumbRef(uid, mGameId, newFilename);
            OnSuccessListener<UploadTask.TaskSnapshot> thumbSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Image successfully uploaded to Firebase (" + thumbRef.getPath() + ")");
                    // delete temp image file from external storage
                    ImageUtils.deleteTemporaryImageFromDisk(tempFilePath);

                    // delete previous images from firebase
                    if (!TextUtils.isEmpty(existingFilename)) {
                        deleteImagesFromFirebase(uid, mGameId, existingFilename);
                    }

                    // Store new image filename in database, this will trigger the ImageView to be reloaded.
                    Fb.getGameImageRef(uid, mGameId).setValue(newFilename);
                }
            };
            uploadImageToFirebase(thumbRef, thumbData, thumbSuccessListener, null);

            return true;
        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Helper method to upload an image to firebase
     * @param imageRef the Firebase StorageReference where the file will be uploaded
     * @param data a byte array containing image data
     * @param success an optional OnSuccessListener
     * @param failure an optional OnFailureListener
     */
    private void uploadImageToFirebase(final StorageReference imageRef,
                                       byte[] data,
                                       @Nullable OnSuccessListener<UploadTask.TaskSnapshot> success,
                                       @Nullable OnFailureListener failure) {
        UploadTask uploadImageTask = imageRef.putBytes(data);

        if (success == null) {
            uploadImageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Image successfully uploaded to Firebase (" + imageRef.getPath() + ")");
                }
            });
        } else {
            uploadImageTask.addOnSuccessListener(success);
        }

        if (failure == null) {
            uploadImageTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Image upload failed (" + imageRef.getPath() + ") -> ", e);
                }
            });
        } else {
            uploadImageTask.addOnFailureListener(failure);
        }
    }
}
