package com.gameaholix.coinops.shopping;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ShoppingAdapter;
import com.gameaholix.coinops.databinding.FragmentListWithButtonBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingListFragment extends Fragment implements
        ShoppingAdapter.ShoppingAdapterOnClickHandler {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_SHOPPING_LIST = "CoinOpsShoppingList";
    private static final String EXTRA_SHOW_ADD_BUTTON = "CoinOpsShowAddButton";

    private Context mContext;
    private String mGameId;
    private boolean mShowAddButton;
    private ShoppingAdapter mShoppingAdapter;
    private ArrayList<Item> mShoppingList;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mShopRef;
    private DatabaseReference mShopListRef;
    private FirebaseUser mUser;
    private ValueEventListener mShoppingListener;
    private FragmentListWithButtonBinding mBind;
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
                mShowAddButton = true;
            } else {
                mGameId = null;
                mShowAddButton = false;
            }
            mShoppingList = new ArrayList<>();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mShoppingList = savedInstanceState.getParcelableArrayList(EXTRA_SHOPPING_LIST);
            mShowAddButton = savedInstanceState.getBoolean(EXTRA_SHOW_ADD_BUTTON);
        }
//        setHasOptionsMenu(true);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mShopRef = mDatabaseReference
                .child(Db.SHOP)
                .child(mUser.getUid());
        if (mShowAddButton) {
            // use game specific list reference
            mShopListRef = mDatabaseReference
                    .child(Db.GAME)
                    .child(mUser.getUid())
                    .child(mGameId)
                    .child(Db.SHOP_LIST);
        } else {
            // use global list reference
            mShopListRef = mDatabaseReference
                    .child(Db.USER)
                    .child(mUser.getUid())
                    .child(Db.SHOP_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;

        // Choose which layout to inflate for this fragment depending on whether or not this fragment
        // is displayed as a game specific list or as a global list.
        if (mShowAddButton) {
            mBind = DataBindingUtil.inflate(inflater,
                    R.layout.fragment_list_with_button, container, false);
            rootView = mBind.getRoot();

            //Setup EditText
            mBind.etEntry.setHint(R.string.shopping_entry_hint);
            mBind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView);
                    }
                    return false;
                }
            });

            // Setup Button
            Button addButton = mBind.btnSave;
            addButton.setText(R.string.add_item);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mBind.etEntry.getText().toString().trim();
                    Item newItem = new Item(null, mGameId, name);
                    addItem(newItem);
                }
            });
        } else {
            rootView = inflater.inflate(R.layout.fragment_list, container, false);
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mShoppingAdapter = new ShoppingAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mShoppingAdapter);
        mShoppingAdapter.setShoppingItems(mShoppingList);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mShoppingListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mShoppingList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        Item shoppingItem = new Item(id, mGameId, name);
                        mShoppingList.add(shoppingItem);
                    }
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
        outState.putParcelableArrayList(EXTRA_SHOPPING_LIST, mShoppingList);
        outState.putBoolean(EXTRA_SHOW_ADD_BUTTON, mShowAddButton);
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

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addItem(Item item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();
            String id = mShopRef.push().getKey();

            // Get database paths from helper class
            String shopPath = Db.getShopPath(uid) + id;
            String gameShopListPath = Db.getGameShopListPath(uid, mGameId) + id;
            String userShopListPath = Db.getUserShopListPath(uid) + id;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(shopPath, item);
            valuesToAdd.put(gameShopListPath, item.getName());
            valuesToAdd.put(userShopListPath, item.getName());

            mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            if (mShowAddButton) {
                hideKeyboard(mBind.etEntry);
                mBind.etEntry.setText(null);
                mBind.etEntry.clearFocus();
            }
//        } else {
//            // user is not signed in
        }
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
