package com.gameaholix.coinops.inventory;

import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

public class InventoryDetailActivity extends AppCompatActivity implements
        InventoryDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = InventoryDetailActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.inventory.InventoryItem";

    private InventoryItem mInventoryItem;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mInventoryItem = getIntent().getParcelableExtra(EXTRA_INVENTORY_ITEM);
        } else {
            mInventoryItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setTitle(R.string.inventory_details_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_inventory:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_INVENTORY_ITEM, mInventoryItem);
    }

    @Override
    public void onDeleteButtonPressed(String id) {
        deleteItemAlert(id);
    }

    private void deleteItemAlert(final String id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.really_delete_inventory_item))
                .setMessage(getString(R.string.inventory_item_will_be_deleted))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(id);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteItem(final String id) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // Get database paths from helper class
            String inventoryPath = Db.getInventoryPath(uid, id);
            String userInventoryPath = Db.getInventoryListPath(uid, id);

            Map<String, Object> valuesToDelete= new HashMap<>();
            valuesToDelete.put(inventoryPath, null);
            valuesToDelete.put(userInventoryPath + Db.NAME, null);

            mDatabaseReference.updateChildren(valuesToDelete, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        finish();
                    } else {
                        WarnUser.displayAlert(InventoryDetailActivity.this,
                                R.string.error_delete_inventory_failed, databaseError.getMessage());
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });
        } else {
            // user is not signed in
        }
    }
}
