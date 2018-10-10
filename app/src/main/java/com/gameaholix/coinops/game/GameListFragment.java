package com.gameaholix.coinops.game;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameListFragment extends Fragment implements GameAdapter.GameAdapterOnClickHandler {
    private static final String TAG = GameListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_LIST = "CoinOpsGameList";

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

//    private String mUsername;

    private GameAdapter mGameAdapter;
    private ArrayList<Game> mGames;

    public GameListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mGames = new ArrayList<Game>();
        } else {
            mGames = savedInstanceState.getParcelableArrayList(EXTRA_GAME_LIST);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_game_list, container,
                false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_game_list);
        mGameAdapter = new GameAdapter(getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mGameAdapter);
        mGameAdapter.setGames(mGames);

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
//            mUsername = user.getDisplayName();
            final String uid = user.getUid();

            // Setup database references
            final DatabaseReference gameRef =
                    mDatabaseReference.child(getString(R.string.db_game)).child(uid);
            final DatabaseReference userRef =
                    mDatabaseReference.child(getString(R.string.db_user)).child(uid);
            final DatabaseReference userGameListRef =
                    userRef.child(getString(R.string.db_game_list));

            // add a game
//                DatabaseReference gameIdRef = gameRef.push();
//                final String gameId = gameIdRef.getKey();
//                final DatabaseReference gameTodoListRef = gameIdRef.child("todo_list");
//                final DatabaseReference gameShopListRef = gameIdRef.child("shop_list");
//                final DatabaseReference gameRepairListRef = gameIdRef.child("repair_list");
//
//                    final Game newGame = new Game("Pole Position");
//                    gameIdRef.setValue(newGame, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            String gameId = databaseReference.getKey();
//                            userGameListRef.child(gameId).child("name").setValue(newGame.getName());
//                       }
//                    });

            // read list of games
            ValueEventListener gameListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mGames.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String gameId = dataSnapshot1.getKey();
                        Game game =  dataSnapshot1.getValue(Game.class);
                        game.setGameId(gameId);
                        mGames.add(game);
                    }
                    mGameAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };

            userGameListRef.addValueEventListener(gameListener);


        } else {
            // user is not signed in
//            mUsername = getString(R.string.anonymous_username);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_GAME_LIST, mGames);
    }

    @Override
    public void onClick(Game game) {
        if (mListener != null) {
            mListener.onGameSelected(game);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        void onGameSelected(Game game);
    }
}
