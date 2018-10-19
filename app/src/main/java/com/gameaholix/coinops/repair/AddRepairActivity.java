package com.gameaholix.coinops.repair;

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

public class AddRepairActivity extends AppCompatActivity implements
        AddRepairFragment.OnFragmentInteractionListener {

    private static final String TAG = AddRepairActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setTitle(R.string.add_repair_title);
    }

    @Override
    public void onAddButtonPressed(final RepairLog log) {
        addLog(log);
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

    private void addLog(RepairLog log) {
        if (TextUtils.isEmpty(log.getName())) {
            WarnUser.displayAlert(this,
                    R.string.error_add_repair_failed,
                    R.string.error_name_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add RepairLog object to Firebase
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        final String uid = user.getUid();

        final DatabaseReference repairRef = mDatabaseReference.child(Db.REPAIR).child(uid);

        final String id = repairRef.push().getKey();

        // Get database paths from helper class
        String repairPath = Db.getRepairPath(uid, id);
        String userRepairPath = Db.getUserRepairPath(uid, id);

        Map<String, Object> valuesToAdd = new HashMap<>();
        valuesToAdd.put(repairPath, log);
        valuesToAdd.put(userRepairPath + Db.NAME, log.getName());

        // TODO: add progress spinner

        mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                } else {
                    WarnUser.displayAlert(AddRepairActivity.this,
                            R.string.error_add_repair_failed, databaseError.getMessage());
                    Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                            " Code: " + databaseError.getCode() +
                            " Details: " + databaseError.getDetails());
                }
            }
        });
    }
}
