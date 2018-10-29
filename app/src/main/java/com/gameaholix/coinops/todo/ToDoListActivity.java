package com.gameaholix.coinops.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ToDoItem;

public class ToDoListActivity extends AppCompatActivity implements
        ToDoListFragment.OnFragmentInteractionListener {
    private static final String TAG = ToDoListActivity.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.to_do_list_title);

        ToDoListFragment fragment = ToDoListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onToDoItemSelected(ToDoItem toDoItem) {
//        Intent intent = new Intent(this, ToDoDetailActivity.class);
//        intent.putExtra(EXTRA_TODO, toDoItem);
//        startActivity(intent);
    }
}
