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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoAddBinding;
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

public class ToDoAddFragment extends DialogFragment {
    private static final String TAG = ToDoAddFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private Context mContext;
    private String mGameId;
    private ToDoItem mNewToDoItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public ToDoAddFragment() {
        // Required empty public constructor
    }

    public static ToDoAddFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ToDoAddFragment fragment = new ToDoAddFragment();
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
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.add_to_do_title);
        }

        // Inflate the layout for this
        final FragmentToDoAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_add, container, false);
        final View rootView = bind.getRoot();

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

    private void addItem(ToDoItem item) {
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
            String id = mDatabaseReference.child(Db.TODO).child(uid).push().getKey();

            if (!TextUtils.isEmpty(id)) {
                DatabaseReference toDoRef = mDatabaseReference
                        .child(Db.TODO)
                        .child(uid)
                        .child(id);

                DatabaseReference gameToDoListRef = mDatabaseReference
                        .child(Db.GAME)
                        .child(uid)
                        .child(mGameId)
                        .child(Db.TODO_LIST)
                        .child(id);

                DatabaseReference userToDoListRef = mDatabaseReference
                        .child(Db.USER)
                        .child(uid)
                        .child(Db.TODO_LIST)
                        .child(id);

                Map<String, Object> valuesToAdd = new HashMap<>();
                valuesToAdd.put(toDoRef.getPath().toString(), item);
                valuesToAdd.put(gameToDoListRef.getPath().toString(), item.getName());
                valuesToAdd.put(userToDoListRef.getPath().toString(), item.getName());

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
            }

//        } else {
//            // user is not signed in
        }
    }
}
