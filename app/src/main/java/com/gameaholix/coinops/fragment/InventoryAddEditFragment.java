package com.gameaholix.coinops.fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryAddBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InventoryAddEditFragment extends DialogFragment {
    private static final String TAG = InventoryAddEditFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsInventoryEditFlag";

    private Context mContext;
    private InventoryItem mItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public InventoryAddEditFragment() {
        // Required empty public constructor
    }

    public static InventoryAddEditFragment newInstance(InventoryItem item) {
        Bundle args = new Bundle();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        args.putParcelable(EXTRA_INVENTORY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItem = getArguments().getParcelable(EXTRA_INVENTORY_ITEM);
                mEdit = true;
            } else {
                mItem = new InventoryItem();
                mEdit = false;
            }
        } else {
            mItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Hide keyboard after touch event occurs outside of EditText in DialogFragment
        // Solution used from:
        // https://stackoverflow.com/questions/16024297/is-there-an-equivalent-for-dispatchtouchevent-from-activity-in-dialog-or-dialo
        if (getActivity() != null) {
            return new Dialog(getActivity(), getTheme()) {
                @Override
                public boolean dispatchTouchEvent(@NonNull MotionEvent motionEvent) {
                    if (getCurrentFocus() != null) {
                        InputMethodManager imm =
                                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
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

        // Setup EditTexts
        if (mEdit) bind.etAddInventoryName.setText(mItem.getName());
        bind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    String validated = checkInputText(input, mItem.getName());
                    textView.setText(validated);
                    mItem.setName(validated);
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
                        String input = ((EditText) view).getText().toString().trim();
                        String validated = checkInputText(input, mItem.getName());
                        ((EditText) view).setText(validated);
                        mItem.setName(validated);
                    }
                }
            }
        });

        if (mEdit) bind.etAddInventoryDescription.setText(mItem.getDescription());
        bind.etAddInventoryDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    String validated = checkInputText(input, mItem.getDescription());
                    textView.setText(validated);
                    mItem.setDescription(validated);
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
                        String input = ((EditText) view).getText().toString().trim();
                        String validated = checkInputText(input, mItem.getDescription());
                        ((EditText) view).setText(validated);
                        mItem.setDescription(validated);
                    }
                }
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryType.setAdapter(typeAdapter);
        if (mEdit) bind.spinnerInventoryType.setSelection(mItem.getType());
        bind.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mItem.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryCondition.setAdapter(conditionAdapter);
        if (mEdit) bind.spinnerInventoryCondition.setSelection(mItem.getCondition());
        bind.spinnerInventoryCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mItem.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) {
            bind.btnSave.setText(R.string.save_changes);
        } else {
            bind.btnSave.setText(R.string.add_inventory_item);
        }
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etAddInventoryName.getText().toString().trim();
                String validated = checkInputText(input, mItem.getName());
                bind.etAddInventoryName.setText(validated);
                if (TextUtils.isEmpty(validated)) {
                    PromptUser.displayAlert(mContext,
                            R.string.error_add_inventory_failed,
                            R.string.error_name_empty);
                    Log.d(TAG, "Failed to add part! Name field was blank.");
                    return;
                } else {
                    mItem.setName(validated);
                }

                input = bind.etAddInventoryDescription.getText().toString().trim();
                validated = checkInputText(input, mItem.getDescription());
                bind.etAddInventoryDescription.setText(validated);
                mItem.setDescription(validated);

                addEditItem();
                mListener.onAddEditCompletedOrCancelled();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mItem);
        outState.putBoolean(EXTRA_EDIT_FLAG, mEdit);
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
        if (getShowsDialog()) getDialog().dismiss();

        // Add or update InventoryItem object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            final DatabaseReference inventoryRootRef = mDatabaseReference.child(Db.INVENTORY).child(uid);

            DatabaseReference inventoryRef;
            DatabaseReference userInventoryListRef;
            String itemId;

            if (mEdit) {
                itemId = mItem.getId();
            } else {
                itemId = inventoryRootRef.push().getKey();
            }

            if (!TextUtils.isEmpty(itemId)) {
                inventoryRef = inventoryRootRef.child(itemId);
                userInventoryListRef = mDatabaseReference
                        .child(Db.USER)
                        .child(uid)
                        .child(Db.INVENTORY_LIST)
                        .child(itemId);
            } else {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_item_id_empty);
                Log.e(TAG, "Failed to add or update database! Item ID cannot be an empty string.");
                return;
            }

            // convert mItem instance to Map so it can be iterated
            Map<String, Object> currentValues = mItem.getMap();

            // create new Map with full database paths as keys using values from mItem Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(inventoryRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Db.NAME)) {
                    valuesWithPath.put(userInventoryListRef.getPath().toString(), currentValues.get(key));
                }
            }

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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

    private String checkInputText(String inputText, String originalText) {
        if (textInputIsValid(inputText)) {
            // text input was valid, return the input text.
            return inputText;
        } else {
            // text input was not valid, so return the original text.
            return originalText;
        }
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

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        void onAddEditCompletedOrCancelled();
    }
}
