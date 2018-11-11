package com.gameaholix.coinops.repair;

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
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StepEditFragment extends DialogFragment {
    private static final String TAG = StepEditFragment.class.getSimpleName();
    private static final String EXTRA_STEP = "CoinOpsRepairStep";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private Context mContext;
    private String mGameId;
    private Item mRepairStep;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mStepsRef;

    public StepEditFragment() {
        // Required empty public constructor
    }

    public static StepEditFragment newInstance(String gameId, Item repairStep) {
        StepEditFragment fragment = new StepEditFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putParcelable(EXTRA_STEP, repairStep);
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
                mRepairStep = getArguments().getParcelable(EXTRA_STEP);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mRepairStep = savedInstanceState.getParcelable(EXTRA_STEP);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStepsRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGameId)
                .child(mRepairStep.getParentId())
                .child(Db.STEPS)
                .child(mRepairStep.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.edit_repair_step_title);
        }

        // Inflate the layout for this fragment
        final FragmentItemAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_item_add, container, false);
        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup EditText
            bind.etEntry.setText(mRepairStep.getName());
            bind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (!textInputIsValid(input)) {
                            textView.setText(mRepairStep.getName());
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
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (!textInputIsValid(input)) {
                                editText.setText(mRepairStep.getName());
                            }
                            hideKeyboard(editText);
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
                        mRepairStep.setName(input);
                    }
                    updateItem();
                    getDialog().dismiss();
                }
            });

            bind.btnDelete.setText(R.string.delete_repair_step);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_STEP, mRepairStep);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set width and height of this DialogFragment, code block used from
        // https://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        if (params != null) {
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

    private void updateItem() {

        // Update Firebase
        if (mUser != null) {
            // user is signed in

            Map<String, Object> valuesToUpdate = new HashMap<>();
            valuesToUpdate.put(mStepsRef.child(Db.NAME).getPath().toString(), mRepairStep.getName());

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
            builder.setTitle(R.string.really_delete_repair_step)
                    .setMessage(R.string.repair_step_will_be_deleted)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItemData();
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

        mStepsRef.removeValue();
    }
}
