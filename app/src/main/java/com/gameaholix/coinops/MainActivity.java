package com.gameaholix.coinops;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.gameaholix.coinops.game.GameListActivity;
import com.gameaholix.coinops.inventory.InventoryListActivity;
import com.gameaholix.coinops.shopping.ShoppingListActivity;
import com.gameaholix.coinops.todo.ToDoListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;

    private String mUsername;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final Button gameButton = findViewById(R.id.btn_game_list);
        gameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameListActivity.class);
                startActivity(intent);
            }
        });

        final Button inventoryButton = findViewById(R.id.btn_inventory_list);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryListActivity.class);
                startActivity(intent);
            }
        });

        final Button toDoButton = findViewById(R.id.btn_to_do_list);
        toDoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoListActivity.class);
                startActivity(intent);
            }
        });

        final Button shoppingButton = findViewById(R.id.btn_shopping_list);
        shoppingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
                startActivity(intent);
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName());

                    // Name, email address, and profile photo Url
                    mUsername = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getIdToken() instead.
                    final String uid = user.getUid();

                    Log.d(TAG, "Uid: " + uid);
                    Log.d(TAG, "name: " + mUsername);
                    Log.d(TAG, "email: " + email);
                    Log.d(TAG, "emailVerified: " + emailVerified);
                    Log.d(TAG, "photoUrl: " + photoUrl);

                    // Database references
//                    DatabaseReference userRef = mDatabaseReference.child("user").child(uid);
//                    final DatabaseReference userGameListRef = userRef.child("game_list");
//                    final DatabaseReference userTodoListRef = userRef.child("todo_list");
//                    final DatabaseReference userShopListRef = userRef.child("shop_list");
//                    final DatabaseReference userInvListRef = userRef.child("inventory_list");

//                    DatabaseReference gameRef = mDatabaseReference.child("game").child(uid);
//                    DatabaseReference todoRef = mDatabaseReference.child("todo").child(uid);
//                    DatabaseReference shopRef = mDatabaseReference.child("shop").child(uid);
//                    DatabaseReference repairRef = mDatabaseReference.child("todo").child(uid);
//                    DatabaseReference inventoryRef = mDatabaseReference.child("inventory").child(uid);

                    // add a to-do list item
//                    DatabaseReference todoIdRef = todoRef.push();
//                    Map<String, Object> todoDetails = new HashMap<>();
//                    todoDetails.put("name", "todo list item name");
//                    todoDetails.put("description", "todo list item description");
//                    todoDetails.put( "game", gameId);
//                    todoIdRef.setValue(todoDetails, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            String todoId = databaseReference.getKey();
//                            gameTodoListRef.child(todoId).setValue(true);
//                            userTodoListRef.child(todoId).setValue(true);
//                        }
//                    });

                    // add a shopping list item
//                    DatabaseReference shopIdRef = shopRef.push();
//                    Map<String, Object> shopDetails = new HashMap<>();
//                    shopDetails.put("name", "shopping list item name");
//                    shopDetails.put("description", "shopping list item description");
//                    shopDetails.put( "game", gameId);
//                    shopIdRef.setValue(shopDetails, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            String shopId = databaseReference.getKey();
//                            gameShopListRef.child(shopId).setValue(true);
//                            userShopListRef.child(shopId).setValue(true);
//                        }
//                    });


//                    gameRef.push().setValue(true);
//
//                    todoListRef.push().setValue(true);
//
//                    shopListRef.push().setValue(true);
//
//                    invListRef.push().setValue(true);
//
//                    todoRef.push().setValue("todo item");




                } else {
                    // user is not signed in
                    mUsername = getString(R.string.anonymous_username);
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false) // smartlock will save user credentials
                                    .setAvailableProviders(Arrays.asList(
//                                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
    }

    private void onSignedOutCleanUp() {
        mUsername = getString(R.string.anonymous_username);
    }

}
