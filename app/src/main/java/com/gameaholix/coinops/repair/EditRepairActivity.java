package com.gameaholix.coinops.repair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.RepairLog;

// TODO: finish this

public class EditRepairActivity extends AppCompatActivity {
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.RepairLog";

    private RepairLog mRepairLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mRepairLog = getIntent().getParcelableExtra(EXTRA_REPAIR);
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        setTitle(R.string.edit_repair_title);

        EditRepairFragment fragment = EditRepairFragment.newInstance(mRepairLog);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
    }
}
