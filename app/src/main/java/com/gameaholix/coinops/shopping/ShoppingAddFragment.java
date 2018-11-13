package com.gameaholix.coinops.shopping;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentItemAddBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ShoppingAddFragment extends DialogFragment {
    private static final String TAG = ShoppingAddFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_SHOPPING = "CoinOpsShoppingItem";

    private Context mContext;
    private String mGameId;
    private Item mNewShoppingItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public ShoppingAddFragment() {
        // Required empty public constructor
    }

    public static ShoppingAddFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ShoppingAddFragment fragment = new ShoppingAddFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            }
            mNewShoppingItem = new Item();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mNewShoppingItem = savedInstanceState.getParcelable(EXTRA_SHOPPING);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.add_shopping_title);
        }

        // Inflate the layout for this fragment
        final FragmentItemAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_item_add, container, false);
        final View rootView = bind.getRoot();

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        bind.btnSave.setText(R.string.add_item);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = bind.etEntry.getText().toString().trim();
                Item newItem = new Item(null, mGameId, name);
                addItem(newItem);
            }
        });

        bind.btnDelete.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_SHOPPING, mNewShoppingItem);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set width and height of this DialogFragment, code block used from
        // https://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        if (getShowsDialog() && getDialog().getWindow() != null) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void addItem(Item item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            Log.d(TAG, "Failed to add item! Name field was blank.");
            return;
        }

        getDialog().dismiss();

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            DatabaseReference shopRef = mDatabaseReference.child(Db.SHOP).child(uid);
            String id = shopRef.push().getKey();

            if (!TextUtils.isEmpty(id)) {
                DatabaseReference gameShopListRef = mDatabaseReference
                        .child(Db.GAME)
                        .child(uid)
                        .child(mGameId)
                        .child(Db.SHOP_LIST)
                        .child(id);

                DatabaseReference userShopListRef = mDatabaseReference
                        .child(Db.USER)
                        .child(uid)
                        .child(Db.SHOP_LIST)
                        .child(id);

                // use atomic writes to firebase
                Map<String, Object> valuesToAdd = new HashMap<>();
                valuesToAdd.put(shopRef.child(id).getPath().toString(), item);
                valuesToAdd.put(gameShopListRef.getPath().toString(), item.getName());
                valuesToAdd.put(userShopListRef.getPath().toString(), item.getName());

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
            }

//        } else {
//            // user is not signed in
        }
    }
}
