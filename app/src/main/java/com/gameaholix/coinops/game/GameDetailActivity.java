package com.gameaholix.coinops.game;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class GameDetailActivity extends AppCompatActivity implements
        GameDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.game.Game";

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        setTitle(mGame.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
