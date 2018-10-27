package com.gameaholix.coinops.repair;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddStepBinding;
import com.gameaholix.coinops.model.RepairStep;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddStepFragment extends Fragment {
    private static final String TAG = AddStepFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR_ID = "CoinOpsRepairLogId";
    private static final String EXTRA_STEP = "com.gameaholix.coinops.model.RepairStep";

    private Context mContext;
    private String mGameId;
    private String mLogId;
    private RepairStep mNewStep;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    public AddStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddStepBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_step, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            if (getActivity() != null && getActivity().getIntent() != null) {
                Intent intent = getActivity().getIntent();
                mGameId = intent.getStringExtra(EXTRA_GAME_ID);
                mLogId = intent.getStringExtra(EXTRA_REPAIR_ID);
            }
            mNewStep = new RepairStep();
        } else {
            mNewStep = savedInstanceState.getParcelable(EXTRA_STEP);
        }

        //Setup EditText
//        bind.etAddStepEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    mNewStep.setEntry(textView.getText().toString().trim());
//                    hideKeyboard(textView);
//                }
//                return false;
//            }
//        });
//        bind.etAddStepEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (view.getId() == R.id.et_add_game_name && !hasFocus) {
//                    if (view instanceof EditText) {
//                        EditText editText = (EditText) view;
//                        mNewStep.setEntry(editText.getText().toString().trim());
//                        hideKeyboard(editText);
//                    }
//                }
//            }
//        });

        // Setup Button
        Button addStepButton = bind.btnSave;
        addStepButton.setText(R.string.add_repair_entry);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewStep.setEntry(bind.etAddStepEntry.getText().toString().trim());
                addStep(mNewStep);
            }
        });

        return rootView;
    }

//    private void hideKeyboard(TextView view) {
//        InputMethodManager imm = (InputMethodManager) view
//                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_STEP, mNewStep);
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

    private void addStep(RepairStep step) {
        if (TextUtils.isEmpty(step.getEntry())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_step_entry_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add RepairLog object to Firebase
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            final DatabaseReference stepRef = mDatabaseReference
                    .child(Db.REPAIR)
                    .child(uid)
                    .child(mGameId)
                    .child(mLogId)
                    .child(Db.STEPS);
            final String stepId = stepRef.push().getKey();

            // Get database paths from helper class
            String stepPath = Db.getStepsPath(uid, mGameId, mLogId, stepId);
//            String stepListPath = Db.getStepListPath(uid, mGameId, mLogId, stepId);

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(stepPath, step);
//            valuesToAdd.put(stepListPath, true);

            // TODO: add progress spinner

            mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        PromptUser.displayAlert(mContext, R.string.error_add_repair_step_failed,
                                databaseError.getMessage());
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
