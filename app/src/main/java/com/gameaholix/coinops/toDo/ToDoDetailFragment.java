package com.gameaholix.coinops.toDo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoDetailBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModel;

public class ToDoDetailFragment extends Fragment {
    private static final String TAG = ToDoDetailFragment.class.getSimpleName();

    private LiveData<ToDoItem> mItemLiveData;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public ToDoDetailFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment instance
     * @return the fragment instance
     */
    public static ToDoDetailFragment newInstance() {
        return new ToDoDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;

        // this will cause the Activity's onPrepareOptionsMenu() method to be called
        getActivity().invalidateOptionsMenu();

        ToDoItemViewModel viewModel = ViewModelProviders
                .of(getActivity())
                .get(ToDoItemViewModel.class);
        String itemId = viewModel.getItemId();
        mItemLiveData = viewModel.getItemLiveData();

        if (TextUtils.isEmpty(itemId)) {
            mListener.onItemIdInvalid();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentToDoDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_detail, container, false);

        bind.setLifecycleOwner(getActivity());
        bind.setItem(mItemLiveData);

        if (getActivity() != null) {
            mItemLiveData.observe(getActivity(), new Observer<ToDoItem>() {
                @Override
                public void onChanged(@Nullable ToDoItem toDoItem) {
                    if (toDoItem != null) {
                        // TODO: figure out how to do this with xml
                        RadioButton priorityButton =
                                (RadioButton) bind.rgPriority.getChildAt(toDoItem.getPriority());
                        priorityButton.setChecked(true);
                    }
                }
            });
        }

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
