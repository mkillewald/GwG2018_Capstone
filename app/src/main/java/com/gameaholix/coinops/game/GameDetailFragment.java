package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.RepairAdapter;
import com.gameaholix.coinops.databinding.FragmentGameDetailBinding;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.model.RepairLog;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameDetailFragment extends Fragment implements
        RepairAdapter.RepairAdapterOnClickHandler{
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_REPAIR_LIST = "CoinOpsRepairLogList";

    private Context mContext;
    private Game mGame;
    private ArrayList<RepairLog> mRepairLogs;
    private RepairAdapter mRepairAdapter;
    private DatabaseReference mGameRef;
    private DatabaseReference mRrepairListRef;
    private FirebaseUser mUser;
    private ValueEventListener mGameListener;
    private ValueEventListener mRepairListener;
    private OnFragmentInteractionListener mListener;

    public GameDetailFragment() {
        // Required empty public constructor
    }

    public static GameDetailFragment newInstance(Game game) {
        Bundle args = new Bundle();
        GameDetailFragment fragment = new GameDetailFragment();
        args.putParcelable(EXTRA_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGame = getArguments().getParcelable(EXTRA_GAME);
            }
            mRepairLogs = new ArrayList<>();
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
            mRepairLogs = savedInstanceState.getParcelableArrayList(EXTRA_REPAIR_LIST);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentGameDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_detail, container, false);

        final View rootView = bind.getRoot();



        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // get current user
        mUser = firebaseAuth.getCurrentUser();

        // Setup database references
        mGameRef = databaseReference
                .child(Db.GAME).child(mUser.getUid()).child(mGame.getGameId());
        mRrepairListRef = mGameRef.child(Db.REPAIR_LIST);

        // Setup Repair Log RecyclerView
        final RecyclerView recyclerView = rootView.findViewById(R.id.rv_repair_list);
        mRepairAdapter = new RepairAdapter( this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRepairAdapter);
        mRepairAdapter.setRepairLogs(mRepairLogs);

        if (mUser != null) {
            // user is signed in

            // Set up event listeners
            mGameListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gameId = dataSnapshot.getKey();

                    mGame = dataSnapshot.getValue(Game.class);
                    if (mGame == null) {
                        Log.d(TAG, "Error: Game details not found");
                    } else {
                        mGame.setGameId(gameId);
                        String[] typeArr = getResources().getStringArray(R.array.game_type);
                        String[] cabinetArr = getResources().getStringArray(R.array.game_cabinet);
                        String[] workingArr = getResources().getStringArray(R.array.game_working);
                        String[] ownershipArr =
                                getResources().getStringArray(R.array.game_ownership);
                        String[] conditionArr =
                                getResources().getStringArray(R.array.game_condition);
                        String[] monitorPhospherArr =
                                getResources().getStringArray(R.array.game_monitor_phospher);
                        String[] monitorTypeArr =
                                getResources().getStringArray(R.array.game_monitor_beam);
                        String[] monitorTechArr =
                                getResources().getStringArray(R.array.game_monitor_tech);
                        String[] monitorSizeArr =
                                getResources().getStringArray(R.array.game_monitor_size);

                        bind.tvGameName.setText(mGame.getName());
                        bind.tvGameType.setText(typeArr[mGame.getType()]);
                        bind.tvGameCabinet.setText(cabinetArr[mGame.getCabinet()]);
                        bind.tvGameWorking.setText(workingArr[mGame.getWorking()]);
                        bind.tvGameOwnership.setText(ownershipArr[mGame.getOwnership()]);
                        bind.tvGameCondition.setText(conditionArr[mGame.getCondition()]);
                        bind.tvGameMonitorPhospher
                                .setText(monitorPhospherArr[mGame.getMonitorPhospher()]);
                        bind.tvGameMonitorType.setText(monitorTypeArr[mGame.getMonitorBeam()]);
                        bind.tvGameMonitorTech.setText(monitorTechArr[mGame.getMonitorTech()]);
                        bind.tvGameMonitorSize.setText(monitorSizeArr[mGame.getMonitorSize()]);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            // read list of games
            mGameRef.addValueEventListener(mGameListener);

            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mRepairLogs.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String logId = dataSnapshot1.getKey();
                        String description = (String) dataSnapshot1.getValue();
                        RepairLog repairLog = new RepairLog(logId, mGame.getGameId(), description);
                        mRepairLogs.add(repairLog);
                    }
                    mRepairAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            // read list of repair logs
            mRrepairListRef.addValueEventListener(mRepairListener);

            // Setup Buttons
            bind.btnAddRepair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddRepairButtonPressed(mGame.getGameId());
                    }
                }
            });

            bind.btnAddTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddTodoButtonPressed(mGame.getGameId());
                    }
                }
            });

            bind.btnAddShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddShoppingButtonPressed(mGame.getGameId());
                    }
                }
            });

            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDeleteButtonPressed(mGame.getGameId());
                    }
                }
            });
//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUser != null) {
            mGameRef.removeEventListener(mGameListener);
            mRrepairListRef.removeEventListener(mRepairListener);
        }
    }

    @Override
    public void onClick(RepairLog repairLog) {
        if (mListener != null) {
            mListener.onLogSelected(repairLog);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
        outState.putParcelableArrayList(EXTRA_REPAIR_LIST, mRepairLogs);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_game:
                Intent intent = new Intent(getContext(), EditGameActivity.class);
                intent.putExtra(EXTRA_GAME, mGame);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onDeleteButtonPressed(String gameId);
        void onAddTodoButtonPressed(String gameId);
        void onAddShoppingButtonPressed(String gameId);
        void onAddRepairButtonPressed(String gameId);
        void onLogSelected(RepairLog repairLog);
    }


}
