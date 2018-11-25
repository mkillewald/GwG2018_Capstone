package com.gameaholix.coinops.game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.GameAdapter;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.viewModel.GameListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class GameListFragment extends Fragment implements GameAdapter.GameAdapterOnClickHandler {
//    private static final String TAG = GameListFragment.class.getSimpleName();

    private Context mContext;
    private GameAdapter mGameAdapter;
    private FirebaseUser mUser;
    private OnFragmentInteractionListener mListener;

    public GameListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_list, container,
                false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mGameAdapter = new GameAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mGameAdapter);
        recyclerView.setHasFixedSize(true);

        // Setup FAB
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFabPressed();
            }
        });

        if (mUser != null) {
            // user is signed in

            // read list of games
            GameListViewModel viewModel = ViewModelProviders.of(this).get(GameListViewModel.class);
            LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData(mUser.getUid());
            liveData.observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        ArrayList<Game> games = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String gameId = child.getKey();
                            String name = (String) child.getValue();
                            Game game = new Game(gameId, name);
                            games.add(game);
                        }
                        mGameAdapter.setGames(games);
                        mGameAdapter.notifyDataSetChanged();
                    }
                }
            });

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onClick(Game game) {
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
        void onGameSelected(Game game);
        void onFabPressed();
    }
}
