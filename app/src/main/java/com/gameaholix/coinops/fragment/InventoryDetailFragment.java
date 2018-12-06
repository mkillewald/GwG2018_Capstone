package com.gameaholix.coinops.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryDetailBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InventoryDetailFragment extends Fragment {
    private static final String TAG = InventoryDetailFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";

    private InventoryItem mItem;
    private FirebaseUser mUser;
    private DatabaseReference mInventoryRef;
    private ValueEventListener mInventoryListener;
    private OnFragmentInteractionListener mListener;

    public InventoryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItem = getArguments().getParcelable(EXTRA_INVENTORY_ITEM);
            }
        } else {
            mItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mInventoryRef = databaseReference
                .child(Db.INVENTORY)
                .child(mUser.getUid())
                .child(mItem.getId());
    }

    public static InventoryDetailFragment newInstance(InventoryItem item) {
        Bundle args = new Bundle();
        InventoryDetailFragment fragment = new InventoryDetailFragment();
        args.putParcelable(EXTRA_INVENTORY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentInventoryDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_detail, container, false);

        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // read inventory item details
            mInventoryListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mItem = dataSnapshot.getValue(InventoryItem.class);
                    if (mItem == null) {
                        Log.d(TAG, "Error: Item details not found");
                    } else {
                        mItem.setId(id);
                        String noSelection = getString(R.string.not_available);
                        String[] typeArr = getResources().getStringArray(R.array.inventory_type);
                        typeArr[0] = noSelection;
                        String[] conditionArr =
                                getResources().getStringArray(R.array.inventory_condition);
                        conditionArr[0] = noSelection;
                        bind.tvInventoryName.setText(mItem.getName());
                        bind.tvInventoryType.setText(typeArr[mItem.getType()]);
                        bind.tvInventoryCondition.setText(conditionArr[mItem.getCondition()]);
                        bind.tvInventoryDescription.setText(mItem.getDescription());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mInventoryRef.addValueEventListener(mInventoryListener);

            // Setup Buttons
            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteButtonPressed();
                }
            });

            bind.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEditButtonPressed(mItem);
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

        mInventoryRef.removeEventListener(mInventoryListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_inventory:
                mListener.onEditButtonPressed(mItem);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mItem);
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
        mInventoryRef.removeEventListener(mInventoryListener);
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
        void onEditButtonPressed(InventoryItem inventoryItem);
        void onDeleteButtonPressed();
    }
}
