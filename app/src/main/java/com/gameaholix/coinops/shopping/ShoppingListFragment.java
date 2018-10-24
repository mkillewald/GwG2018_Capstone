package com.gameaholix.coinops.shopping;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ShoppingAdapter;
import com.gameaholix.coinops.model.ShoppingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment implements
        ShoppingAdapter.ShoppingAdapterOnClickHandler {

//    private static final String TAG = ShoppingListFragment.class.getSimpleName();
    private static final String EXTRA_SHOPPING_LIST = "CoinOpsShoppingList";

    private OnFragmentInteractionListener mListener;

    private ShoppingAdapter mShoppingAdapter;
    private ArrayList<ShoppingItem> mShoppingList;

    public ShoppingListFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_shopping_list, container,
                false);

        if (savedInstanceState == null) {
            mShoppingList = new ArrayList<>();
        } else {
            mShoppingList = savedInstanceState.getParcelableArrayList(EXTRA_SHOPPING_LIST);
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_shopping_list);
        mShoppingAdapter = new ShoppingAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mShoppingAdapter);
        mShoppingAdapter.setShoppingItems(mShoppingList);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // TODO: finish this
            // Setup database references

            // read list of shopping items

            // add a shopping list item
//                    DatabaseReference shopIdRef = shopRef.push();
//                    Map<String, Object> shopDetails = new HashMap<>();
//                    shopDetails.put("name", "shopping list item name");
//                    shopDetails.put("description", "shopping list item description");
//                    shopDetails.put( "game", gameId);
//                    shopIdRef.setValue(shopDetails, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            String shopId = databaseReference.getKey();
//                            gameShopListRef.child(shopId).setValue(true);
//                            userShopListRef.child(shopId).setValue(true);
//                        }
//                    });

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_SHOPPING_LIST, mShoppingList);
    }

    @Override
    public void onClick(ShoppingItem shoppingItem) {
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
        // TODO: Update argument type and name
        void onShoppingItemSelected(ShoppingItem shoppingItem);
    }
}
