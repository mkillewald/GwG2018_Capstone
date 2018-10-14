package com.gameaholix.coinops.game;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        final String uid = user.getUid();

        final DatabaseReference userRef =
                mDatabaseReference.child(getString(R.string.db_user)).child(uid);
        final DatabaseReference userGameListRef =
                userRef.child(getString(R.string.db_game_list));
//                final DatabaseReference userTodoListRef = userRef.child("todo_list");
//                final DatabaseReference userShopListRef = userRef.child("shop_list");
//                final DatabaseReference userInvListRef = userRef.child("inventory_list");

        final DatabaseReference gameRef =
                mDatabaseReference.child(getString(R.string.db_game)).child(uid);
//                final DatabaseReference gameTodoListRef = gameIdRef.child("todo_list");
//                final DatabaseReference gameShopListRef = gameIdRef.child("shop_list");
//                final DatabaseReference gameRepairListRef = gameIdRef.child("repair_list");

        final DatabaseReference gameIdRef = gameRef.push();
        final String gameId = gameIdRef.getKey();

        gameIdRef.setValue(game, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                String gameId = databaseReference.getKey();
                userGameListRef.child(gameId).child(getString(R.string.db_name_key)).setValue(game.getName());
            }
        });

        finish();
    }
}
