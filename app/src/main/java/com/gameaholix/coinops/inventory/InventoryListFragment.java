package com.gameaholix.coinops.inventory;

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
import com.gameaholix.coinops.adapter.InventoryAdapter;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InventoryListFragment extends Fragment implements InventoryAdapter.InventoryAdapterOnClickHandler {
    private static final String TAG = InventoryListFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_LIST = "CoinOpsInventoryList";

    private InventoryAdapter mInventoryAdapter;
    private ArrayList<InventoryItem> mInventoryItems;
    private DatabaseReference mUserInventoryListRef;
    private ValueEventListener mInventoryListener;
    private OnFragmentInteractionListener mListener;

    public InventoryListFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_inventory_list, container,
                false);

        if (savedInstanceState == null) {
            mInventoryItems = new ArrayList<>();
        } else {
            mInventoryItems = savedInstanceState.getParcelableArrayList(EXTRA_INVENTORY_LIST);
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_inventory_list);
        mInventoryAdapter = new InventoryAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mInventoryAdapter);
        mInventoryAdapter.setInventoryItems(mInventoryItems);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // Setup database references
            mUserInventoryListRef = databaseReference.child(Db.USER).child(uid).child(Db.INVENTORY_LIST);

            // read list of inventory items
            mInventoryListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mInventoryItems.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String id = dataSnapshot1.getKey();
                        InventoryItem item =  dataSnapshot1.getValue(InventoryItem.class);
                        if (item != null) {
                            item.setId(id);
                        }
                        mInventoryItems.add(item);
                    }
                    mInventoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            mUserInventoryListRef.addValueEventListener(mInventoryListener);

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUserInventoryListRef.removeEventListener(mInventoryListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_INVENTORY_LIST, mInventoryItems);
    }

    @Override
    public void onClick(InventoryItem inventoryItem) {
        if (mListener != null) {
            mListener.onInventoryItemSelected(inventoryItem);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        void onInventoryItemSelected(InventoryItem inventoryItem);
    }
}
