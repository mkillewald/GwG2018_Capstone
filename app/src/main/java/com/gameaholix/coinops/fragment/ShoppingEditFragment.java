package com.gameaholix.coinops.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentItemAddBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ShoppingEditFragment extends DialogFragment {
    private static final String TAG = ShoppingEditFragment.class.getSimpleName();
    private static final String EXTRA_SHOPPING = "CoinOpsShoppingItem";

    private Context mContext;
    private Item mShoppingItem;
    private ValueEventListener mShopListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mShopRef;

    public ShoppingEditFragment() {
        // Required empty public constructor
    }

    public static ShoppingEditFragment newInstance(Item shoppingItem) {
        // When this factory method is called from the combined (global) shopping list,
        // the shopping list item that is passed in will not have a parentId set.
        // We will need to obtain the parentId from the database.
        Bundle args = new Bundle();
        ShoppingEditFragment fragment = new ShoppingEditFragment();
        args.putParcelable(EXTRA_SHOPPING, shoppingItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mShoppingItem = getArguments().getParcelable(EXTRA_SHOPPING);
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
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.edit_shopping_title);
        }

        // Inflate the layout for this fragment
        final FragmentItemAddBinding bind = DataBindingUtil.inflate(inflater,
                R.layout.fragment_item_add, container, false);
        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            if (mShoppingItem.getParentId() == null) {
                // Setup event listener
                // This is needed to retrieve the parentId from the database
                mShopListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.getKey();

                        mShoppingItem = dataSnapshot.getValue(Item.class);

                        // mShoppingItem should now have a parentId set

                        if (mShoppingItem == null) {
                            Log.d(TAG, "Error: To do item details not found");
                        } else {
                            mShoppingItem.setId(id);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mShopRef.addListenerForSingleValueEvent(mShopListener);
            }

            // Setup EditText
            bind.etEntry.setText(mShoppingItem.getName());
            bind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (!textInputIsValid(input)) {
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
                        if (view instanceof EditText) {
                            String input = ((EditText) view).getText().toString().trim();
                            if (!textInputIsValid(input)) {
                                ((EditText) view).setText(mShoppingItem.getName());
                            }
                        }
                    }
                }
            });

            // Setup Buttons
            bind.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });

            bind.btnSave.setText(R.string.save_changes);
            bind.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String input = bind.etEntry.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mShoppingItem.setName(input);
                    }
                    getDialog().dismiss();

                    if (mShoppingItem.getParentId() != null) {
                        updateItem();
                    } else {
                        PromptUser.displayAlert(mContext, R.string.error_edit_shopping_failed,
                                R.string.error_please_try_again);
                        Log.e(TAG, "ERROR: parent id is null");
                    }
                }
            });

            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                    showDeleteAlert();
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

        if (mShopListener != null) {
            mShopRef.removeEventListener(mShopListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_SHOPPING, mShoppingItem);
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

    private boolean textInputIsValid(String inputText) {
        boolean result = true;

        // TODO: possibly add more validation checks, and return false if any one of them fails.
        if (TextUtils.isEmpty(inputText)) {
            Log.d(TAG, "User input was blank or empty.");
            result = false;
        }

        return result;
    }

    private void updateItem() {

        // Update Firebase
        if (mUser != null) {
            // user is signed in

            DatabaseReference gameShopListRef = mDatabaseReference
                    .child(Db.GAME)
                    .child(mUser.getUid())
                    .child(mShoppingItem.getParentId())
                    .child(Db.SHOP_LIST)
                    .child(mShoppingItem.getId());

            DatabaseReference userShopListRef = mDatabaseReference
                    .child(Db.USER)
                    .child(mUser.getUid())
                    .child(Db.SHOP_LIST)
                    .child(mShoppingItem.getId());

            // use atomic writes to firebase
            Map<String, Object> valuesToUpdate = new HashMap<>();
            valuesToUpdate.put(mShopRef.getPath().toString(), mShoppingItem);
            valuesToUpdate.put(gameShopListRef.getPath().toString(), mShoppingItem.getName());
            valuesToUpdate.put(userShopListRef.getPath().toString(), mShoppingItem.getName());

            mDatabaseReference.updateChildren(valuesToUpdate, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
                                PromptUser.displayAlert(mContext, R.string.error_delete_shopping_failed,
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
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mShoppingItem.getParentId())
                .child(Db.SHOP_LIST)
                .child(mShoppingItem.getId())
                .removeValue();

        // delete user shopping entry (global list)
        mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.SHOP_LIST)
                .child(mShoppingItem.getId())
                .removeValue();
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
