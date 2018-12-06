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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentListBinding;
import com.gameaholix.coinops.adapter.GameAdapter;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.viewModel.GameListViewModel;

import java.util.List;

public class GameListFragment extends Fragment implements GameAdapter.GameAdapterOnClickHandler {
//    private static final String TAG = GameListFragment.class.getSimpleName();

    private Context mContext;
    private GameAdapter mGameAdapter;
    private OnFragmentInteractionListener mListener;

    public GameListFragment() {
        // Required empty public constructor
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

        mGameAdapter = new GameAdapter(this);
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
        GameListViewModel gameListViewModel =
                ViewModelProviders.of(this).get(GameListViewModel.class);
        LiveData<List<ListRow>> gameListLiveData = gameListViewModel.getGameListLiveData();
        if (getActivity() != null) {
            gameListLiveData.observe(getActivity(), new Observer<List<ListRow>>() {
                @Override
                public void onChanged(@Nullable List<ListRow> games) {
                    if (games != null) {
                        mGameAdapter.setGames(games);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onClick(ListRow game) {
        if (mListener != null) {
            mListener.onGameSelected(game);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onGameSelected(ListRow game);
        void onFabPressed();
    }
}
