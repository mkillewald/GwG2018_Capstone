package com.gameaholix.coinops.todo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ToDoItem;

public class ToDoDetailActivity extends AppCompatActivity implements
        ToDoDetailFragment.OnFragmentInteractionListener,
        ToDoEditFragment.OnFragmentInteractionListener {

    private static final String TAG = ToDoDetailActivity.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";

    private ToDoItem mToDoItem;
    private String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mToDoItem = getIntent().getParcelableExtra(EXTRA_TODO);
            mGameName = getIntent().getStringExtra(EXTRA_GAME_NAME);
        } else {
            mToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
        }

        if (!TextUtils.isEmpty(mGameName)) {
            setTitle(mGameName);
        } else {
            setTitle(R.string.to_do_details_title);
        }

        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(mToDoItem);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ToDoEditFragment) {
            menu.findItem(R.id.menu_edit_todo).setVisible(false);
        } else {
            menu.findItem(R.id.menu_edit_todo).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_todo:
                // handled by ToDoDetailFragment
                return false;
            case R.id.menu_delete_todo:
                // handled by ToDoDetailFragment
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_TODO, mToDoItem);
        outState.putString(EXTRA_GAME_NAME, mGameName);
    }

    @Override
    public void onEditButtonPressed(ToDoItem toDoItem) {
        // replace ToDoDetailFragment with ToDoEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoEditFragment.newInstance(toDoItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace ToDoEditFragment with ToDoDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoDetailFragment.newInstance(mToDoItem));
        ft.commit();

        invalidateOptionsMenu();
    }
}
