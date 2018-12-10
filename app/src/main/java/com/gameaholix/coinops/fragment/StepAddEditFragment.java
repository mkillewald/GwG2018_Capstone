package com.gameaholix.coinops.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentStepAddBinding;
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


public class StepAddEditFragment extends BaseDialogFragment {
    private static final String TAG = StepAddEditFragment.class.getSimpleName();
    private static final String EXTRA_STEP = "CoinOpsRepairStep";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsStepEditFlag";

    private Context mContext;
    private String mGameId;
    private Item mRepairStep;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public StepAddEditFragment() {
        // Required empty public constructor
    }

    // add a new repair step
    public static StepAddEditFragment newInstance(String gameId, String logId) {
        StepAddEditFragment fragment = new StepAddEditFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putParcelable(EXTRA_STEP, new Item(logId));
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    // edit an existing repair step
    public static StepAddEditFragment newInstance(String gameId, Item repairStep) {
        StepAddEditFragment fragment = new StepAddEditFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putParcelable(EXTRA_STEP, repairStep);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
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
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
                mRepairStep = getArguments().getParcelable(EXTRA_STEP);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mRepairStep = savedInstanceState.getParcelable(EXTRA_STEP);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
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
            if (mEdit) {
                getDialog().setTitle(R.string.edit_repair_step_title);
            } else {
                getDialog().setTitle(R.string.add_repair_step_title);
            }
        }

        // Inflate the layout for this fragment
        final FragmentStepAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText
        if (mEdit) bind.etEntry.setText(mRepairStep.getName());
        bind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mRepairStep.setName(input);
                    } else {
                        textView.setText(mRepairStep.getName());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        bind.etEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_entry && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        String input = editText.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mRepairStep.setName(input);
                        } else {
                            editText.setText(mRepairStep.getName());
                        }
                    }
                }
            }
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) getDialog().dismiss();
                mListener.onStepAddEditCompletedOrCancelled();
            }
        });

        if (mEdit) {
            bind.btnSave.setText(R.string.save_changes);
            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getShowsDialog()) getDialog().dismiss();
                    showDeleteAlert();
//                    mListener.onStepAddEditCompletedOrCancelled();
                }
            });
        } else {
            bind.btnSave.setText(R.string.add_repair_step);
            bind.btnDelete.setVisibility(View.GONE);
        }

        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etEntry.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mRepairStep.setName(input);
                } else {
                    bind.etEntry.setText(mRepairStep.getName());
                }

                addEditStep();
                mListener.onStepAddEditCompletedOrCancelled();
            }
        });
        
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_STEP, mRepairStep);
        outState.putBoolean(EXTRA_EDIT_FLAG, mEdit);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void addEditStep() {
        if (TextUtils.isEmpty(mRepairStep.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_step_failed,
                    R.string.error_repair_step_entry_empty);
            Log.e(TAG, "Failed to add repair step! Name field was blank.");
            return;
        }

        if (getShowsDialog()) getDialog().dismiss();

        // add new repair step or update existing repair step to Firebase
        if (mUser != null) {
            // user is signed in

            if (TextUtils.isEmpty(mGameId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_game_id_empty);
                Log.e(TAG, "Failed to add or update database! Game ID cannot be an empty string.");
                return;
            }

            if (TextUtils.isEmpty(mRepairStep.getParentId())) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_log_id_empty);
                Log.e(TAG, "Failed to add or update database! Repair ID cannot be an empty string.");
                return;
            }

            DatabaseReference stepRootRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGameId)
                .child(mRepairStep.getParentId())
                .child(Db.STEPS);

            String stepId;
            if (mEdit) {
                stepId = mRepairStep.getId();
            } else {
                stepId = stepRootRef.push().getKey();
            }

            if (TextUtils.isEmpty(stepId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_step_id_empty);
                Log.e(TAG, "Failed to add or update database! Repair step ID cannot be an empty string.");
                return;
            }

            Map<String, Object> valuesWithPath = new HashMap<>();
            if (mEdit) {
                // convert mRepairStep instance to Map so it can be iterated
                Map<String, Object> currentValues = mRepairStep.getMap();

                // create new Map with full database paths as keys using values from Map created above
                for (String key : currentValues.keySet()) {
                    valuesWithPath.put(stepRootRef.child(stepId).child(key).getPath().toString(),
                            currentValues.get(key));
                }
            } else {
                valuesWithPath.put(stepRootRef.child(stepId).getPath().toString(), mRepairStep);
            }

            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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

//        } else {
//            // user is not signed in
        }
    }

    private void showDeleteAlert() {
        if (mUser != null) {
            // user is signed in

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle(R.string.really_delete_repair_step)
                    .setMessage(R.string.repair_step_will_be_deleted)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItemData();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteItemData() {
        DatabaseReference stepRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGameId)
                .child(mRepairStep.getParentId())
                .child(Db.STEPS)
                .child(mRepairStep.getId());

        // delete repair step item
        stepRef.removeValue();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onStepAddEditCompletedOrCancelled();
    }
}
