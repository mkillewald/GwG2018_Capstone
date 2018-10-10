package com.gameaholix.coinops.todo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class ToDoDetailActivity extends AppCompatActivity implements
        ToDoDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = ToDoDetailActivity.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.todo.ToDoItem";

    private ToDoItem mToDoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mToDoItem = getIntent().getParcelableExtra(EXTRA_TODO);
        } else {
            mToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
        }

        setTitle(mToDoItem.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_TODO, mToDoItem);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
