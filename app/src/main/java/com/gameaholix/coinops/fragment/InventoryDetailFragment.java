package com.gameaholix.coinops.fragment;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryDetailBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.viewModel.InventoryItemViewModel;
import com.gameaholix.coinops.viewModel.InventoryItemViewModelFactory;

public class InventoryDetailFragment extends Fragment {
    private static final String TAG = InventoryDetailFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";

    private String mItemId;
    private InventoryItem mItem;
    private OnFragmentInteractionListener mListener;

    public InventoryDetailFragment() {
        // Required empty public constructor
    }

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
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_INVENTORY_ID);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_INVENTORY_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentInventoryDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_detail, container, false);

        final View rootView = bind.getRoot();

        if (TextUtils.isEmpty(mItemId)) {
            // TODO: finish this
        }

        String noSelection = getString(R.string.not_available);
        final String[] typeArr = getResources().getStringArray(R.array.inventory_type);
        typeArr[0] = noSelection;
        final String[] conditionArr =
                getResources().getStringArray(R.array.inventory_condition);
        conditionArr[0] = noSelection;

        // read inventory item details
        if (getActivity() != null) {
            InventoryItemViewModel viewModel = ViewModelProviders
                    .of(getActivity(), new InventoryItemViewModelFactory(mItemId))
                    .get(InventoryItemViewModel.class);
            LiveData<InventoryItem> inventoryItemLiveData = viewModel.getItemLiveData();
            inventoryItemLiveData.observe(getActivity(), new Observer<InventoryItem>() {
                @Override
                public void onChanged(@Nullable InventoryItem item) {
                    if (item != null) {
                        item.setId(mItemId);
                        bind.tvInventoryName.setText(item.getName());
                        bind.tvInventoryType.setText(typeArr[item.getType()]);
                        bind.tvInventoryCondition.setText(conditionArr[item.getCondition()]);
                        bind.tvInventoryDescription.setText(item.getDescription());
                        mItem = item;
                    }
                }
            });
        }

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
                mListener.onEditButtonPressed(mItem);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_inventory:
                mListener.onEditButtonPressed(mItem);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onEditButtonPressed(InventoryItem inventoryItem);
        void onDeleteButtonPressed();
    }
}
