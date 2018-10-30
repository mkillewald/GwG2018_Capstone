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

public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Context mContext;
    private Game mGame;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGameRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mShopRef;
    private DatabaseReference mToDoRef;
    private FirebaseUser mUser;
    private ValueEventListener mGameListener;
    private ValueEventListener mDeleteTodoListener;
    private ValueEventListener mDeleteShopListener;
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
                    showDeleteAlert();
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

        if (mToDoRef != null) {
            mToDoRef.removeEventListener(mDeleteTodoListener);
        }

        if (mShopRef != null) {
            mShopRef.removeEventListener(mDeleteShopListener);
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

    private void showDeleteAlert() {
        if (mUser != null) {
            //user is signed in

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
                            deleteAllGameData();
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteAllGameData() {
        mDeleteTodoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    dataSnapshot1.getRef().removeValue();
                    mUserRef.child(Db.TODO_LIST).child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDeleteShopListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    dataSnapshot1.getRef().removeValue();
                    mUserRef.child(Db.SHOP_LIST).child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // delete game details
        mGameRef.removeValue();

        // remove user game_list entry
        mUserRef.child(Db.GAME_LIST)
                .child(mGame.getId())
                .removeValue();

        // delete repair logs
        mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGame.getId())
                .removeValue();

        // delete to do items
        mToDoRef = mDatabaseReference
                .child(Db.TODO)
                .child(mUser.getUid());

        mToDoRef.orderByChild(Db.PARENT_ID)
                .equalTo(mGame.getId())
                .addValueEventListener(mDeleteTodoListener);

        // delete shopping list items
        mShopRef = mDatabaseReference
                .child(Db.SHOP)
                .child(mUser.getUid());

        mShopRef.orderByChild(Db.PARENT_ID)
                .equalTo(mGame.getId())
                .addValueEventListener(mDeleteShopListener);
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
