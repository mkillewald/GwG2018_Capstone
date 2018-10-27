package com.gameaholix.coinops.game;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Game;

public class EditGameActivity extends AppCompatActivity {
//    private static final String TAG = EditGameActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        setTitle(R.string.edit_game_title);

        EditGameFragment fragment = EditGameFragment.newInstance(mGame);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_GAME, mGame);
    }

    // Hide keyboard after touch event occurs outside of EditText
    // Solution used from:
    // https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if ( view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}

