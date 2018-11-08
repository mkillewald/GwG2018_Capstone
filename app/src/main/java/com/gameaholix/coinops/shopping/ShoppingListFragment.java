package com.gameaholix.coinops.shopping;

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
import com.gameaholix.coinops.adapter.ShoppingAdapter;
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

public class ShoppingListFragment extends Fragment implements
        ShoppingAdapter.ShoppingAdapterOnClickHandler {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private String mGameId;
    private ShoppingAdapter mShoppingAdapter;
    private DatabaseReference mShopListRef;
    private FirebaseUser mUser;
    private ValueEventListener mShoppingListener;
    private OnFragmentInteractionListener mListener;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    public static ShoppingListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ShoppingListFragment fragment = new ShoppingListFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            } else {
                mGameId = null;
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (mGameId == null) {
            // use global list reference
            mShopListRef = databaseReference
                    .child(Db.USER)
                    .child(mUser.getUid())
                    .child(Db.SHOP_LIST);
        } else {
            // use game specific list reference
            mShopListRef = databaseReference
                    .child(Db.GAME)
                    .child(mUser.getUid())
                    .child(mGameId)
                    .child(Db.SHOP_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate view for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mShoppingAdapter = new ShoppingAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mShoppingAdapter);
        recyclerView.setHasFixedSize(true);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mShoppingListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Item> shoppingList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        Item shoppingItem = new Item(id, mGameId, name);
                        shoppingList.add(shoppingItem);
                    }
                    mShoppingAdapter.setShoppingItems(shoppingList);
                    mShoppingAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            // read list of repair logs
            mShopListRef.addValueEventListener(mShoppingListener);

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mShopListRef.removeEventListener(mShoppingListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
    }

    @Override
    public void onClick(Item shoppingItem) {
        if (mListener != null) {
            mListener.onShoppingItemSelected(shoppingItem);
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
        void onShoppingItemSelected(Item shoppingItem);
    }
}
