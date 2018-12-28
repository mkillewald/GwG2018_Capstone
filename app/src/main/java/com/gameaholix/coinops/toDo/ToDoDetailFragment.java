package com.gameaholix.coinops.toDo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoDetailBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModel;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModelFactory;

public class ToDoDetailFragment extends Fragment {
    private static final String TAG = ToDoDetailFragment.class.getSimpleName();
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

    private String mItemId;
    private LiveData<ToDoItem> mItemLiveData;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public ToDoDetailFragment() {
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
//        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_TODO_ID);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_TODO_ID);
        }

        if (TextUtils.isEmpty(mItemId)) {
            mListener.onItemIdInvalid();
            return;
        }

        if (getActivity() != null) {
            ToDoItemViewModel viewModel = ViewModelProviders
                    .of(getActivity(), new ToDoItemViewModelFactory(mItemId))
                    .get(ToDoItemViewModel.class);
            mItemLiveData = viewModel.getItemLiveData();
        }

//        mToDoRef = databaseReference
//                .child(Fb.TODO)
//                .child(mUser.getUid())
//                .child(mItemId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentToDoDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_detail, container, false);

        bind.setLifecycleOwner(getActivity());
        bind.setItem(mItemLiveData);

//            // Setup event listener
//            mToDoListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String id = dataSnapshot.getKey();
//
//                    mItem = dataSnapshot.getValue(ToDoItem.class);
//                    if (mItem == null) {
//                        Log.d(TAG, "Error: To do item details not found");
//                    } else {
//
//                        RadioButton priorityButton =
//                                (RadioButton) bind.rgPriority.getChildAt(mItem.getPriority());
//                        priorityButton.setChecked(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            };
//            mToDoRef.addValueEventListener(mToDoListener);

        // Setup Buttons
        bind.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onToDoDeleteButtonPressed();
            }
        });

        bind.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onToDoEditButtonPressed();
            }
        });

        return bind.getRoot();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.menu_edit_todo:
//                if (mListener != null) {
//                    mListener.onToDoEditButtonPressed();
//                }
//                return true;
//            case R.id.menu_delete_todo:
//                showDeleteAlert();
//                return true;
//            default:
//                return super.onOptionsItemSelected(menuItem);
//        }
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
     */
    public interface OnFragmentInteractionListener {
        void onItemIdInvalid();
        void onToDoEditButtonPressed();
        void onToDoDeleteButtonPressed();
    }
}
