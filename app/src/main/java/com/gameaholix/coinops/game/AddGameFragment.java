package com.gameaholix.coinops.game;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddGameBinding;
import com.gameaholix.coinops.databinding.DialogMonitorDetailsBinding;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddGameFragment extends DialogFragment {
    private static final String TAG = AddGameFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Context mContext;
    private Game mNewGame;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public AddGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mNewGame = new Game();
        } else {
            mNewGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddGameBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = bind.getRoot();

        // Setup EditText
        bind.etGameName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        hideKeyboard(editText);
                    }
                }
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameType.setAdapter(typeAdapter);
        bind.spinnerGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> cabinetAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_cabinet, android.R.layout.simple_spinner_item);
        cabinetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCabinet.setAdapter(cabinetAdapter);
        bind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setCabinet(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> workingAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_working, android.R.layout.simple_spinner_item);
        workingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameWorking.setAdapter(workingAdapter);
        bind.spinnerGameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setWorking(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_ownership, android.R.layout.simple_spinner_item);
        ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameOwnership.setAdapter(ownershipAdapter);
        bind.spinnerGameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setOwnership(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCondition.setAdapter(conditionAdapter);
        bind.spinnerGameCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

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
                updateMonitorDetails(mNewGame, bind.tvMonitorDetails);
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                dialogInterface.dismiss();
            }
        });
        monitorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                updateMonitorDetails(mNewGame, bind.tvMonitorDetails);
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                dialogInterface.dismiss();
            }
        });

        // Setup NumberPickers in dialog
        dialogBind.npGameMonitorSize.setMinValue(0);
        dialogBind.npGameMonitorSize.setMaxValue(5);
        dialogBind.npGameMonitorSize.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_size));
        dialogBind.npGameMonitorSize.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorSize.setValue(mNewGame.getMonitorSize());
        dialogBind.npGameMonitorSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorSize(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorPhospher.setMinValue(0);
        dialogBind.npGameMonitorPhospher.setMaxValue(2);
        dialogBind.npGameMonitorPhospher.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_phospher));
        dialogBind.npGameMonitorPhospher.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorPhospher.setValue(mNewGame.getMonitorPhospher());
        dialogBind.npGameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorPhospher(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorBeam.setMinValue(0);
        dialogBind.npGameMonitorBeam.setMaxValue(2);
        dialogBind.npGameMonitorBeam.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_beam));
        dialogBind.npGameMonitorBeam.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorBeam.setValue(mNewGame.getMonitorBeam());
        dialogBind.npGameMonitorBeam.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorBeam(numberPicker.getValue());
            }
        });

        dialogBind.npGameMonitorTech.setMinValue(0);
        dialogBind.npGameMonitorTech.setMaxValue(2);
        dialogBind.npGameMonitorTech.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_tech));
        dialogBind.npGameMonitorTech.setWrapSelectorWheel(false);
        dialogBind.npGameMonitorTech.setValue(mNewGame.getMonitorTech());
        dialogBind.npGameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorTech(numberPicker.getValue());
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
                getDialog().dismiss();
            }
        });

        bind.btnDelete.setVisibility(View.GONE);

        bind.btnSave.setText(R.string.add_game);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text from EditText
                mNewGame.setName(bind.etGameName.getText().toString().trim());
                addGame(mNewGame);
            }
        });

        return rootView;
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mNewGame);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set width and height of this DialogFragment, code block used from
        // https://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    private void addGame(Game game) {
        if (TextUtils.isEmpty(game.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_game_failed,
                    R.string.error_name_empty);
            return;
        }

        getDialog().dismiss();

        // TODO: add checks for if game name already exists.

        // Add Game object to Firebase
        if (mUser != null) {
            // user is signed in
            final String uid = mUser.getUid();

            final DatabaseReference gameRef = mDatabaseReference.child(Db.GAME).child(uid);

            final String gameId = gameRef.push().getKey();

            // Get database paths from helper class
            String gamePath = Db.getGamePath(uid) + gameId;
            String userGameListPath = Db.getGameListPath(uid) + gameId;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(gamePath, game);
            valuesToAdd.put(userGameListPath, game.getName());

            mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
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
}
