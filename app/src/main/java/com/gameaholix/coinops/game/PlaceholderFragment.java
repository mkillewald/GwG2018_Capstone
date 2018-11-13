package com.gameaholix.coinops.game;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Game;


public class PlaceholderFragment extends Fragment {
//    private static final String TAG = PlaceholderFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Game mGame;

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(Game game) {
        Bundle args = new Bundle();
        PlaceholderFragment fragment = new PlaceholderFragment();
        args.putParcelable(EXTRA_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGame = getArguments().getParcelable(EXTRA_GAME);
            }

            // Show GameDetailFragmment when this fragment is first created
            if (getActivity() != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.fragment_placeholder, GameDetailFragment.newInstance(mGame));
                ft.commit();
            }
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
    }
}
