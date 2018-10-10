package com.gameaholix.coinops.game;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class GameListActivity extends AppCompatActivity implements
        GameListFragment.OnFragmentInteractionListener {

    private static final String TAG = GameListActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.game.Game";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.game_list_title);
    }

    @Override
    public void onGameSelected(Game game) {
        Intent intent = new Intent(this, GameDetailActivity.class);
        intent.putExtra(EXTRA_GAME, game);
        startActivity(intent);
    }
}
