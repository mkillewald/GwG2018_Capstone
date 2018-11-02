package com.gameaholix.coinops.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoDetailBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
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

    private Context mContext;
    private ToDoItem mToDoItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mToDoRef;
    private ValueEventListener mToDoListener;

    public ToDoDetailFragment() {
        // Required empty public constructor
    }

    public static ToDoDetailFragment newInstance(ToDoItem toDoItem) {
        Bundle args = new Bundle();
        ToDoDetailFragment fragment = new ToDoDetailFragment();
        args.putParcelable(EXTRA_TODO, toDoItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getActivity() != null) {
                mToDoItem = getActivity().getIntent().getParcelableExtra(EXTRA_TODO);
            }
        } else {
            mToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mToDoRef = mDatabaseReference
                .child(Db.TODO)
                .child(mUser.getUid())
                .child(mToDoItem.getId());
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

                    mToDoItem = dataSnapshot.getValue(ToDoItem.class);
                    if (mToDoItem == null) {
                        Log.d(TAG, "Error: To do item details not found");
                    } else {
                        mToDoItem.setId(id);

                        bind.tvTodoName.setText(mToDoItem.getName());
                        RadioButton priorityButton =
                                (RadioButton) bind.rgPriority.getChildAt(mToDoItem.getPriority());
                        priorityButton.setChecked(true);
                        bind.tvTodoDescription.setText(mToDoItem.getDescription());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mToDoRef.addValueEventListener(mToDoListener);

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mToDoRef.removeEventListener(mToDoListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_todo:
                showEditToDoDialog();
                return true;
            case R.id.menu_delete_todo:
                showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_TODO, mToDoItem);
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

    private void showEditToDoDialog() {
        if (getActivity() != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            EditToDoFragment fragment = EditToDoFragment.newInstance(mToDoItem);
            fragment.show(fm, "fragment_edit_todo");
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
            builder.setTitle(getString(R.string.really_delete_item))
                    .setMessage(getString(R.string.item_will_be_deleted))
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItemData();
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
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
        mToDoRef.removeValue();

        // delete game to do list entry
        mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mToDoItem.getParentId())
                .child(Db.TODO_LIST)
                .child(mToDoItem.getId())
                .removeValue();

        // delete user to do list entry (global list)
        mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.TODO_LIST)
                .child(mToDoItem.getId())
                .removeValue();
    }
}
