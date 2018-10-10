package com.gameaholix.coinops.todo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ToDoListFragment extends Fragment implements ToDoAdapter.ToDoAdapterOnClickHandler {

    private static final String TAG = ToDoListFragment.class.getSimpleName();
    private static final String EXTRA_TODO_LIST = "CoinOpsToDoList";

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

//    private String mUsername;

    private ToDoAdapter mToDoAdapter;
    private ArrayList<ToDoItem> mToDoList;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mToDoList = new ArrayList<ToDoItem>();
        } else {
            mToDoList = savedInstanceState.getParcelableArrayList(EXTRA_TODO_LIST);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_todo_list);
        mToDoAdapter = new ToDoAdapter(getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mToDoAdapter);
        mToDoAdapter.setToDoItems(mToDoList);

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // Setup database references

            // read to do list items

            // add a to-do list item
//                    DatabaseReference todoIdRef = todoRef.push();
//                    Map<String, Object> todoDetails = new HashMap<>();
//                    todoDetails.put("name", "to do list item name");
//                    todoDetails.put("description", "to do list item description");
//                    todoDetails.put( "game", gameId);
//                    todoIdRef.setValue(todoDetails, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            String todoId = databaseReference.getKey();
//                            gameTodoListRef.child(todoId).setValue(true);
//                            userTodoListRef.child(todoId).setValue(true);
//                        }
//                    });
        } else {
            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_TODO_LIST, mToDoList);
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
        // TODO: Update argument type and name
        void onToDoItemSelected(ToDoItem toDoItem);
    }
}
