package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddInventoryBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditInventoryFragment extends Fragment {
    private static final String TAG = EditInventoryFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";
    private static final String EXTRA_VALUES = "CoinOpsInventoryValuesToUpdate";

    private Context mContext;
    private InventoryItem mItem;
    private Bundle mValuesBundle;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;

    public EditInventoryFragment() {
        // Required empty public constructor
    }

    public static EditInventoryFragment newInstance(InventoryItem item) {
        Bundle args = new Bundle();
        EditInventoryFragment fragment = new EditInventoryFragment();
        args.putParcelable(EXTRA_INVENTORY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItem = getArguments().getParcelable(EXTRA_INVENTORY_ITEM);
            }
            mValuesBundle = new Bundle();
        } else {
            mItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
            mValuesBundle = savedInstanceState.getBundle(EXTRA_VALUES);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddInventoryBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_inventory, container, false);
        final View rootView = bind.getRoot();


        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();
            final String id = mItem.getId();

            // Setup EditTexts
            bind.etAddInventoryName.setText(mItem.getName());
            bind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mValuesBundle.putString(Db.NAME, input);
                        } else {
                            textView.setText(mItem.getName());
                        }
                        hideKeyboard(textView);
                        return true;
                    }
                    return false;
                }
            });
            bind.etAddInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view.getId() == R.id.et_add_inventory_name && !hasFocus) {
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (textInputIsValid(input)) {
                                mValuesBundle.putString(Db.NAME, input);
                            } else {
                                editText.setText(mItem.getName());
                            }
                            hideKeyboard(editText);
                        }
                    }
                }
            });

            bind.etAddInventoryDescription.setText(mItem.getDescription());
            bind.etAddInventoryDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mValuesBundle.putString(Db.DESCRIPTION, input);
                        } else {
                            textView.setText(mItem.getDescription());
                        }
                        hideKeyboard(textView);
                        return true;
                    }
                    return false;
                }
            });
            bind.etAddInventoryDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view.getId() == R.id.et_add_inventory_description && !hasFocus) {
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (textInputIsValid(input)) {
                                mValuesBundle.putString(Db.DESCRIPTION, input);
                            } else {
                                editText.setText(mItem.getDescription());
                            }
                            hideKeyboard(editText);
                        }
                    }
                }
            });

            // Setup Spinners
            ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.inventory_type, android.R.layout.simple_spinner_item);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerInventoryType.setAdapter(typeAdapter);
            bind.spinnerInventoryType.setSelection(mItem.getType());
            bind.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.TYPE, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.inventory_condition, android.R.layout.simple_spinner_item);
            conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerInventoryCondition.setAdapter(conditionAdapter);
            bind.spinnerInventoryCondition.setSelection(mItem.getCondition());
            bind.spinnerInventoryCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.CONDITION, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            // Setup Buttons
            bind.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEditCompletedOrCancelled();
                }
            });

            bind.btnSave.setText(R.string.save_changes);
            bind.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String nameEntry = bind.etAddInventoryName.getText().toString().trim();
                    if (textInputIsValid(nameEntry)) {
                        mValuesBundle.putString(Db.NAME, nameEntry);
                    }

                    String descriptionEntry = bind.etAddInventoryDescription.getText().toString().trim();
                    if (textInputIsValid(descriptionEntry)) {
                        mValuesBundle.putString(Db.DESCRIPTION, descriptionEntry);
                    }

                    // Get database paths from helper class
                    String inventoryPath = Db.getInventoryPath(uid) + id;
                    String userInventoryListPath = Db.getInventoryListPath(uid) + id;

                    // Convert values Bundle to HashMap for Firebase call to updateChildren()
                    Map<String, Object> valuesMap = new HashMap<>();

                    for (String key : Db.INVENTORY_STRINGS) {
                        if (mValuesBundle.containsKey(key)) {
                            valuesMap.put(inventoryPath + "/" + key, mValuesBundle.getString(key));
                            if (key.equals(Db.NAME)) {
                                valuesMap.put(userInventoryListPath, mValuesBundle.getString(key));
                            }
                        }
                    }

                    for (String key : Db.INVENTORY_INTS) {
                        if (mValuesBundle.containsKey(key)) {
                            valuesMap.put(inventoryPath + "/" + key, mValuesBundle.getInt(key));
                        }
                    }

                    updateItem(valuesMap);
                    mListener.onEditCompletedOrCancelled();
                }
            });

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mItem);
        outState.putBundle(EXTRA_VALUES, mValuesBundle);
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

    private boolean textInputIsValid(String inputText) {
        boolean result = true;

        // TODO: possibly add more validation checks, and return false if any one of them fails.
        if (TextUtils.isEmpty(inputText)) {
            result = false;
        }

        return result;
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateItem(Map<String, Object> valuesToUpdate) {
        // TODO: add checks for if game name already exists.

        // Update Firebase
        if (mUser != null) {
            // user is signed in

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
        void onEditCompletedOrCancelled();
    }
}
