package com.gameaholix.coinops.shopping;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentItemAddBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ShoppingAddEditFragment extends BaseDialogFragment {
    private static final String TAG = ShoppingAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_SHOPPING = "CoinOpsShoppingItem";
    private static final String EXTRA_SHOPPING_ID = "CoinOpsShoppingId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsShoppingEditFlag";

    private Context mContext;
    private String mGameId;
    private String mShoppingId;
    private Item mShoppingItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    private ValueEventListener mShopListener;
    private DatabaseReference mShopRef;

    public ShoppingAddEditFragment() {
        // Required empty public constructor
    }

    // add a new shopping item
    public static ShoppingAddEditFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ShoppingAddEditFragment fragment = new ShoppingAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    // update an existing shopping item
    public static ShoppingAddEditFragment newInstance(@Nullable String gameId, String itemId) {
        Bundle args = new Bundle();
        ShoppingAddEditFragment fragment = new ShoppingAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putString(EXTRA_SHOPPING_ID, itemId);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
                if (mEdit) {
                    mGameId = getArguments().getString(EXTRA_GAME_ID);
                    mShoppingId = getArguments().getString(EXTRA_SHOPPING_ID);
                } else {
                    mGameId = getArguments().getString(EXTRA_GAME_ID);
                }
                mShoppingItem = new Item(mGameId);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mShoppingId = savedInstanceState.getString(EXTRA_SHOPPING_ID);
            mShoppingItem = savedInstanceState.getParcelable(EXTRA_SHOPPING);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
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
            if (mEdit) {
                getDialog().setTitle(R.string.edit_shopping_title);
            } else {
                getDialog().setTitle(R.string.add_shopping_title);
            }
        }

        // Inflate the layout for this fragment
        final FragmentItemAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_item_add, container, false);
        final View rootView = bind.getRoot();

        if (mUser != null && mEdit) {
            // user is signed in

            // TODO: want to find better way to do this.
            if (!TextUtils.isEmpty(mShoppingId)) {
                mShopRef = mDatabaseReference
                        .child(Fb.SHOP)
                        .child(mUser.getUid())
                        .child(mShoppingId);


                // Setup event listener
                // This is needed to retrieve the parentId from the database
                mShopListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mShoppingItem = dataSnapshot.getValue(Item.class);
                        bind.etEntry.setText(mShoppingItem.getName());
                        if (TextUtils.isEmpty(mGameId)) mGameId = mShoppingItem.getParentId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mShopRef.addListenerForSingleValueEvent(mShopListener);
            } else {
                PromptUser.displayAlert(mContext,
                        R.string.error_read_database_failed,
                        R.string.error_item_id_empty);
                Log.e(TAG, "Failed to add or update database! Repair Log ID cannot be an empty string.");
            }
        }

        // Setup EditText
        if (mEdit) bind.etEntry.setText(mShoppingItem.getName());
        bind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Verify input and hide keyboard if IME_ACTION_DONE
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mShoppingItem.setName(input);
                    } else {
                        textView.setText(mShoppingItem.getName());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        bind.etEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_entry && !hasFocus) {
                    // Verify input if editText loses focus
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        String input = editText.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mShoppingItem.setName(input);
                        } else {
                            editText.setText(mShoppingItem.getName());
                        }
                    }
                }
            }
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) getDialog().dismiss();
                mListener.onShoppingAddEditCompletedOrCancelled();
            }
        });

        if (mEdit) {
            bind.btnSave.setText(R.string.save_changes);
            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getShowsDialog()) getDialog().dismiss();
                    showDeleteAlert();
                    mListener.onShoppingAddEditCompletedOrCancelled();
                }
            });
        } else {
            bind.btnSave.setText(R.string.add_item);
            bind.btnDelete.setVisibility(View.GONE);
        }
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etEntry.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mShoppingItem.setName(input);
                } else {
                    bind.etEntry.setText(mShoppingItem.getName());
                }

                addEditItem();
                mListener.onShoppingAddEditCompletedOrCancelled();

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mShopListener != null) {
            mShopRef.removeEventListener(mShopListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putString(EXTRA_SHOPPING_ID, mShoppingId);
        outState.putParcelable(EXTRA_SHOPPING, mShoppingItem);
        outState.putBoolean(EXTRA_EDIT_FLAG, mEdit);
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

    private void addEditItem() {
        if (TextUtils.isEmpty(mShoppingItem.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            Log.d(TAG, "Failed to add item! Name field was blank.");
            return;
        }

        if (getShowsDialog()) getDialog().dismiss();

        // Add new item or update existing item to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            DatabaseReference shopRootRef = mDatabaseReference.child(Fb.SHOP).child(uid);

            if (!mEdit) {
                mShoppingId = shopRootRef.push().getKey();
            }

            if (TextUtils.isEmpty(mShoppingId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_log_id_empty);
                Log.e(TAG, "Failed to add or update database! Repair Log ID cannot be an empty string.");
                return;
            }

            if (TextUtils.isEmpty(mGameId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_game_id_empty);
                Log.e(TAG, "addEditItem: Failed to add or update database! Game ID cannot be an empty string.");
                return;
            }

            DatabaseReference gameShopListRef = mDatabaseReference
                    .child(Fb.GAME)
                    .child(uid)
                    .child(mGameId)
                    .child(Fb.SHOP_LIST)
                    .child(mShoppingId);

            DatabaseReference userShopListRef = mDatabaseReference
                    .child(Fb.USER)
                    .child(uid)
                    .child(Fb.SHOP_LIST)
                    .child(mShoppingId);

            Map<String, Object> valuesWithPath = new HashMap<>();
            if (mEdit) {
                // convert mShoppingItem instance to Map so it can be iterated
                Map<String, Object> currentValues = mShoppingItem.getMap();

                // create new Map with full database paths as keys using values from the Map created above
                for (String key : currentValues.keySet()) {
                    valuesWithPath.put(mShopRef.child(key).getPath().toString(), currentValues.get(key));
                    if (key.equals(Fb.NAME)) {
                        valuesWithPath.put(gameShopListRef.getPath().toString(), mShoppingItem.getName());
                        valuesWithPath.put(userShopListRef.getPath().toString(), mShoppingItem.getName());
                    }
                }
            } else {
                // we are adding a new item to the database
                valuesWithPath.put(shopRootRef.child(mShoppingId).getPath().toString(), mShoppingItem);
                valuesWithPath.put(gameShopListRef.getPath().toString(), mShoppingItem.getName());
                valuesWithPath.put(userShopListRef.getPath().toString(), mShoppingItem.getName());
            }

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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

//        } else {
//            // user is not signed in
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
            builder.setTitle(R.string.really_delete_item)
                    .setMessage(R.string.item_will_be_deleted)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mShoppingItem.getParentId() != null) {
                                deleteItemData();
                            } else {
                                PromptUser.displayAlert(mContext, R.string.error_delete_item_failed,
                                        R.string.error_please_try_again);
                                Log.e(TAG, "ERROR: parent id is null");
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
        // delete shopping item
        mShopRef.removeValue();

        // delete game shopping list entry
        mDatabaseReference
                .child(Fb.GAME)
                .child(mUser.getUid())
                .child(mShoppingItem.getParentId())
                .child(Fb.SHOP_LIST)
                .child(mShoppingId)
                .removeValue();

        // delete user shopping entry (global list)
        mDatabaseReference
                .child(Fb.USER)
                .child(mUser.getUid())
                .child(Fb.SHOP_LIST)
                .child(mShoppingId)
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
        void onShoppingAddEditCompletedOrCancelled();
    }
}
