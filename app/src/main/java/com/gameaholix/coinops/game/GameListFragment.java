package com.gameaholix.coinops.game;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ListRowAdapter;
import com.gameaholix.coinops.databinding.FragmentListBinding;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.game.viewModel.GameListViewModel;

import java.util.List;

public class GameListFragment extends Fragment implements ListRowAdapter.ListAdapterOnClickHandler {
//    private static final String TAG = GameListFragment.class.getSimpleName();

    private Context mContext;
    private ListRowAdapter mGameAdapter;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public GameListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentListBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false);
        final View rootView = bind.getRoot();

        mGameAdapter = new ListRowAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        bind.rvList.setLayoutManager(linearLayoutManager);
        bind.rvList.setAdapter(mGameAdapter);
        bind.rvList.setHasFixedSize(true);

        // Setup FAB
        bind.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFabPressed();
            }
        });

        // read list of games
        if (getActivity() != null) {
            GameListViewModel gameListViewModel =
                    ViewModelProviders.of(getActivity()).get(GameListViewModel.class);
            LiveData<List<ListRow>> gameListLiveData = gameListViewModel.getListLiveData();
            gameListLiveData.observe(getActivity(), new Observer<List<ListRow>>() {
                @Override
                public void onChanged(@Nullable List<ListRow> list) {
                    if (list != null) {
                        mGameAdapter.setList(list);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onClick(ListRow row) {
        if (mListener != null) {
            mListener.onGameSelected(row);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onGameSelected(ListRow row);
        void onFabPressed();
    }
}
