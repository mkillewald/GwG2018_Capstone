package com.gameaholix.coinops.inventory;

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
import com.gameaholix.coinops.databinding.FragmentInventoryDetailBinding;
import com.gameaholix.coinops.inventory.viewModel.InventoryItemViewModel;
import com.gameaholix.coinops.model.InventoryItem;

public class InventoryDetailFragment extends Fragment {
//    private static final String TAG = InventoryDetailFragment.class.getSimpleName();

    private LiveData<InventoryItem> mItemLiveData;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public InventoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;

        // this will cause the Activity's onPrepareOptionsMenu() method to be called
        getActivity().invalidateOptionsMenu();

        InventoryItemViewModel viewModel = ViewModelProviders
                .of(getActivity())
                .get(InventoryItemViewModel.class);
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
        final FragmentInventoryDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_detail, container, false);

        bind.setLifecycleOwner(getActivity());
        bind.setItem(mItemLiveData);

        String noSelection = getString(R.string.not_available);

        final String[] typeArray = getResources().getStringArray(R.array.inventory_type);
        typeArray[0] = noSelection;
        bind.setTypeArray(typeArray);

        final String[] conditionArray =
                getResources().getStringArray(R.array.inventory_condition);
        conditionArray[0] = noSelection;
        bind.setConditionArray(conditionArray);

        // Setup Buttons
        bind.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeleteButtonPressed();
            }
        });

        bind.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEditButtonPressed();
            }
        });

        return bind.getRoot();
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
        void onEditButtonPressed();
        void onDeleteButtonPressed();
    }
}
