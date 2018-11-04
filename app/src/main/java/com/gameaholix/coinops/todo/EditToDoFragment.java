package com.gameaholix.coinops.todo;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddToDoBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditToDoFragment extends Fragment {
    private static final String TAG = EditToDoFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_VALUES = "CoinOpsToDoValuesToUpdate";

    private ToDoItem mToDoItem;
    private Bundle mValuesBundle;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;

    public EditToDoFragment() {
        // Required empty public constructor
    }

    public static EditToDoFragment newInstance(ToDoItem item) {
        Bundle args = new Bundle();
        EditToDoFragment fragment = new EditToDoFragment();
        args.putParcelable(EXTRA_TODO, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mToDoItem = getArguments().getParcelable(EXTRA_TODO);
            }
            mValuesBundle = new Bundle();
        } else {
            mToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
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
        final FragmentAddToDoBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_to_do, container, false);
        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();
            final String id = mToDoItem.getId();
            final String gameId = mToDoItem.getParentId();

            // Setup EditTexts
            bind.etTodoName.setText(mToDoItem.getName());
            bind.etTodoName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mValuesBundle.putString(Db.NAME, input);
                        } else {
                            textView.setText(mToDoItem.getName());
                        }
                        hideKeyboard(textView);
                        return true;
                    }
                    return false;
                }
            });
            bind.etTodoName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view.getId() == R.id.et_todo_name && !hasFocus) {
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (textInputIsValid(input)) {
                                mValuesBundle.putString(Db.NAME, input);
                            } else {
                                editText.setText(mToDoItem.getName());
                            }
                            hideKeyboard(editText);
                        }
                    }
                }
            });

            bind.etTodoDescription.setText(mToDoItem.getDescription());
            bind.etTodoDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mValuesBundle.putString(Db.DESCRIPTION, input);
                        } else {
                            textView.setText(mToDoItem.getDescription());
                        }
                        hideKeyboard(textView);
                        return true;
                    }
                    return false;
                }
            });
            bind.etTodoDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view.getId() == R.id.et_todo_description && !hasFocus) {
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (textInputIsValid(input)) {
                                mValuesBundle.putString(Db.DESCRIPTION, input);
                            } else {
                                editText.setText(mToDoItem.getDescription());
                            }
                            hideKeyboard(editText);
                        }
                    }
                }
            });

            // Setup RadioGroup
            RadioButton priorityButton =
                    (RadioButton) bind.rgPriority.getChildAt(mToDoItem.getPriority());
            priorityButton.setChecked(true);
            bind.rgPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    RadioButton checkedButton = radioGroup.findViewById(checkedId);
                    mValuesBundle.putInt(Db.PRIORITY, radioGroup.indexOfChild(checkedButton));
                }
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
                    String nameEntry = bind.etTodoName.getText().toString().trim();
                    if (textInputIsValid(nameEntry)) {
                        mValuesBundle.putString(Db.NAME, nameEntry);
                    }

                    String descriptionEntry = bind.etTodoDescription.getText().toString().trim();
                    if (textInputIsValid(descriptionEntry)) {
                        mValuesBundle.putString(Db.DESCRIPTION, descriptionEntry);
                    }

                    // Get database paths from helper class
                    String toDoPath = Db.getToDoPath(uid) + id;
                    String userToDoListPath = Db.getUserToDoListPath(uid) + id;
                    String gameToDoListPath = Db.getGameToDoListPath(uid, gameId) + id;

                    // Convert values Bundle to HashMap for Firebase call to updateChildren()
                    Map<String, Object> valuesMap = new HashMap<>();

                    for (String key : Db.TO_DO_STRINGS) {
                        if (mValuesBundle.containsKey(key)) {
                            valuesMap.put(toDoPath + "/" + key, mValuesBundle.getString(key));
                            if (key.equals(Db.NAME)) {
                                valuesMap.put(userToDoListPath, mValuesBundle.getString(key));
                                valuesMap.put(gameToDoListPath, mValuesBundle.getString(key));
                            }
                        }
                    }

                    for (String key : Db.TO_DO_INTS) {
                        if (mValuesBundle.containsKey(key)) {
                            valuesMap.put(toDoPath + "/" + key, mValuesBundle.getInt(key));
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

        outState.putParcelable(EXTRA_TODO, mToDoItem);
        outState.putBundle(EXTRA_VALUES, mValuesBundle);
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
