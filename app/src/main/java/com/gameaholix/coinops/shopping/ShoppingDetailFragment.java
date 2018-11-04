package com.gameaholix.coinops.shopping;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentShoppingDetailBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShoppingDetailFragment extends Fragment {
    private static final String TAG = ShoppingDetailFragment.class.getSimpleName();
    private static final String EXTRA_SHOPPING = "CoinOpsShoppingItem";

    private Context mContext;
    private Item mShoppingItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mShopRef;
    private ValueEventListener mShopListener;
    private OnFragmentInteractionListener mListener;

    public ShoppingDetailFragment() {
        // Required empty public constructor
    }

    public static ShoppingDetailFragment newInstance(Item item) {
        Bundle args = new Bundle();
        ShoppingDetailFragment fragment = new ShoppingDetailFragment();
        args.putParcelable(EXTRA_SHOPPING, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getActivity() != null) {
                mShoppingItem = getActivity().getIntent().getParcelableExtra(EXTRA_SHOPPING);
            }
        } else {
            mShoppingItem = savedInstanceState.getParcelable(EXTRA_SHOPPING);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mShopRef = mDatabaseReference
                .child(Db.SHOP)
                .child(mUser.getUid())
                .child(mShoppingItem.getId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentShoppingDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_shopping_detail, container, false);

        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mShopListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mShoppingItem = dataSnapshot.getValue(Item.class);
                    if (mShoppingItem == null) {
                        Log.d(TAG, "Error: Shopping item details not found");
                    } else {
                        mShoppingItem.setId(id);

                        // TODO: finish binding detail layout

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mShopRef.addValueEventListener(mShopListener);

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mShopRef.removeEventListener(mShopListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_shopping:
                if (mListener != null) {
                    mListener.onEditButtonPressed(mShoppingItem);
                }
                return true;
            case R.id.menu_delete_shopping:
                showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_SHOPPING, mShoppingItem);
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

    private void showDeleteAlert() {
        if (mUser != null) {
            // user is signed in

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle(getString(R.string.really_delete_item))
                    .setMessage(getString(R.string.item_will_be_deleted))
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
        mShopRef.removeValue();

        // delete game to do list entry
        mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mShoppingItem.getParentId())
                .child(Db.SHOP_LIST)
                .child(mShoppingItem.getId())
                .removeValue();

        // delete user to do list entry (global list)
        mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.SHOP_LIST)
                .child(mShoppingItem.getId())
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
        void onEditButtonPressed(Item item);
    }
}
