package com.gameaholix.coinops.inventory;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryAddBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InventoryAddFragment extends DialogFragment {
    private static final String TAG = InventoryAddFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";

    private Context mContext;
    private InventoryItem mNewItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public InventoryAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            mNewItem = new InventoryItem();
        } else {
            mNewItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
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
            getDialog().setTitle(R.string.add_inventory_title);
        }

        // Inflate the layout for this fragment
        final FragmentInventoryAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_add, container, false);
        final View rootView = bind.getRoot();

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryType.setAdapter(typeAdapter);
        bind.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewItem.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryCondition.setAdapter(conditionAdapter);
        bind.spinnerInventoryCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewItem.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        bind.btnSave.setText(R.string.add_inventory_item);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text from EditTexts
                mNewItem.setName(bind.etAddInventoryName.getText().toString().trim());
                mNewItem.setDescription(bind.etAddInventoryDescription.getText().toString().trim());
                addItem(mNewItem);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mNewItem);
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

    private void addItem(InventoryItem item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_inventory_failed,
                    R.string.error_name_empty);
            Log.d(TAG, "Failed to add part! Name field was blank.");
            return;
        }

        getDialog().dismiss();

        // Add InventoryItem object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            final DatabaseReference inventoryRef = mDatabaseReference.child(Db.INVENTORY).child(uid);
            String id = inventoryRef.push().getKey();

            if (!TextUtils.isEmpty(id)) {
                DatabaseReference userInventoryListRef = mDatabaseReference
                        .child(Db.USER)
                        .child(uid)
                        .child(Db.INVENTORY_LIST)
                        .child(id);

                Map<String, Object> valuesToAdd = new HashMap<>();
                valuesToAdd.put(inventoryRef.child(id).getPath().toString(), item);
                valuesToAdd.put(userInventoryListRef.getPath().toString(), item.getName());

                mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
