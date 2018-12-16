package com.gameaholix.coinops.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
//    private static final String TAG = InventoryDetailFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";

    private Context mContext;
    private String mItemId;
    private InventoryItemViewModel mViewModel;
    private LiveData<InventoryItem> mItemLiveData;
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

        if (TextUtils.isEmpty(mItemId)) {
            mListener.onItemIdInvalid();
            return;
        }

        if (getActivity() != null) {
            mViewModel = ViewModelProviders
                    .of(getActivity(), new InventoryItemViewModelFactory(mItemId))
                    .get(InventoryItemViewModel.class);
            mItemLiveData = mViewModel.getItemLiveData();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentInventoryDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_detail, container, false);

        final View rootView = bind.getRoot();

        String noSelection = getString(R.string.not_available);
        final String[] typeArr = getResources().getStringArray(R.array.inventory_type);
        typeArr[0] = noSelection;
        final String[] conditionArr =
                getResources().getStringArray(R.array.inventory_condition);
        conditionArr[0] = noSelection;

        if (getActivity() != null) {
            mItemLiveData.observe(getActivity(), new Observer<InventoryItem>() {
                @Override
                public void onChanged(@Nullable InventoryItem item) {
                    if (item != null) {
                        bind.tvInventoryName.setText(item.getName());
                        bind.tvInventoryType.setText(typeArr[item.getType()]);
                        bind.tvInventoryCondition.setText(conditionArr[item.getCondition()]);
                        bind.tvInventoryDescription.setText(item.getDescription());
                    }
                }
            });

        }

        // Setup Buttons
        bind.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert();
            }
        });

        bind.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEditButtonPressed();
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        InventoryItem inventoryItem = mItemLiveData.getValue();
        switch (menuItem.getItemId()) {
            case R.id.menu_edit_inventory:
                if (inventoryItem != null) mListener.onEditButtonPressed();
                return true;
            case R.id.menu_delete_inventory:
                if (inventoryItem != null) showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
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

    private void showDeleteAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle(R.string.really_delete_inventory_item)
                .setMessage(R.string.inventory_item_will_be_deleted)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItemData();
                        mListener.onDeleteCompleted();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteItemData() {
        mViewModel.delete();
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
        void onItemIdInvalid();
        void onEditButtonPressed();
        void onDeleteCompleted();
    }
}
