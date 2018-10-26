package com.gameaholix.coinops.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.GameDetailPagerAdapter;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.repair.AddRepairActivity;
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.model.RepairLog;
import com.gameaholix.coinops.shopping.AddShoppingActivity;
import com.gameaholix.coinops.todo.AddToDoActivity;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GameDetailActivity extends AppCompatActivity implements
        GameDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_GAME_ID = "CoinOpsGameID";
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.RepairLog";

    private Game mGame;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setTitle(R.string.game_details_title);

        List<Fragment> fragments = new Vector<Fragment>();

        fragments.add(GameDetailFragment.newInstance(mGame));
        fragments.add(BlankFragment.newInstance());
        fragments.add(BlankFragment.newInstance());
        fragments.add(BlankFragment.newInstance());


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new GameDetailPagerAdapter(this, getSupportFragmentManager(),
                fragments));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_game:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_GAME, mGame);
    }

    @Override
    public void onLogSelected(RepairLog repairLog) {
        Intent intent = new Intent(this, RepairDetailActivity.class);
        intent.putExtra(EXTRA_REPAIR, repairLog);
        startActivity(intent);
    }

    @Override
    public void onAddRepairButtonPressed(String gameId) {
        Intent intent = new Intent(GameDetailActivity.this, AddRepairActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        startActivity(intent);
    }

    @Override
    public void onAddTodoButtonPressed(String gameId) {
        Intent intent = new Intent(GameDetailActivity.this, AddToDoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAddShoppingButtonPressed(String gameId) {
        Intent intent = new Intent(GameDetailActivity.this, AddShoppingActivity.class);
        startActivity(intent);

    }

    @Override
    public void onDeleteButtonPressed(String gameId) { deleteGameAlert(gameId); }

    private void deleteGameAlert(final String gameId) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.really_delete_game))
                .setMessage(getString(R.string.game_will_be_deleted))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGame(gameId);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteGame(final String gameId) {
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            // Get database paths from helper class
            String gamePath = Db.getGamePath(uid, gameId);
            String userGamePath = Db.getGameListPath(uid, gameId);

            Map<String, Object> valuesToDelete= new HashMap<>();
            valuesToDelete.put(gamePath, null);
            valuesToDelete.put(userGamePath + Db.NAME, null);

            mDatabaseReference.updateChildren(valuesToDelete, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        finish();
                    } else {
                        PromptUser.displayAlert(GameDetailActivity.this,
                                R.string.error_delete_game_failed, databaseError.getMessage());
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });
//        } else {
//            // user is not signed in
        }
    }
}
