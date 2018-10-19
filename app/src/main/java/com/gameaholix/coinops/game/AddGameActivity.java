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

public class AddGameActivity extends AppCompatActivity implements
        AddGameFragment.OnFragmentInteractionListener{

    private static final String TAG = AddGameActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setTitle(R.string.add_game_title);
    }

    @Override
    public void onAddGameButtonPressed(final Game game) {
        addGame(game);
    }

    // Hide keyboard after touch event occurs outside of EditText
    // Solution used from:
    // https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if ( view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void addGame(Game game) {
        if (TextUtils.isEmpty(game.getName())) {
            WarnUser.displayAlert(this,
                    R.string.error_add_game_failed,
                    R.string.error_name_empty);
            return;
        }

        // TODO: add checks for if game name already exists.

        // Add Game object to Firebase
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        final String uid = user.getUid();

        final DatabaseReference gameRef = mDatabaseReference.child(Db.GAME).child(uid);

        final String gameId = gameRef.push().getKey();

        // Get database paths from helper class
        String gamePath = Db.getGamePath(uid, gameId);
        String userGamePath = Db.getUserGamePath(uid, gameId);

        Map<String, Object> valuesToAdd = new HashMap<>();
        valuesToAdd.put(gamePath, game);
        valuesToAdd.put(userGamePath + Db.NAME, game.getName());

        // TODO: add progress spinner

        mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                } else {
                    WarnUser.displayAlert(AddGameActivity.this,
                            R.string.error_add_game_failed, databaseError.getMessage());
                    Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                            " Code: " + databaseError.getCode() +
                            " Details: " + databaseError.getDetails());
                }
            }
        });
    }

}
