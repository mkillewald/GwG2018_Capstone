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
import com.gameaholix.coinops.databinding.FragmentStepAddBinding;
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


public class StepAddFragment extends DialogFragment {
    private static final String TAG = StepAddFragment.class.getSimpleName();
    private static final String EXTRA_STEP = "CoinOpsRepairStep";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private Context mContext;
    private String mGameId;
    private Item mNewStep;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public StepAddFragment() {
        // Required empty public constructor
    }

    public static StepAddFragment newInstance(String gameId, Item repairStep) {
        StepAddFragment fragment = new StepAddFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putParcelable(EXTRA_STEP, repairStep);
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
                mNewStep = getArguments().getParcelable(EXTRA_STEP);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mNewStep = savedInstanceState.getParcelable(EXTRA_STEP);
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
            getDialog().setTitle(R.string.add_repair_step_title);
        }

        // Inflate the layout for this fragment
        final FragmentStepAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step_add, container, false);
        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup Buttons
            bind.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });

            bind.btnSave.setText(R.string.save_changes);
            bind.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String input = bind.etEntry.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mNewStep.setName(input);
                        addStep();
                    } else {
                        PromptUser.displayAlert(mContext,
                                R.string.error_add_repair_step_failed,
                                R.string.error_repair_step_entry_empty);
                    }
                    getDialog().dismiss();
                }
            });

            bind.btnDelete.setVisibility(View.GONE);
//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_STEP, mNewStep);
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

    private boolean textInputIsValid(String inputText) {
        boolean result = true;

        // TODO: possibly add more validation checks, and return false if any one of them fails.
        if (TextUtils.isEmpty(inputText)) {
            Log.d(TAG, "User input was blank or empty.");
            result = false;
        }

        return result;
    }

    private void addStep() {
        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in

            DatabaseReference stepListRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGameId)
                .child(mNewStep.getParentId())
                .child(Db.STEPS);

            String stepId = stepListRef.push().getKey();

            if (!TextUtils.isEmpty(stepId)) {
                Map<String, Object> valuesToAdd = new HashMap<>();
                valuesToAdd.put(stepListRef.child(stepId).getPath().toString(), mNewStep);

                mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                    " Code: " + databaseError.getCode() +
                                    " Details: " + databaseError.getDetails());
                        }
                    }
                });
            }

//        } else {
//            // user is not signed in
        }
    }

}
