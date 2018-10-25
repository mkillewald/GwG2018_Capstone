package com.gameaholix.coinops.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.DialogMonitorDetailsBinding;
import com.gameaholix.coinops.databinding.FragmentAddGameBinding;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

// TODO: combine AddGame.. and EditGame.. to one activity/fragment

public class EditGameFragment extends Fragment {
//    private static final String TAG = EditGameFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_VALUES = "CoinOpsGameValuesToUpdate";

    private Context mContext;
    private Game mGame;
    private Bundle mValuesBundle;
    private OnFragmentInteractionListener mListener;

    public EditGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddGameBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            if (getActivity() != null && getActivity().getIntent() != null) {
                Intent intent = getActivity().getIntent();
                mGame = intent.getParcelableExtra(EXTRA_GAME);
            }
            mValuesBundle = new Bundle();
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
            mValuesBundle = savedInstanceState.getBundle(EXTRA_VALUES);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();
            final String gameId = mGame.getGameId();

            // Setup EditText
            bind.etGameName.setText(mGame.getName());
            bind.etGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        String input = textView.getText().toString().trim();
                        if (textInputIsValid(input)) {
                            mValuesBundle.putString(Db.NAME, input);
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
                    if (view.getId() == R.id.et_game_name && !hasFocus) {
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            String input = editText.getText().toString().trim();
                            if (textInputIsValid(input)) {
                                mValuesBundle.putString(Db.NAME, input);
                            } else {
                                editText.setText(mGame.getName());
                            }
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
            bind.spinnerGameType.setSelection(mGame.getType());
            bind.spinnerGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.TYPE, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            ArrayAdapter<CharSequence> cabinetAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.game_cabinet, android.R.layout.simple_spinner_item);
            cabinetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerGameCabinet.setAdapter(cabinetAdapter);
            bind.spinnerGameCabinet.setSelection(mGame.getCabinet());
            bind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.CABINET, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            ArrayAdapter<CharSequence> workingAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.game_working, android.R.layout.simple_spinner_item);
            workingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerGameWorking.setAdapter(workingAdapter);
            bind.spinnerGameWorking.setSelection(mGame.getWorking());
            bind.spinnerGameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.WORKING, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.game_ownership, android.R.layout.simple_spinner_item);
            ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerGameOwnership.setAdapter(ownershipAdapter);
            bind.spinnerGameOwnership.setSelection(mGame.getOwnership());
            bind.spinnerGameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.OWNERSHIP, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                    mContext, R.array.game_condition, android.R.layout.simple_spinner_item);
            conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bind.spinnerGameCondition.setAdapter(conditionAdapter);
            bind.spinnerGameCondition.setSelection(mGame.getCondition());
            bind.spinnerGameCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mValuesBundle.putInt(Db.CONDITION, position);
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
                    ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                    updateMonitorDetails(mGame, bind.tvMonitorDetails);
                    dialogInterface.dismiss();
                }
            });
            monitorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                    updateMonitorDetails(mGame, bind.tvMonitorDetails);
                    dialogInterface.dismiss();
                }
            });
            updateMonitorDetails(mGame, bind.tvMonitorDetails);

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
                    mValuesBundle.putInt(Db.MONITOR_SIZE, numberPicker.getValue());                }
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
                    mValuesBundle.putInt(Db.MONITOR_PHOSPHER, numberPicker.getValue());
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
                    mValuesBundle.putInt(Db.MONITOR_BEAM, numberPicker.getValue());
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
                    mValuesBundle.putInt(Db.MONITOR_TECH, numberPicker.getValue());
                }
            });

            View.OnClickListener monitorDetailsListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monitorDialog.show();
                }
            };
            bind.tvMonitorDetails.setOnClickListener(monitorDetailsListener);
            bind.ibMonitorDetailsArrow.setOnClickListener(monitorDetailsListener);

            // Setup Button
            bind.btnSave.setText(R.string.save_changes);
            bind.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {

                        // Get database paths from helper class
                        String gamePath = Db.getGamePath(uid, gameId);
                        String userGamePath = Db.getGameListPath(uid, gameId);

                        // Convert values Bundle to HashMap for Firebase call to updateChildren()
                        Map<String, Object> valuesMap = new HashMap<>();

                        for (String key : Db.GAME_STRINGS) {
                            if (mValuesBundle.containsKey(key)) {
                                valuesMap.put(gamePath + key, mValuesBundle.getString(key));
                                if (key.equals(Db.NAME)) {
                                    valuesMap.put(userGamePath + key, mValuesBundle.getString(key));
                                }
                            }
                        }

                        for (String key : Db.GAME_INTS) {
                            if (mValuesBundle.containsKey(key)) {
                                valuesMap.put(gamePath + key, mValuesBundle.getInt(key));
                            }
                        }

                        mListener.onEditGameButtonPressed(valuesMap);
                    }
                }
            });

//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    private boolean textInputIsValid(String inputText) {
        boolean result = true;

        // TODO: possibly add more validation checks, and return false if any one of them fails.
        if (TextUtils.isEmpty(inputText)) {
            result = false;
        }

        return result;
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
        outState.putBundle(EXTRA_VALUES, mValuesBundle);
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
        void onEditGameButtonPressed(Map<String, Object> valuesToUpdate);
    }
}
