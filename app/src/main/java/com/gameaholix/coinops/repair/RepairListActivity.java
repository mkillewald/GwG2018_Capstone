package com.gameaholix.coinops.repair;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class RepairListActivity extends AppCompatActivity implements
        RepairListFragment.OnFragmentInteractionListener {

    private static final String TAG = RepairListActivity.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.repair.RepairLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        setTitle(R.string.);
    }

    @Override
    public void onRepairLogSelected(RepairLog repairLog) {
        Intent intent = new Intent(this, RepairDetailActivity.class);
        intent.putExtra(EXTRA_REPAIR, repairLog);
        startActivity(intent);
    }
}
