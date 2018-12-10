package com.gameaholix.coinops.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.DialogMonitorDetailsBinding;
import com.gameaholix.coinops.databinding.FragmentGameAddBinding;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class GameAddEditFragment extends BaseDialogFragment {
    private static final String TAG = GameAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_GAME_EDIT_FLAG = "CoinOpsGameEditFlag";

    private Context mContext;
    private Game mGame;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public GameAddEditFragment() {
        // Required empty public constructor
    }

    // edit an existing Game in the database
    public static GameAddEditFragment newInstance(Game game) {
        Bundle args = new Bundle();
        GameAddEditFragment fragment = new GameAddEditFragment();
        args.putParcelable(EXTRA_GAME, game);
        args.putBoolean(EXTRA_GAME_EDIT_FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mEdit = getArguments().getBoolean(EXTRA_GAME_EDIT_FLAG);
                mGame = getArguments().getParcelable(EXTRA_GAME);
            } else {
                mEdit = false;
                mGame = new Game();
            }
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
            mEdit = savedInstanceState.getBoolean(EXTRA_GAME_EDIT_FLAG);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            if (!mEdit) getDialog().setTitle(R.string.add_game_title);
        }

        // Inflate the layout for this fragment
        final FragmentGameAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText
        if (mEdit) bind.etGameName.setText(mGame.getName());
        bind.etGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Verify input and hide keyboard if IME_ACTION_DONE
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (textInputIsValid(input)) {
                        mGame.setName(input);
                    } else {
                        textView.setText(mGame.getName());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        bind.etGameName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // Verify input if editText loses focus
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        String input = ((EditText) view).getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mGame.setName(input);
                        } else {
                            ((EditText) view).setText(mGame.getName());
                        }
                    }
                }
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameType.setAdapter(typeAdapter);
        if (mEdit) bind.spinnerGameType.setSelection(mGame.getType());
        bind.spinnerGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGame.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> cabinetAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_cabinet, android.R.layout.simple_spinner_item);
        cabinetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCabinet.setAdapter(cabinetAdapter);
        if (mEdit) bind.spinnerGameCabinet.setSelection(mGame.getCabinet());
        bind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGame.setCabinet(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> workingAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_working, android.R.layout.simple_spinner_item);
        workingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameWorking.setAdapter(workingAdapter);
        if (mEdit) bind.spinnerGameWorking.setSelection(mGame.getWorking());
        bind.spinnerGameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGame.setWorking(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_ownership, android.R.layout.simple_spinner_item);
        ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameOwnership.setAdapter(ownershipAdapter);
        if (mEdit) bind.spinnerGameOwnership.setSelection(mGame.getOwnership());
        bind.spinnerGameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGame.setOwnership(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCondition.setAdapter(conditionAdapter);
        if (mEdit) bind.spinnerGameCondition.setSelection(mGame.getCondition());
        bind.spinnerGameCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGame.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup Monitor Detais TextView
        updateMonitorDetails(mGame, bind.tvMonitorDetails);

        // Setup Monitor Details Dialog
        final AlertDialog.Builder monitorDialog = new AlertDialog.Builder(mContext);
        final DialogMonitorDetailsBinding dialogBind = DataBindingUtil.inflate(
                inflater, R.layout.dialog_monitor_details, container, false);
        monitorDialog.setTitle(R.string.select_game_monitor_details);
        final View monitorDialogView = dialogBind.getRoot();
        monitorDialog.setView(monitorDialogView);
        monitorDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // this gets called if user taps on Done button
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                updateMonitorDetails(mGame, bind.tvMonitorDetails);
                dialogInterface.dismiss();
            }
        });
        monitorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // this gets called if user taps outside of dialog
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                updateMonitorDetails(mGame, bind.tvMonitorDetails);
                dialogInterface.dismiss();
            }
        });

        // Setup NumberPickers in dialog
        dialogBind.npGameMonitorSize.setMinValue(0);
        dialogBind.npGameMonitorSize.setMaxValue(5);
        dialogBind.npGameMonitorSize.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_size));
        dialogBind.npGameMonitorSize.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorSize.setValue(mGame.getMonitorSize());
        dialogBind.npGameMonitorSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mGame.setMonitorSize(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorPhospher.setMinValue(0);
        dialogBind.npGameMonitorPhospher.setMaxValue(2);
        dialogBind.npGameMonitorPhospher.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_phospher));
        dialogBind.npGameMonitorPhospher.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorPhospher.setValue(mGame.getMonitorPhospher());
        dialogBind.npGameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mGame.setMonitorPhospher(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorBeam.setMinValue(0);
        dialogBind.npGameMonitorBeam.setMaxValue(2);
        dialogBind.npGameMonitorBeam.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_beam));
        dialogBind.npGameMonitorBeam.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorBeam.setValue(mGame.getMonitorBeam());
        dialogBind.npGameMonitorBeam.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mGame.setMonitorBeam(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorTech.setMinValue(0);
        dialogBind.npGameMonitorTech.setMaxValue(2);
        dialogBind.npGameMonitorTech.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_tech));
        dialogBind.npGameMonitorTech.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorTech.setValue(mGame.getMonitorTech());
        dialogBind.npGameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mGame.setMonitorTech(numberPicker.getValue());
            }
        });

        // Setup monitor details onClickListener
        View.OnClickListener monitorDetailsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monitorDialog.show();
            }
        };
        bind.tvMonitorDetails.setOnClickListener(monitorDetailsListener);
        bind.ibMonitorDetailsArrow.setOnClickListener(monitorDetailsListener);

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onGameAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) bind.btnSave.setText(R.string.save_changes);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verify EditText input if user taps on btnSave before onEditorAction or onFocusChange
                String input = bind.etGameName.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mGame.setName(input);
                } else {
                    bind.etGameName.setText(mGame.getName());
                }

                addEditGame();
                mListener.onGameAddEditCompletedOrCancelled();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
        outState.putBoolean(EXTRA_GAME_EDIT_FLAG, mEdit);
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

    private void updateMonitorDetails(Game game, TextView monitorDetails) {
        String[] sizeArr = getResources().getStringArray(R.array.game_monitor_size);
        String[] phospherArr = getResources().getStringArray(R.array.game_monitor_phospher);
        String[] beamArr = getResources().getStringArray(R.array.game_monitor_beam);
        String[] techArr = getResources().getStringArray(R.array.game_monitor_tech);

        if (game.getMonitorSize() == 0 && game.getMonitorPhospher() == 0 &&
                game.getMonitorBeam() == 0 && game.getMonitorTech() == 0 ){
            monitorDetails.setText(R.string.select_game_monitor);
        } else {
            String details = sizeArr[game.getMonitorSize()] + " " +
                    phospherArr[game.getMonitorPhospher()] + " " +
                    beamArr[game.getMonitorBeam()] + " " +
                    techArr[game.getMonitorTech()];
            monitorDetails.setText(details);
        }
    }

    private void addEditGame() {
        if (TextUtils.isEmpty(mGame.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_game_failed,
                    R.string.error_name_empty);
            Log.e(TAG, "Failed to add game! Name field was blank.");
            return;
        }

        if (getShowsDialog()) getDialog().dismiss();

        // add new game or update existing game to firebase
        if (mUser != null) {
            // user is signed in
            String uid = mUser.getUid();

            DatabaseReference gameRootRef = mDatabaseReference.child(Db.GAME).child(uid);


            String gameId;
            if (mEdit) {
                gameId = mGame.getId();
            } else {
                gameId = gameRootRef.push().getKey();
            }

            if (TextUtils.isEmpty(gameId)) {
                PromptUser.displayAlert(mContext,
                        R.string.error_update_database_failed,
                        R.string.error_game_id_empty);
                Log.e(TAG, "Failed to add or update database! Game ID cannot be an empty string.");
                return;
            }

            DatabaseReference gameRef = gameRootRef.child(gameId);
            DatabaseReference userGameListRef = mDatabaseReference
                    .child(Db.USER)
                    .child(uid)
                    .child(Db.GAME_LIST)
                    .child(gameId);

            // convert mGame instance to Map so it can be iterated
            Map<String, Object> currentValues = mGame.getMap();

            // create new Map with full database paths as keys using values from mGame Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(gameRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Db.NAME)) {
                    valuesWithPath.put(userGameListRef.getPath().toString(), currentValues.get(key));
                }
            }

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });
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
        void onGameAddEditCompletedOrCancelled();
    }
}
