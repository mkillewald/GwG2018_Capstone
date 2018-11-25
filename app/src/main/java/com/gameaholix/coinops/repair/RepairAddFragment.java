package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentRepairAddBinding;
import com.gameaholix.coinops.game.GameAddFragment;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RepairAddFragment extends DialogFragment {
    private static final String TAG = GameAddFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";

    private Context mContext;
    private String mGameId;
    private Item mNewRepair;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public RepairAddFragment() {
        // Required empty public constructor
    }

    public static RepairAddFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        RepairAddFragment fragment = new RepairAddFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            }
            mNewRepair = new Item();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mNewRepair = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.add_repair_title);
        }

        // Inflate the layout for this fragment
        final FragmentRepairAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text from EditText
                mNewRepair.setName(bind.etRepairDescription.getText().toString().trim());
                addLog(mNewRepair);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_REPAIR, mNewRepair);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set width and height of this DialogFragment, code block used from
        // https://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        if (getShowsDialog() && getDialog().getWindow() != null) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void addLog(Item repairLog) {
        if (TextUtils.isEmpty(repairLog.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_log_description_empty);
            Log.d(TAG, "Failed to add repair log! Trouble description field was blank.");
            return;
        }

        getDialog().dismiss();

        // Add Game object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            DatabaseReference repairRef = mDatabaseReference
                    .child(Db.REPAIR)
                    .child(uid)
                    .child(mGameId);

            String id = repairRef.push().getKey();

            if (!TextUtils.isEmpty(id)) {
                DatabaseReference repairListRef = mDatabaseReference
                        .child(Db.GAME)
                        .child(uid)
                        .child(mGameId)
                        .child(Db.REPAIR_LIST)
                        .child(id);

                Map<String, Object> valuesToAdd = new HashMap<>();
                valuesToAdd.put(repairRef.child(id).getPath().toString(), repairLog);
                valuesToAdd.put(repairListRef.getPath().toString(), repairLog.getName());

                mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                    " Code: " + databaseError.getCode() +
                                    " Details: " + databaseError.getDetails());
                        }
                    }
                });
            } else {
                Log.e(TAG, "Error: repair log id was null or empty");
            }
//        } else {
//            // user is not signed in
        }
    }

}
