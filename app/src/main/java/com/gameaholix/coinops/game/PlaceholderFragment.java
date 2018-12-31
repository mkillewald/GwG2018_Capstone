package com.gameaholix.coinops.game;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;


public class PlaceholderFragment extends Fragment {
//    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    /**
     * Required empty public constructor
     */
    public PlaceholderFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment instance
     * @return the fragment instance
     */
    public static PlaceholderFragment newInstance() {
        return new PlaceholderFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);

        if (savedInstanceState == null) {
            // Show GameDetailFragmment when this fragment is first created
            if (getActivity() != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.fragment_placeholder, GameDetailFragment.newInstance());
                ft.commit();
            }
        }

        return rootView;
    }
}
