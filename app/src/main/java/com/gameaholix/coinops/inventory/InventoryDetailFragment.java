package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryDetailBinding;
import com.gameaholix.coinops.model.InventoryItem;
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
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";

    private Context mContext;
    private InventoryItem mItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mInventoryRef;
    private ValueEventListener mInventoryListener;

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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mInventoryRef = mDatabaseReference
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
            final String uid = mUser.getUid();

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
                showEditInventoryDialog();
                return true;
            case R.id.menu_delete_inventory:
                showDeleteAlert();
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
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInventoryRef.removeEventListener(mInventoryListener);
    }

    private void showEditInventoryDialog() {
        if (getActivity() != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            EditInventoryFragment fragment = EditInventoryFragment.newInstance(mItem);
            fragment.show(fm, "fragment_edit_inventory");
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
            builder.setTitle(getString(R.string.really_delete_inventory_item))
                    .setMessage(getString(R.string.inventory_item_will_be_deleted))
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
        // delete inventory item
        mInventoryRef.removeValue();

        // delete inventory list entry
        mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.INVENTORY_LIST)
                .child(mItem.getId())
                .removeValue();
    }
}
