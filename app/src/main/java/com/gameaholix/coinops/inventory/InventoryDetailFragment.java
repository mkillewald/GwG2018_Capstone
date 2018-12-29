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
import com.gameaholix.coinops.inventory.viewModel.InventoryItemViewModelFactory;
import com.gameaholix.coinops.model.InventoryItem;

public class InventoryDetailFragment extends Fragment {
//    private static final String TAG = InventoryDetailFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";

    private String mItemId;
    private LiveData<InventoryItem> mItemLiveData;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public InventoryDetailFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment instance
     * @param itemId the ID of the InventoryItem this fragment will display
     * @return the fragment instance
     */
    public static InventoryDetailFragment newInstance(String itemId) {
        Bundle args = new Bundle();
        InventoryDetailFragment fragment = new InventoryDetailFragment();
        args.putString(EXTRA_INVENTORY_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_INVENTORY_ID);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_INVENTORY_ID);
        }

        if (TextUtils.isEmpty(mItemId)) {
            mListener.onItemIdInvalid();
            return;
        }

        if (getActivity() != null) {
            // this will cause the Activity's onPrepareOptionsMenu() method to be called
            getActivity().invalidateOptionsMenu();

            InventoryItemViewModel viewModel = ViewModelProviders
                    .of(getActivity(), new InventoryItemViewModelFactory(mItemId))
                    .get(InventoryItemViewModel.class);
            mItemLiveData = viewModel.getItemLiveData();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_INVENTORY_ID, mItemId);
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
