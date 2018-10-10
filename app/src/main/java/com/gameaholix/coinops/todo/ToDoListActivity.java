package com.gameaholix.coinops.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class ToDoListActivity extends AppCompatActivity implements
        ToDoListFragment.OnFragmentInteractionListener {

    private static final String TAG = ToDoListActivity.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.todo.ToDoItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.to_do_list_title);
    }

    @Override
    public void onToDoItemSelected(ToDoItem toDo) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO, toDo);
        startActivity(intent);
    }
}
