package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryDetailBinding;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InventoryDetailFragment extends Fragment {

    private static final String TAG = InventoryDetailFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY = "com.gameaholix.coinops.inventory.InventoryItem";

    private InventoryItem mInventoryItem;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    public InventoryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mInventoryItem = intent.getParcelableExtra(EXTRA_INVENTORY);
            }
        } else {
            mInventoryItem = savedInstanceState.getParcelable(EXTRA_INVENTORY);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentInventoryDetailBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_detail, container, false);

        final View rootView = binding.getRoot();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // Setup database references
            final DatabaseReference inventoryRef = mDatabaseReference.child(Db.INVENTORY)
                    .child(uid).child(mInventoryItem.getId());

            // read inventory item details
            ValueEventListener inventoryListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mInventoryItem = dataSnapshot.getValue(InventoryItem.class);
                    if (null == mInventoryItem) {
                        Log.d(TAG, "Error: Item details not found in database");
                    } else {
                        mInventoryItem.setId(id);
                        binding.tvInventoryName.setText(mInventoryItem.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            inventoryRef.addValueEventListener(inventoryListener);
        } else {
            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY, mInventoryItem);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
