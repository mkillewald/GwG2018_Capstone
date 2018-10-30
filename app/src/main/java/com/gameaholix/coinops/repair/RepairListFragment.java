package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.RepairAdapter;
import com.gameaholix.coinops.databinding.FragmentListWithButtonBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepairListFragment extends Fragment implements RepairAdapter.RepairAdapterOnClickHandler {
    private static final String TAG = RepairListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_REPAIR_LIST = "CoinOpsRepairList";

    private Context mContext;
    private String mGameId;
    private ArrayList<Item> mRepairLogs;
    private RepairAdapter mRepairAdapter;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mRepairRef;
    private DatabaseReference mRepairListRef;
    private FirebaseUser mUser;
    private ValueEventListener mRepairListener;
    private FragmentListWithButtonBinding mBind;
    private OnFragmentInteractionListener mListener;

    public RepairListFragment() {
        // Required empty public constructor
    }

    public static RepairListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        RepairListFragment fragment = new RepairListFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            }
            mRepairLogs = new ArrayList<>();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mRepairLogs = savedInstanceState.getParcelableArrayList(EXTRA_REPAIR_LIST);
        }
//        setHasOptionsMenu(true);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mRepairRef = mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid());
        mRepairListRef = mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGameId)
                .child(Db.REPAIR_LIST);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list_with_button, container, false);
        final View rootView = mBind.getRoot();

        // Setup Repair Log RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        mRepairAdapter = new RepairAdapter( this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRepairAdapter);
        mRepairAdapter.setRepairLogs(mRepairLogs);

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mRepairListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mRepairLogs.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String logId = dataSnapshot1.getKey();
                        String name = (String) dataSnapshot1.getValue();
                        Item repairLog = new Item(logId, mGameId, name);
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
            mRepairListRef.addValueEventListener(mRepairListener);

            //Setup EditText
            mBind.etEntry.setHint(R.string.repair_entry_hint);
            mBind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView);
                    }
                    return false;
                }
            });
//            mBind.etRepairLogDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean hasFocus) {
//                    if (view.getId() == R.id.et_add_game_name && !hasFocus) {
//                        if (view instanceof EditText) {
//                            EditText editText = (EditText) view;
//                            hideKeyboard(editText);
//                        }
//                    }
//                }
//            });

            // Setup Button
            Button addLogButton = mBind.btnSave;
            addLogButton.setText(R.string.add_repair_log);
            addLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item newLog = new Item();
                    newLog.setName(mBind.etEntry.getText().toString().trim());
                    addLog(newLog);
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
            mRepairListRef.removeEventListener(mRepairListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_REPAIR_LIST, mRepairLogs);
        outState.putString(EXTRA_GAME_ID, mGameId);
    }

    @Override
    public void onClick(Item repairLog) {
        if (mListener != null) {
            mListener.onRepairLogSelected(repairLog);
        }
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
//            case R.id.menu_add_repair:
//                Intent intent = new Intent(getContext(), AddRepairActivity.class);
//                intent.putExtra(EXTRA_GAME_ID, mGameId);
//                startActivity(intent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addLog(Item log) {
        if (TextUtils.isEmpty(log.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_repair_log_failed,
                    R.string.error_repair_log_description_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();
            String logId = mRepairRef.push().getKey();

            // Get database paths from helper class
            String repairPath = Db.getRepairPath(uid, mGameId) +  logId;
            String userRepairPath = Db.getRepairListPath(uid, mGameId) + logId;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(repairPath, log);
            valuesToAdd.put(userRepairPath, log.getName());

            mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            hideKeyboard(mBind.etEntry);
            mBind.etEntry.setText(null);
            mBind.etEntry.clearFocus();
//        } else {
//            // user is not signed in
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
        void onRepairLogSelected(Item repairLog);
    }
}
