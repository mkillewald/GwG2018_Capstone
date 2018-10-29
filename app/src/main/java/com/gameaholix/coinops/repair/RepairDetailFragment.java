package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.StepAdapter;
import com.gameaholix.coinops.databinding.FragmentRepairDetailBinding;
import com.gameaholix.coinops.model.Entry;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepairDetailFragment extends Fragment {
    private static final String TAG = RepairDetailFragment.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.Entry";
    private static final String EXTRA_STEP_LIST = "CoinOpsRepairStepList";

    private Context mContext;
    private Entry mRepairLog;
    private ArrayList<Entry> mRepairSteps;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mRepairRef;
    private DatabaseReference mStepRef;
    private ValueEventListener mRepairListener;
    private ValueEventListener mStepListener;
    private FragmentRepairDetailBinding mBind;
    private StepAdapter mStepAdapter;

    public RepairDetailFragment() {
        // Required empty public constructor
    }

    public static RepairDetailFragment newInstance(Entry log) {
        Bundle args = new Bundle();
        RepairDetailFragment fragment = new RepairDetailFragment();
        args.putParcelable(EXTRA_REPAIR, log);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mRepairLog = getArguments().getParcelable(EXTRA_REPAIR);
            }
            mRepairSteps = new ArrayList<>();
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
            mRepairSteps = savedInstanceState.getParcelableArrayList(EXTRA_STEP_LIST);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_detail, container, false);

        final View rootView = mBind.getRoot();

        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();
            final String gameId = mRepairLog.getParentId();
            String logId = mRepairLog.getId();

            // Setup database references
            mRepairRef = mDatabaseReference.child(Db.REPAIR).child(uid).child(gameId).child(logId);
            mStepRef = mRepairRef.child(Db.STEPS);

            // Setup Repair Step RecyclerView
            final RecyclerView recyclerView = rootView.findViewById(R.id.rv_repair_steps);
            mStepAdapter = new StepAdapter( mContext, null );
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(mStepAdapter);
            mStepAdapter.setRepairSteps(mRepairSteps);

            // Setup event listeners
            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mRepairLog = dataSnapshot.getValue(Entry.class);
                    if (mRepairLog == null) {
                        Log.d(TAG, "Error: Repair log details not found");
                    } else {
                        mRepairLog.setId(id);
                        mRepairLog.setParentId(gameId);

                        mBind.tvRepairDescription.setText(mRepairLog.getEntry());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mRepairRef.addValueEventListener(mRepairListener);

            mStepListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mRepairSteps.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        String stepId = dataSnapshot1.getKey();

                        // TODO: finish this
                        Entry repairStep = dataSnapshot1.getValue(Entry.class);
                        mRepairSteps.add(repairStep);
                    }
                    mStepAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mStepRef.addValueEventListener(mStepListener);

            // Setup EditText
            mBind.etAddStepEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView);
                    }
                    return false;
                }
            });
//            mBind.etAddStepEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean hasFocus) {
//                    if (view.getId() == R.id.et_game_name && !hasFocus) {
//                        if (view instanceof EditText) {
//                            EditText editText = (EditText) view;
//                            hideKeyboard(editText);
//                        }
//                    }
//                }
//            });


            // Setup Button
            mBind.btnAddStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Entry newStep = new Entry();
                    newStep.setEntry(mBind.etAddStepEntry.getText().toString().trim());
                    addStep(newStep);
                }
            });
//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRepairRef.removeEventListener(mRepairListener);
        mStepRef.removeEventListener(mStepListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
        outState.putParcelableArrayList(EXTRA_STEP_LIST, mRepairSteps);
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

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addStep(Entry step) {
        if (TextUtils.isEmpty(step.getEntry())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_step_failed,
                    R.string.error_repair_step_entry_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();

            final DatabaseReference stepRef = mDatabaseReference
                    .child(Db.REPAIR)
                    .child(uid)
                    .child(mRepairLog.getParentId())
                    .child(mRepairLog.getId())
                    .child(Db.STEPS);
            final String stepId = stepRef.push().getKey();

            // Get database paths from helper class
            String stepPath = Db.getStepsPath(uid, mRepairLog.getParentId(), mRepairLog.getId(), stepId);
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
                        hideKeyboard(mBind.etAddStepEntry);
                        mBind.etAddStepEntry.setText(null);
                        mBind.etAddStepEntry.clearFocus();
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
