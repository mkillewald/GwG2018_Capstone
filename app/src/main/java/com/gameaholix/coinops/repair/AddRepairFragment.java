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
import com.gameaholix.coinops.databinding.FragmentAddRepairBinding;
import com.gameaholix.coinops.game.AddGameFragment;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddRepairFragment extends DialogFragment {
    private static final String TAG = AddGameFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";

    private Context mContext;
    private String mGameId;
    private Item mNewRepair;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public AddRepairFragment() {
        // Required empty public constructor
    }

    public static AddRepairFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        AddRepairFragment fragment = new AddRepairFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddRepairBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_repair, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText

        // Setup Button
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text from EditText
                mNewRepair.setName(bind.etRepairDescription.getText().toString().trim());
                addLog(mNewRepair);
                getDialog().dismiss();
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
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        if (params != null) {
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

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void addLog(Item repairLog) {
        if (TextUtils.isEmpty(repairLog.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_log_description_empty);
            return;
        }

        // TODO: add checks for if game name already exists.

        // Add Game object to Firebase
        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();

            final DatabaseReference repairRef = mDatabaseReference.child(Db.REPAIR).child(uid);

            final String id = repairRef.push().getKey();

            // Get database paths from helper class
            String repairPath = Db.getRepairPath(uid, mGameId) + id;
            String repairListPath = Db.getRepairListPath(uid, mGameId) + id;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(repairPath, repairLog);
            valuesToAdd.put(repairListPath, repairLog.getName());

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
//        } else {
//            // user is not signed in
        }
    }

}
