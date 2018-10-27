package com.gameaholix.coinops.repair;

import android.content.Context;
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
import com.gameaholix.coinops.databinding.FragmentAddRepairBinding;
import com.gameaholix.coinops.model.RepairLog;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddRepairFragment extends Fragment {
    private static final String TAG = AddRepairFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.RepairLog";

    private Context mContext;
    private String mGameId;
    private RepairLog mNewLog;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    public AddRepairFragment() {
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
        final FragmentAddRepairBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_repair, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            if (getActivity() != null && getActivity().getIntent() != null) {
                mGameId = getActivity().getIntent().getStringExtra(EXTRA_GAME_ID);
            }
            mNewLog = new RepairLog();
        } else {
            mNewLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
        }

        //Setup EditText
//        bind.etRepairLogDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    mNewLog.setDescription(textView.getText().toString().trim());
//                    hideKeyboard(textView);
//                }
//                return false;
//            }
//        });
//        bind.etRepairLogDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (view.getId() == R.id.et_add_game_name && !hasFocus) {
//                    if (view instanceof EditText) {
//                        EditText editText = (EditText) view;
//                        mNewLog.setDescription(editText.getText().toString().trim());
//                        hideKeyboard(editText);
//                    }
//                }
//            }
//        });

        // Setup Button
        Button addLogButton = bind.btnSave;
        addLogButton.setText(R.string.add_repair_entry);
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewLog.setDescription(bind.etRepairLogDescription.getText().toString().trim());
                addLog(mNewLog);
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

        outState.putParcelable(EXTRA_REPAIR, mNewLog);
        outState.putString(EXTRA_GAME_ID, mGameId);
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

    private void addLog(RepairLog log) {
        if (TextUtils.isEmpty(log.getDescription())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_log_description_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add RepairLog object to Firebase
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            final DatabaseReference repairRef = mDatabaseReference.child(Db.REPAIR).child(uid);
            final String logId = repairRef.push().getKey();

            // Get database paths from helper class
            String repairPath = Db.getRepairPath(uid, mGameId, logId);
            String userRepairPath = Db.getRepairListPath(uid, mGameId, logId);

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(repairPath, log);
            valuesToAdd.put(userRepairPath, log.getDescription());

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
                        PromptUser.displayAlert(mContext, R.string.error_add_repair_log_failed,
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
