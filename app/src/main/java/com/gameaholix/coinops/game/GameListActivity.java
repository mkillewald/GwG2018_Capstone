package com.gameaholix.coinops.game;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Game;

public class GameListActivity extends AppCompatActivity implements
        GameListFragment.OnFragmentInteractionListener {
//    private static final String TAG = GameListActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.game_list_title);

        if (savedInstanceState == null) {
            GameListFragment fragment = new GameListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_game:
                showAddGameDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGameSelected(Game game) {
        Intent intent = new Intent(this, GameDetailActivity.class);
        intent.putExtra(EXTRA_GAME, game);
        startActivity(intent);
    }

    private void showAddGameDialog() {
        FragmentManager fm = getSupportFragmentManager();
        GameAddFragment fragment = new GameAddFragment();
        fragment.show(fm, "fragment_game_add");
    }

}
