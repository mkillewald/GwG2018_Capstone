package com.gameaholix.coinops.repair;

import android.content.Context;
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
import com.gameaholix.coinops.adapter.RepairAdapter;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RepairListFragment extends Fragment implements RepairAdapter.RepairAdapterOnClickHandler {
    private static final String TAG = RepairListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR_LIST = "CoinOpsRepairList";

    private Context mContext;
    private String mGameId;
    private ArrayList<Item> mRepairLogs;
    private RepairAdapter mRepairAdapter;
    private DatabaseReference mRepairListRef;
    private FirebaseUser mUser;
    private ValueEventListener mRepairListener;
    private OnFragmentInteractionListener mListener;

    public RepairListFragment() {
        // Required empty public constructor
    }

    public static RepairListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        RepairListFragment fragment = new RepairListFragment();
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
            mRepairLogs = new ArrayList<>();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mRepairLogs = savedInstanceState.getParcelableArrayList(EXTRA_REPAIR_LIST);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        mRepairListRef = databaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGameId)
                .child(Db.REPAIR_LIST);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list, container,
                false);

        // Setup Repair Log RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mRepairAdapter = new RepairAdapter( this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRepairAdapter);
        mRepairAdapter.setRepairLogs(mRepairLogs);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mRepairLogs.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String logId = child.getKey();
                        String name = (String) child.getValue();
                        Item repairLog = new Item(logId, mGameId, name);
                        mRepairLogs.add(repairLog);
                    }
                    mRepairAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            // read list of repair logs
            mRepairListRef.addValueEventListener(mRepairListener);

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRepairListener != null) {
            mRepairListRef.removeEventListener(mRepairListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelableArrayList(EXTRA_REPAIR_LIST, mRepairLogs);
    }

    @Override
    public void onClick(Item repairLog) {
        if (mListener != null) {
            mListener.onRepairLogSelected(repairLog);
        }
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
        void onRepairLogSelected(Item repairLog);
    }
}
