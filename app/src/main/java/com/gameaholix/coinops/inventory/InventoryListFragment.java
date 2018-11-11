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

    private Context mContext;
    private FirebaseUser mUser;
    private InventoryAdapter mInventoryAdapter;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserInventoryListRef;
    private ValueEventListener mInventoryListener;
    private OnFragmentInteractionListener mListener;

    public InventoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container,
                false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mInventoryAdapter = new InventoryAdapter(mContext, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mInventoryAdapter);
        recyclerView.setHasFixedSize(true);

        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();

            // Setup database references
            mUserInventoryListRef = mDatabaseReference.child(Db.USER).child(uid).child(Db.INVENTORY_LIST);

            // read list of inventory items
            mInventoryListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<InventoryItem> items = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        InventoryItem item = new InventoryItem(id, name);
                        items.add(item);
                    }
                    mInventoryAdapter.setInventoryItems(items);
                    mInventoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mUserInventoryListRef
                    .orderByValue()
                    .addValueEventListener(mInventoryListener);

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
    public void onClick(InventoryItem inventoryItem) {
        if (mListener != null) {
            mListener.onInventoryItemSelected(inventoryItem);
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
        void onInventoryItemSelected(InventoryItem inventoryItem);
    }
}
