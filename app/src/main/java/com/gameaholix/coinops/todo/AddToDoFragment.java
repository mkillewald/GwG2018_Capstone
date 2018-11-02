package com.gameaholix.coinops.todo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddToDoBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// TODO: finish this

public class AddToDoFragment extends DialogFragment {
    private static final String TAG = AddToDoFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private Context mContext;
    private String mGameId;
    private ToDoItem mNewToDoItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public AddToDoFragment() {
        // Required empty public constructor
    }

    public static AddToDoFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        AddToDoFragment fragment = new AddToDoFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            }
            mNewToDoItem = new ToDoItem(mGameId);

        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mNewToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        final FragmentAddToDoBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_to_do, container, false);
        final View rootView = bind.getRoot();

        //Setup EditTexts
        bind.etTodoName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        hideKeyboard(editText);
                    }
                }
            }
        });

        bind.etTodoDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        hideKeyboard(editText);
                    }
                }
            }
        });

        // Setup radio group
        bind.rgPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedButton = radioGroup.findViewById(checkedId);
                mNewToDoItem.setPriority(radioGroup.indexOfChild(checkedButton));
            }
        });

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
                // get text from EditTexts
                mNewToDoItem.setName(bind.etTodoName.getText().toString().trim());
                mNewToDoItem.setDescription(bind.etTodoDescription.getText().toString().trim());
                addItem(mNewToDoItem);
            }
        });

        return rootView;
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_TODO, mNewToDoItem);
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

    private void addItem(ToDoItem item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            return;
        }

        getDialog().dismiss();

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in

            String uid = mUser.getUid();
            String id = mDatabaseReference.child(Db.TODO).child(uid).push().getKey();

            // Get database paths from helper class
            String toDoPath = Db.getToDoPath(uid) + id;
            String gameToDoListPath = Db.getGameToDoListPath(uid, mGameId) + id;
            String userToDoListPath = Db.getUserToDoListPath(uid) + id;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(toDoPath, item);
            valuesToAdd.put(gameToDoListPath, item.getName());
            valuesToAdd.put(userToDoListPath, item.getName());

            Log.d(TAG, valuesToAdd.toString());

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

//        } else {
//            // user is not signed in
        }
    }

}
