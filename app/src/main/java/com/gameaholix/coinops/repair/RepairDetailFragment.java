package com.gameaholix.coinops.repair;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.StepAdapter;
import com.gameaholix.coinops.databinding.FragmentRepairDetailBinding;
import com.gameaholix.coinops.step.RepairStep;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.DateHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RepairDetailFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler {
    private static final String TAG = RepairDetailFragment.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.repair.RepairLog";
    private static final String EXTRA_STEP_LIST = "CoinOpsRepairStepList";

    private Context mContext;
    private RepairLog mRepairLog;
    private ArrayList<RepairStep> mRepairSteps;
    private DatabaseReference mRepairRef;
    private DatabaseReference mStepRef;
    private ValueEventListener mRepairListener;
    private ValueEventListener mStepListener;
    private StepAdapter mStepAdapter;
    private OnFragmentInteractionListener mListener;

    public RepairDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentRepairDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_detail, container, false);

        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mRepairLog = intent.getParcelableExtra(EXTRA_REPAIR);
            }
            mRepairSteps = new ArrayList<>();
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
            mRepairSteps = savedInstanceState.getParcelableArrayList(EXTRA_STEP_LIST);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();
            final String gameId = mRepairLog.getGameId();
            String logId = mRepairLog.getId();

            // Setup database references
            mRepairRef = databaseReference.child(Db.REPAIR).child(uid).child(gameId).child(logId);
            mStepRef = mRepairRef.child(Db.STEPS);

            // Setup Repair Step RecyclerView
            final RecyclerView recyclerView = rootView.findViewById(R.id.rv_repair_steps);
            mStepAdapter = new StepAdapter( this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(mStepAdapter);
            mStepAdapter.setRepairSteps(mRepairSteps);

            // Setup event listeners
            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mRepairLog = dataSnapshot.getValue(RepairLog.class);
                    if (mRepairLog == null) {
                        Log.d(TAG, "Error: Repair log details not found");
                    } else {
                        mRepairLog.setId(id);
                        mRepairLog.setGameId(gameId);

                        String createdAtString =
                                DateHelper.getDateTime(mContext, mRepairLog.getCreatedAtLong());
                        bind.tvRepairCreatedAt.setText(createdAtString);
                        bind.tvRepairDescription.setText(mRepairLog.getDescription());
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
                        String stepId = dataSnapshot1.getKey();

                        // TODO: finish this
                        RepairStep repairStep = dataSnapshot1.getValue(RepairStep.class);
                        Log.d(TAG, "RepairStep Entry: " + repairStep.getEntry());
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

            // Setup Button
            bind.btnAddStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddStepButtonPressed(mRepairLog.getGameId(), mRepairLog.getId());
                    }
                }
            });
        } else {
            // user is not signed in
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
    public void onClick(RepairStep repairStep) {
        // TODO: fill this in as needed
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
        void onAddStepButtonPressed(String gameId, String logId);
    }
}
