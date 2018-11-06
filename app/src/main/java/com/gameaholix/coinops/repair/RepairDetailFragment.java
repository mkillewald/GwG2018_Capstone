package com.gameaholix.coinops.repair;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.StepAdapter;
import com.gameaholix.coinops.databinding.FragmentRepairDetailBinding;
import com.gameaholix.coinops.model.Item;
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

public class RepairDetailFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler {
    private static final String TAG = RepairDetailFragment.class.getSimpleName();
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";

    private Context mContext;
    private Item mRepairLog;
    private String mGameId;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mRepairRef;
    private DatabaseReference mStepRef;
    private ValueEventListener mRepairListener;
    private ValueEventListener mStepListener;
    private FragmentRepairDetailBinding mBind;
    private StepAdapter mStepAdapter;
    private OnFragmentInteractionListener mListener;

    public RepairDetailFragment() {
        // Required empty public constructor
    }

    public static RepairDetailFragment newInstance(Item log) {
        Bundle args = new Bundle();
        RepairDetailFragment fragment = new RepairDetailFragment();
        args.putParcelable(EXTRA_REPAIR, log);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mRepairLog = getArguments().getParcelable(EXTRA_REPAIR);
            }
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        if (mRepairLog != null) {
            mGameId = mRepairLog.getParentId();
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mRepairRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGameId)
                .child(mRepairLog.getId());
        mStepRef = mRepairRef.child(Db.STEPS);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_detail, container, false);

        final View rootView = mBind.getRoot();

        if (mUser != null) {
            // user is signed in;

            // Setup Repair Step RecyclerView
            mStepAdapter = new StepAdapter( mContext, this );
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mBind.rvRepairSteps.setLayoutManager(linearLayoutManager);
            mBind.rvRepairSteps.setAdapter(mStepAdapter);

            // Setup event listeners
            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();
                    mRepairLog = dataSnapshot.getValue(Item.class);
                    if (mRepairLog == null) {
                        Log.d(TAG, "Error: Repair log details not found");
                    } else {
                        mRepairLog.setId(id);
                        mRepairLog.setParentId(mGameId);

                        mBind.tvRepairDescription.setText(mRepairLog.getName());
                        mBind.tvRepairDescription.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListener.onDescriptionSelected(mRepairLog);
                            }
                        });
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
                    ArrayList<Item> repairSteps = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String stepId = child.getKey();
                        Item repairStep = child.getValue(Item.class);
                        if (repairStep != null) {
                            repairStep.setId(stepId);
                            repairStep.setParentId(mRepairLog.getId());
                            repairSteps.add(repairStep);
                        }
                    }
                    mStepAdapter.setRepairSteps(repairSteps);
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

            // Setup Button
            mBind.btnAddStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item newStep = new Item();
                    newStep.setName(mBind.etAddStepEntry.getText().toString().trim());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_repair:
                showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
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

    @Override
    public void onClick(Item repairStep) {
        if (mListener != null) {
            mListener.onStepSelected(repairStep);
        }
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addStep(Item step) {
        if (TextUtils.isEmpty(step.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_step_failed,
                    R.string.error_repair_step_entry_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();
            String stepId = mStepRef.push().getKey();

            // Get database paths from helper class
            String stepPath = Db.getStepsPath(uid, mGameId, mRepairLog.getId()) + stepId;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(stepPath, step);

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

            hideKeyboard(mBind.etAddStepEntry);
            mBind.etAddStepEntry.setText(null);
            mBind.etAddStepEntry.clearFocus();

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
            builder.setTitle(R.string.really_delete_repair_log)
                    .setMessage(R.string.repair_log_will_be_deleted)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItemData();
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteItemData() {
        // delete repair log
        mRepairRef.removeValue();

        // delete repair log list entry
        mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGameId)
                .child(Db.REPAIR_LIST)
                .child(mRepairLog.getId())
                .removeValue();
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
        void onStepSelected(Item repairStep);
        void onDescriptionSelected(Item repairLog);
    }
}
