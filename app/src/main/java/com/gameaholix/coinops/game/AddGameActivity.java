package com.gameaholix.coinops.game;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class AddGameActivity extends AppCompatActivity implements
        AddGameFragment.OnFragmentInteractionListener{

    private static final String TAG = AddGameActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.add_game_title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
