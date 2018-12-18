package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentRepairAddBinding;
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

// TODO: determine if we really want to set parent ID (the gamne ID).

public class RepairAddEditFragment extends BaseDialogFragment {
    private static final String TAG = RepairAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsRepairEditFlag";

    private Context mContext;
    private String mGameId;
    private Item mRepairLog;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public RepairAddEditFragment() {
        // Required empty public constructor
    }

    // Add a new repair log
    public static RepairAddEditFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        RepairAddEditFragment fragment = new RepairAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    // Edit an existing repair log
    public static RepairAddEditFragment newInstance(Item repairLog) {
        RepairAddEditFragment fragment = new RepairAddEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_REPAIR, repairLog);
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
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
                if (mEdit) {
                    mRepairLog = getArguments().getParcelable(EXTRA_REPAIR);
                    if (mRepairLog != null) {
                        mGameId = mRepairLog.getParentId();
                    }
                } else {
                    mGameId = getArguments().getString(EXTRA_GAME_ID);
                    mRepairLog = new Item(mGameId);
                }
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
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
                getDialog().setTitle(R.string.edit_repair_title);
            } else {
                getDialog().setTitle(R.string.add_repair_title);
            }
        }

        // Inflate the layout for this fragment
        final FragmentRepairAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText
        if (mEdit) bind.etRepairDescription.setText(mRepairLog.getName());
        bind.etRepairDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Verify input and hide keyboard if IME_ACTION_DONE
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mRepairLog.setName(input);
                    } else {
                        textView.setText(mRepairLog.getName());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        bind.etRepairDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_repair_description && !hasFocus) {
                    // Verify input if editText loses focus
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        String input = editText.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mRepairLog.setName(input);
                        } else {
                            editText.setText(mRepairLog.getName());
                        }
                    }
                }
            }
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        if (mEdit) bind.btnSave.setText(R.string.save_changes);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etRepairDescription.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mRepairLog.setName(input);
                } else {
                    bind.etRepairDescription.setText(mRepairLog.getName());
                }

                addEditLog();
                mListener.onRepairAddEditCompletedOrCancelled();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
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

    private void addEditLog() {
        if (TextUtils.isEmpty(mRepairLog.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_log_description_empty);
            Log.d(TAG, "Failed to add repair log! Trouble description field was blank.");
            return;
        }

        if (getShowsDialog()) getDialog().dismiss();

        // Add new repair log or update existing repair log to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            if (TextUtils.isEmpty(mGameId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_game_id_empty);
                Log.e(TAG, "Failed to add or update database! Game ID cannot be an empty string.");
                return;
            }

            DatabaseReference repairRootRef = mDatabaseReference
                    .child(Db.REPAIR)
                    .child(uid)
                    .child(mGameId);

            String logId;
            if (mEdit) {
                logId = mRepairLog.getId();
            } else {
                logId = repairRootRef.push().getKey();
            }

            if (TextUtils.isEmpty(logId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_log_id_empty);
                Log.e(TAG, "Failed to add or update database! Repair Log ID cannot be an empty string.");
                return;
            }

            DatabaseReference repairRef = repairRootRef.child(logId);
            DatabaseReference repairListRef = mDatabaseReference
                    .child(Db.GAME)
                    .child(uid)
                    .child(mGameId)
                    .child(Db.REPAIR_LIST)
                    .child(logId);

            Map<String, Object> valuesWithPath = new HashMap<>();
            if (mEdit) {
                // convert mRepairLog instance to Map so it can be iterated
                Map<String, Object> currentValues = mRepairLog.getMap();

                // create new Map with full database paths as keys using values from the Map created above
                for (String key : currentValues.keySet()) {
                    valuesWithPath.put(repairRef.child(key).getPath().toString(), currentValues.get(key));
                    if (key.equals(Db.NAME)) {
                        valuesWithPath.put(repairListRef.getPath().toString(), currentValues.get(key));
                    }
                }
            } else {
                // we are adding a new entry to the database
                valuesWithPath.put(repairRef.getPath().toString(), mRepairLog);
                valuesWithPath.put(repairListRef.getPath().toString(), mRepairLog.getName());
            }

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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
        void onRepairAddEditCompletedOrCancelled();
    }

}
