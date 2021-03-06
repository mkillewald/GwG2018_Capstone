package com.gameaholix.coinops.shopping;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ListRowAdapter;
import com.gameaholix.coinops.databinding.FragmentListBinding;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.shopping.viewModel.ShoppingListViewModel;
import com.gameaholix.coinops.shopping.viewModel.ShoppingListViewModelFactory;

import java.util.List;

public class ShoppingListFragment extends Fragment implements
        ListRowAdapter.ListAdapterOnClickHandler {
//    private static final String TAG = ShoppingListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private String mGameId;
    private ListRowAdapter mShoppingAdapter;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public ShoppingListFragment() {
    }

    /**
     * Static factory method used by ShoppingListActivity (global list)
     * @return the fragment instance
     */
    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    /**
     * Static factory method used by GameDetailActivity (game specific list)
     * @param gameId the ID of the parent Game entity
     * @return the fragment instance
     */
    public static ShoppingListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ShoppingListFragment fragment = new ShoppingListFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                // we are displaying a game specific list
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            } else {
                // we are displaying a global list
                mGameId = null;
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate view for this fragment
        final FragmentListBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false);

        mShoppingAdapter = new ListRowAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bind.rvList.setLayoutManager(linearLayoutManager);
        bind.rvList.setAdapter(mShoppingAdapter);
        bind.rvList.setHasFixedSize(true);

        if (TextUtils.isEmpty(mGameId)) {
            // Global list, hide Fab
            bind.fab.setEnabled(false);
            bind.fab.setClickable(false);
            bind.fab.setAlpha(0.0f);
        } else {
            // Game specific list, show Fab
            bind.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onShoppingFabPressed();
                }
            });
        }

        // read list of shopping items
        if (getActivity() != null) {
            ShoppingListViewModel shoppingListViewModel = ViewModelProviders
                    .of(getActivity(), new ShoppingListViewModelFactory(mGameId))
                    .get(ShoppingListViewModel.class);
            LiveData<List<ListRow>> shoppingListLiveData = shoppingListViewModel.getListLiveData();
            shoppingListLiveData.observe(getActivity(), new Observer<List<ListRow>>() {
                @Override
                public void onChanged(@Nullable List<ListRow> list) {
                    if (list != null) {
                        mShoppingAdapter.setList(list);
                    }
                }
            });
        }

        return bind.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
    }

    @Override
    public void onClick(ListRow row) {
        if (mListener != null) {
            mListener.onShoppingItemSelected(row);
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
     */
    public interface OnFragmentInteractionListener {
        void onShoppingItemSelected(ListRow row);
        void onShoppingFabPressed();
    }
}
