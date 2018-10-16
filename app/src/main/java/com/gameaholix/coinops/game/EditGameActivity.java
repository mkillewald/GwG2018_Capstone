package com.gameaholix.coinops.game;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.WarnUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditGameActivity extends AppCompatActivity implements
        EditGameFragment.OnFragmentInteractionListener {

    private static final String TAG = EditGameActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.game.Game";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        setTitle(R.string.edit_game_title);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_GAME, mGame);
    }

    @Override
    public void onEditGameButtonPressed(final Game game) {
        if (game.getName() == null || TextUtils.isEmpty(game.getName())) {
            WarnUser.displayAlert(this, R.string.error_edit_game_failed, R.string.error_game_name_empty);
            return;
        }

        updateGame(game);
    }

    // Hide keyboard after touch event occurs outside of EditText
    // Solution used from https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if ( view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void updateGame(final Game game) {
        // TODO: add checks for if game name already exists.

        // Update Firebase
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();
            final String gameId = game.getGameId();
            final String gamePath = Db.GAME_PATH + uid + "/" + gameId;
            final String userGamePath = Db.USER_PATH + uid + Db.GAME_LIST_PATH + gameId + Db.NAME_PATH;

            Map<String, Object> valuesToUpdate = new HashMap<>();
            valuesToUpdate.put(gamePath, game.toMap());
            valuesToUpdate.put(userGamePath, game.getName());

            mDatabaseReference.updateChildren(valuesToUpdate, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        finish();
                    } else {
                        WarnUser.displayAlert(EditGameActivity.this,
                                R.string.error_edit_game_failed, databaseError.getMessage());
                        Log.e(TAG, "Database Error: " + databaseError.getDetails());
                    }
                }
            });

//            final DatabaseReference userRef = mDatabaseReference.child(Db.USER).child(uid);
//            final DatabaseReference userGameListRef = userRef.child(Db.GAME_LIST);
//            final DatabaseReference gameRef = mDatabaseReference.child(Db.GAME).child(uid);
//            final DatabaseReference gameIdRef = gameRef.child(gameId);
//
//            gameIdRef.setValue(game, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                    userGameListRef.child(gameId).child(Db.NAME).setValue(game.getName());
//                }
//            });


        } else {
            // user is not signed in
        }
    }
}

