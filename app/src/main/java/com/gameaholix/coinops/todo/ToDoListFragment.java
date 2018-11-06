package com.gameaholix.coinops.todo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ToDoAdapter;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ToDoListFragment extends Fragment implements ToDoAdapter.ToDoAdapterOnClickHandler {
    private static final String TAG = ToDoListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_SHOW_ADD_BUTTON = "CoinOpsShowAddButton";

    private String mGameId;
    private boolean mShowAddButton;
    private ToDoAdapter mToDoAdapter;
    private DatabaseReference mToDoListRef;
    private FirebaseUser mUser;
    private ValueEventListener mToDoListener;
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
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mShowAddButton = savedInstanceState.getBoolean(EXTRA_SHOW_ADD_BUTTON);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (mShowAddButton) {
            // use game specific list reference
            mToDoListRef = databaseReference
                    .child(Db.GAME)
                    .child(mUser.getUid())
                    .child(mGameId)
                    .child(Db.TODO_LIST);
        } else {
            // use global list reference
            mToDoListRef = databaseReference
                    .child(Db.USER)
                    .child(mUser.getUid())
                    .child(Db.TODO_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate view for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mToDoAdapter = new ToDoAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mToDoAdapter);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mToDoListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<ToDoItem> toDoList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        ToDoItem toDoItem = new ToDoItem(id, mGameId, name);
                        toDoList.add(toDoItem);
                    }
                    mToDoAdapter.setToDoItems(toDoList);
                    mToDoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
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
        void onToDoItemSelected(ToDoItem toDoItem);
    }
}
