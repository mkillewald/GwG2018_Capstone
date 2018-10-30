package com.gameaholix.coinops.todo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ToDoAdapter;
import com.gameaholix.coinops.databinding.FragmentListWithButtonBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoListFragment extends Fragment implements ToDoAdapter.ToDoAdapterOnClickHandler {
    private static final String TAG = ToDoListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_TODO_LIST = "CoinOpsToDoList";
    private static final String EXTRA_SHOW_ADD_BUTTON = "CoinOpsShowAddButton";

    private Context mContext;
    private String mGameId;
    private boolean mShowAddButton;
    private ToDoAdapter mToDoAdapter;
    private ArrayList<ToDoItem> mToDoList;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mToDoRef;
    private DatabaseReference mToDoListRef;
    private FirebaseUser mUser;
    private ValueEventListener mToDoListener;
    private FragmentListWithButtonBinding mBind;
    private OnFragmentInteractionListener mListener;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    public static ToDoListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ToDoListFragment fragment = new ToDoListFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
                mShowAddButton = true;
            } else {
                mGameId = null;
                mShowAddButton = false;
            }
            mToDoList = new ArrayList<>();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mToDoList = savedInstanceState.getParcelableArrayList(EXTRA_TODO_LIST);
            mShowAddButton = savedInstanceState.getBoolean(EXTRA_SHOW_ADD_BUTTON);
        }
//        setHasOptionsMenu(true);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mToDoRef = mDatabaseReference
                .child(Db.TODO)
                .child(mUser.getUid());
        if (mShowAddButton) {
            // use game specific list reference
            mToDoListRef = mDatabaseReference
                    .child(Db.GAME)
                    .child(mUser.getUid())
                    .child(mGameId)
                    .child(Db.TODO_LIST);
        } else {
            // use global list reference
            mToDoListRef = mDatabaseReference
                    .child(Db.USER)
                    .child(mUser.getUid())
                    .child(Db.TODO_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;

        // Choose which layout to inflate for this fragment depending on whether or not this fragment
        // is displayed as a game specific list or as a global list.
        if (mShowAddButton) {
            mBind = DataBindingUtil.inflate(inflater,
                    R.layout.fragment_list_with_button, container, false);
            rootView = mBind.getRoot();

            //Setup EditText
            mBind.etEntry.setHint(R.string.shopping_entry_hint);
            mBind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView);
                    }
                    return false;
                }
            });

            // Setup Button
            Button addButton = mBind.btnSave;
            addButton.setText(R.string.add_item);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name =  mBind.etEntry.getText().toString().trim();
                    ToDoItem newItem = new ToDoItem(null, mGameId, name);
                    addItem(newItem);
                }
            });
        } else {
            rootView = inflater.inflate(R.layout.fragment_list, container, false);
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mToDoAdapter = new ToDoAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mToDoAdapter);
        mToDoAdapter.setToDoItems(mToDoList);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mToDoListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mToDoList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        ToDoItem toDoItem = new ToDoItem(id, mGameId, name);
                        mToDoList.add(toDoItem);
                    }
                    mToDoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            // read list of repair logs
            mToDoListRef.addValueEventListener(mToDoListener);

//        } else {
            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mToDoListRef.removeEventListener(mToDoListener);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelableArrayList(EXTRA_TODO_LIST, mToDoList);
        outState.putBoolean(EXTRA_SHOW_ADD_BUTTON, mShowAddButton);
    }

    @Override
    public void onClick(ToDoItem toDoItem) {
        if (mListener != null) {
            mListener.onToDoItemSelected(toDoItem);
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

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addItem(ToDoItem item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();
            String id = mToDoRef.push().getKey();

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

            if (mShowAddButton) {
                hideKeyboard(mBind.etEntry);
                mBind.etEntry.setText(null);
                mBind.etEntry.clearFocus();
            }
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
        // TODO: Update argument type and name
        void onToDoItemSelected(ToDoItem toDoItem);
    }
}
