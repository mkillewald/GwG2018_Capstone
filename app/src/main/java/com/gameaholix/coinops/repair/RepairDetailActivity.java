package com.gameaholix.coinops.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.step.AddStepActivity;

public class RepairDetailActivity extends AppCompatActivity implements
        RepairDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = RepairDetailActivity.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.repair.RepairLog";
    private static final String EXTRA_REPAIR_ID = "CoinOpsRepairLogId";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private RepairLog mRepairLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mRepairLog = getIntent().getParcelableExtra(EXTRA_REPAIR);
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        setTitle(R.string.repair_details_title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
    }

    @Override
    public void onAddStepButtonPressed(String gameId, String logId) {
        Intent intent = new Intent(RepairDetailActivity.this, AddStepActivity.class);
        intent.putExtra(EXTRA_REPAIR_ID, logId);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        startActivity(intent);
    }
}
