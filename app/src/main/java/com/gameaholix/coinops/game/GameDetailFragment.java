package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentGameDetailBinding;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Context mContext;
    private Game mGame;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGameRef;
    private DatabaseReference mUserRef;
    private FirebaseUser mUser;
    private ValueEventListener mGameListener;
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
//            mRepairLogs = new ArrayList<>();
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }
        setHasOptionsMenu(true);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mGameRef = mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGame.getId());
        mUserRef = mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentGameDetailBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_detail, container, false);

        final View rootView = bind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mGameListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gameId = dataSnapshot.getKey();

                    mGame = dataSnapshot.getValue(Game.class);
                    if (mGame == null) {
                        Log.d(TAG, "Error: Game details not found");
                    } else {
                        mGame.setId(gameId);

                        String noSelection = getString(R.string.not_available);
                        String[] typeArr = getResources().getStringArray(R.array.game_type);
                        typeArr[0] = noSelection;
                        String[] cabinetArr = getResources().getStringArray(R.array.game_cabinet);
                        cabinetArr[0] = noSelection;
                        String[] workingArr = getResources().getStringArray(R.array.game_working);
                        workingArr[0] = noSelection;
                        String[] ownershipArr =
                                getResources().getStringArray(R.array.game_ownership);
                        ownershipArr[0] = noSelection;
                        String[] conditionArr =
                                getResources().getStringArray(R.array.game_condition);
                        conditionArr[0] = noSelection;
                        String[] monitorPhospherArr =
                                getResources().getStringArray(R.array.game_monitor_phospher);
                        String[] monitorTypeArr =
                                getResources().getStringArray(R.array.game_monitor_beam);
                        String[] monitorTechArr =
                                getResources().getStringArray(R.array.game_monitor_tech);
                        String[] monitorSizeArr =
                                getResources().getStringArray(R.array.game_monitor_size);

                        if (mListener != null) {
                            mListener.onGameNameChanged(mGame.getName());
                        }

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

            // Setup Delete Button
            bind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteGame();
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
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
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

    private void deleteGame() {
        if (mUser != null) {
            //user is signed in
            final Map<String, Object> valuesToDelete = getValuesToDelete();

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle(getString(R.string.really_delete_game))
                    .setMessage(getString(R.string.game_will_be_deleted))
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteValues(valuesToDelete);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private Map<String, Object> getValuesToDelete() {
        final Map<String, Object> valuesToDelete = new HashMap<>();
        final String uid = mUser.getUid();
        final String gameId = mGame.getId();

        // Get database paths from helper class
        String gamePath = Db.getGamePath(uid) + gameId;
        String userGameListPath = Db.getGameListPath(uid) + gameId;
        String repairPath = Db.getRepairPath(uid, gameId);

        valuesToDelete.put(gamePath, null);
        valuesToDelete.put(userGameListPath, null);
        valuesToDelete.put(repairPath, null);

        ValueEventListener toDoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String toDoPath = Db.getToDoPath(uid);
                String userToDoListPath = Db.getUserToDoListPath(uid);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    valuesToDelete.put(toDoPath + key, null);
                    valuesToDelete.put(userToDoListPath + key, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mGameRef.child(Db.TODO_LIST).addListenerForSingleValueEvent(toDoListener);

        ValueEventListener shopListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String shopPath = Db.getShopPath(uid);
                String userShopListPath = Db.getUserShopListPath(uid);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    valuesToDelete.put(shopPath + key, null);
                    valuesToDelete.put(userShopListPath + key, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mGameRef.child(Db.SHOP_LIST).addListenerForSingleValueEvent(shopListener);

        return valuesToDelete;
    }

    private void deleteValues(Map<String, Object> valuesToDelete) {
        mDatabaseReference.updateChildren(valuesToDelete, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                            " Code: " + databaseError.getCode() +
                            " Details: " + databaseError.getDetails());
                }
            }
        });

        if (getActivity() != null) {
            getActivity().finish();
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
        void onGameNameChanged(String name);
    }


}
