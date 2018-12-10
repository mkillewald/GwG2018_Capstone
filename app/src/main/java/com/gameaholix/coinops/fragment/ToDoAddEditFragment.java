package com.gameaholix.coinops.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoAddBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ToDoAddEditFragment extends BaseDialogFragment {
    private static final String TAG = ToDoAddEditFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsTodoEditFlag";

    private Context mContext;
    private String mGameId;
    private ToDoItem mToDoItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public ToDoAddEditFragment() {
        // Required empty public constructor
    }

    // add a new to do item
    public static ToDoAddEditFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ToDoAddEditFragment fragment = new ToDoAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    // edit an existing to do item
    public static ToDoAddEditFragment newInstance(ToDoItem item) {
        Bundle args = new Bundle();
        ToDoAddEditFragment fragment = new ToDoAddEditFragment();
        args.putParcelable(EXTRA_TODO, item);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
        if (!getShowsDialog()) setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                if (getArguments().containsKey(EXTRA_GAME_ID)) {
                    mGameId = getArguments().getString(EXTRA_GAME_ID);
                    mToDoItem = new ToDoItem(mGameId);
                } else if (getArguments().containsKey(EXTRA_TODO)) {
                    mToDoItem = getArguments().getParcelable(EXTRA_TODO);
                    if (mToDoItem != null) {
                        mGameId = mToDoItem.getParentId();
                    }
                }
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
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
            if (!mEdit) getDialog().setTitle(R.string.add_to_do_title);
        }

        // Inflate the layout for this
        final FragmentToDoAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditTexts
        if (mEdit) bind.etTodoName.setText(mToDoItem.getName());
        bind.etTodoName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mToDoItem.setName(input);
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
                        String input = ((EditText) view).getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mToDoItem.setName(input);
                        } else {
                            ((EditText) view).setText(mToDoItem.getName());
                        }
                    }
                }
            }
        });

        if (mEdit) bind.etTodoDescription.setText(mToDoItem.getDescription());
        bind.etTodoDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mToDoItem.setDescription(input);
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
                        String input = ((EditText) view).getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mToDoItem.setDescription(input);
                        } else {
                            ((EditText) view).setText(mToDoItem.getDescription());
                        }
                    }
                }
            }
        });

        // Setup RadioGroup
        if (mEdit) {
            RadioButton priorityButton =
                    (RadioButton) bind.rgPriority.getChildAt(mToDoItem.getPriority());
            priorityButton.setChecked(true);
        }
        bind.rgPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedButton = radioGroup.findViewById(checkedId);
                mToDoItem.setPriority(radioGroup.indexOfChild(checkedButton));
            }
        });

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) getDialog().dismiss();
                mListener.onToDoEditCompletedOrCancelled();
            }
        });

        if (mEdit) {
            bind.btnSave.setText(R.string.save_changes);
        } else {
            bind.btnSave.setText(R.string.add_item);
        }
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etTodoName.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mToDoItem.setName(input);
                } else {
                    bind.etTodoName.setText(mToDoItem.getName());
                }

                input = bind.etTodoDescription.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mToDoItem.setDescription(input);
                } else {
                    bind.etTodoName.setText(mToDoItem.getDescription());
                }

                addEditItem();
                mListener.onToDoEditCompletedOrCancelled();
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_todo:
                if (mListener != null) {
                    mListener.onToDoDeleteButtonPressed(mToDoItem);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_TODO, mToDoItem);
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
        if (TextUtils.isEmpty(mToDoItem.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            Log.d(TAG, "Failed to add item! Name field was blank.");
            return;
        }

        if (getShowsDialog()) getDialog().dismiss();

        // Add new item or update existing item to firebase
        if (mUser != null) {
            // user is signed in

            String uid = mUser.getUid();
            DatabaseReference toDoRootRef = mDatabaseReference.child(Db.TODO).child(uid);

            String toDoId;
            if (mEdit) {
                toDoId = mToDoItem.getId();
            } else {
                toDoId = toDoRootRef.push().getKey();
            }

            if (TextUtils.isEmpty(toDoId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_item_id_empty);
                Log.e(TAG, "Failed to add or update database! ToDo ID cannot be an empty string.");
                return;
            }

            if (TextUtils.isEmpty(mGameId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_game_id_empty);
                Log.e(TAG, "Failed to add or update database! Game ID cannot be an empty string.");
                return;
            }

            DatabaseReference toDoRef = toDoRootRef.child(toDoId);
            DatabaseReference gameToDoListRef = mDatabaseReference
                    .child(Db.GAME)
                    .child(uid)
                    .child(mGameId)
                    .child(Db.TODO_LIST)
                    .child(toDoId);
            DatabaseReference userToDoListRef = mDatabaseReference
                    .child(Db.USER)
                    .child(uid)
                    .child(Db.TODO_LIST)
                    .child(toDoId);

            Map<String, Object> valuesWithPath = new HashMap<>();
            if (mEdit) {
                // convert mToDoItem instance to Map so it can be iterated
                Map<String, Object> currentValues = mToDoItem.getMap();

                // create new Map with full database paths as keys using values from the Map created above
                for (String key : currentValues.keySet()) {
                    valuesWithPath.put(toDoRef.child(key).getPath().toString(), currentValues.get(key));
                    if (key.equals(Db.NAME)) {
                        valuesWithPath.put(userToDoListRef.getPath().toString(), currentValues.get(key));
                        valuesWithPath.put(gameToDoListRef.getPath().toString(), currentValues.get(key));
                    }
                }
            } else {
                // we are adding a new item to the database
                valuesWithPath.put(toDoRef.getPath().toString(), mToDoItem);
                valuesWithPath.put(gameToDoListRef.getPath().toString(), mToDoItem.getName());
                valuesWithPath.put(userToDoListRef.getPath().toString(), mToDoItem.getName());
            }

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
        void onToDoEditCompletedOrCancelled();
        void onToDoDeleteButtonPressed(ToDoItem toDoItem);
    }
}
