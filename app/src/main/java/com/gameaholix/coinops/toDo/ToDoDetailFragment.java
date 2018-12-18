package com.gameaholix.coinops.toDo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoDetailBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToDoDetailFragment extends Fragment {
    private static final String TAG = ToDoDetailFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

    private String mItemId;
    private ToDoItem mItem;
    private FirebaseUser mUser;
    private DatabaseReference mToDoRef;
    private ValueEventListener mToDoListener;
    private OnFragmentInteractionListener mListener;

    public ToDoDetailFragment() {
        // Required empty public constructor
    }

    public static ToDoDetailFragment newInstance(String itemId) {
        Bundle args = new Bundle();
        ToDoDetailFragment fragment = new ToDoDetailFragment();
        args.putString(EXTRA_TODO_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_TODO_ID);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_TODO_ID);
            mItem = savedInstanceState.getParcelable(EXTRA_TODO);
        }

        if (TextUtils.isEmpty(mItemId)) {
            // TODO: finish this
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mToDoRef = databaseReference
                .child(Db.TODO)
                .child(mUser.getUid())
                .child(mItemId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentToDoDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_detail, container, false);

        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mToDoListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();

                    mItem = dataSnapshot.getValue(ToDoItem.class);
                    if (mItem == null) {
                        Log.d(TAG, "Error: To do item details not found");
                    } else {
                        mItem.setId(id);

                        bind.tvTodoName.setText(mItem.getName());
                        RadioButton priorityButton =
                                (RadioButton) bind.rgPriority.getChildAt(mItem.getPriority());
                        priorityButton.setChecked(true);
                        bind.tvTodoDescription.setText(mItem.getDescription());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mToDoRef.addValueEventListener(mToDoListener);

            // Setup Buttons
            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onToDoDeleteButtonPressed(mItem);
                }
            });

            bind.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onToDoEditButtonPressed(mItem);
                }
            });

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mToDoListener != null) {
            mToDoRef.removeEventListener(mToDoListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_todo:
                if (mListener != null) {
                    mListener.onToDoEditButtonPressed(mItem);
                }
                return true;
            case R.id.menu_delete_todo:
                if (mListener != null) {
                    mListener.onToDoDeleteButtonPressed(mItem);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_TODO, mItem);
        outState.putString(EXTRA_TODO_ID, mItemId);
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
        void onToDoEditButtonPressed(ToDoItem toDoItem);
        void onToDoDeleteButtonPressed(ToDoItem toDoItem);
    }
}
