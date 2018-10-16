package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.HintSpinnerAdapter;
import com.gameaholix.coinops.databinding.FragmentAddGameBinding;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class EditGameFragment extends Fragment {
    private static final String TAG = EditGameFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.game.Game";
    private static final String EXTRA_VALUES = "CoinOpsValuesToUpdate";

    private Context mContext;
    private Game mGame;
    private String mNewName;
    private Bundle mValuesBundle;
    private FirebaseAuth mFirebaseAuth;
    private OnFragmentInteractionListener mListener;

    public EditGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddGameBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();

            if (intent != null) {
                mGame = intent.getParcelableExtra(EXTRA_GAME);
            }

            mValuesBundle = new Bundle();
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
            mValuesBundle = savedInstanceState.getBundle(EXTRA_VALUES);
        }

        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();
            final String gameId = mGame.getGameId();

            // Setup TextView array for game condition labels
            final TextView gameConditionOff = bind.gameConditionLabels.tvGameConditionOff;
            final TextView gameCondition1 = bind.gameConditionLabels.tvGameCondition1;
            final TextView gameCondition2 = bind.gameConditionLabels.tvGameCondition2;
            final TextView gameCondition3 = bind.gameConditionLabels.tvGameCondition3;
            final TextView gameCondition4 = bind.gameConditionLabels.tvGameCondition4;
            final TextView gameCondition5 = bind.gameConditionLabels.tvGameCondition5;
            final TextView gameCondition6 = bind.gameConditionLabels.tvGameCondition6;
            final TextView gameCondition7 = bind.gameConditionLabels.tvGameCondition7;
            final TextView gameCondition8 = bind.gameConditionLabels.tvGameCondition8;
            final TextView gameCondition9 = bind.gameConditionLabels.tvGameCondition9;
            final TextView gameCondition10 = bind.gameConditionLabels.tvGameCondition10;
            final TextView gameCondition11 = bind.gameConditionLabels.tvGameCondition11;
            final TextView[] gameConditionLabels = {gameConditionOff, gameCondition1, gameCondition2,
                    gameCondition3, gameCondition4, gameCondition5, gameCondition6, gameCondition7,
                    gameCondition8, gameCondition9, gameCondition10, gameCondition11};

            // Setup EditText
            bind.etAddGameName.setText(mGame.getName());
            // TODO are both Listeners needed??
            bind.etAddGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {

                        // save text input
                        mNewName = textView.getText().toString().trim();

                        if (TextUtils.isEmpty(mNewName)) {
                            textView.setText(mGame.getName());
                        } else {
                            mValuesBundle.putString(Db.NAME, mNewName);
                        }

                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) textView
                                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
            bind.etAddGameName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view.getId() == R.id.et_add_game_name && !hasFocus) {

                        // save text input
                        if (view instanceof EditText) {
                            EditText editText = (EditText) view;
                            mNewName = editText.getText().toString().trim();

                            if (TextUtils.isEmpty(mNewName)) {
                                editText.setText(mGame.getName());
                            } else {
                                mValuesBundle.putString(Db.NAME, mNewName);
                            }
                        }

                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) view
                                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });

            // Setup Spinners
            final HintSpinnerAdapter typeAdapter = new HintSpinnerAdapter(mContext,
                    getResources().getStringArray(R.array.game_type));
            bind.spinnerGameType.setAdapter(typeAdapter);
            if (mGame.getType() > 0) {
                bind.spinnerGameType.setSelection(mGame.getType());
            }
            bind.spinnerGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    // First item is disabled and used for hint
                    if (position > 0) {
                        mValuesBundle.putInt(Db.TYPE, position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            final HintSpinnerAdapter cabinetAdapter = new HintSpinnerAdapter(mContext,
                    getResources().getStringArray(R.array.game_cabinet));
            bind.spinnerGameCabinet.setAdapter(cabinetAdapter);
            if (mGame.getCabinet() > 0) {
                bind.spinnerGameCabinet.setSelection(mGame.getCabinet());
            }
            bind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    // First item is disabled and used for hint
                    if (position > 0) {
                        mValuesBundle.putInt(Db.CABINET, position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            final HintSpinnerAdapter workingAdapter = new HintSpinnerAdapter(
                    mContext, getResources().getStringArray(R.array.game_working));
            bind.spinnerGameWorking.setAdapter(workingAdapter);
            if (mGame.getWorking() > 0) {
                bind.spinnerGameWorking.setSelection(mGame.getWorking());
            }
            bind.spinnerGameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    // First item is disabled and used for hint
                    if (position > 0) {
                        mValuesBundle.putInt(Db.WORKING, position);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            final HintSpinnerAdapter ownershipAdapter = new HintSpinnerAdapter(
                    mContext, getResources().getStringArray(R.array.game_ownership));
            bind.spinnerGameOwnership.setAdapter(ownershipAdapter);
            if (mGame.getOwnership() > 0) {
                bind.spinnerGameOwnership.setSelection(mGame.getOwnership());
            }
            bind.spinnerGameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    // First item is disabled and used for hint
                    if (position > 0) {
                        mValuesBundle.putInt(Db.OWNERSHIP, position);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            // Setup SeekBar
            bind.sbGameCondition.setMax(11);
            if (mGame.getCondition() > 0) {
                bind.sbGameCondition.setProgress(mGame.getCondition());
                gameConditionLabels[mGame.getCondition()].setTextColor(Color.BLACK);
            }
            bind.sbGameCondition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (progress >= 0 && progress <= seekBar.getMax()) {

                            for (TextView textView : gameConditionLabels) {
                                textView.setTextColor(Color.GRAY);
                            }

                            TextView textViewAtPosition = gameConditionLabels[progress];
                            textViewAtPosition.setTextColor(Color.BLACK);

                            mValuesBundle.putInt(Db.CONDITION, progress);
                            seekBar.setSecondaryProgress(progress);
                        }
                    }

                }
            });

            // Setup NumberPickers
            bind.npGameMonitorSize.setMinValue(0);
            bind.npGameMonitorSize.setMaxValue(5);
            bind.npGameMonitorSize.setDisplayedValues(
                    getResources().getStringArray(R.array.game_monitor_size));
            bind.npGameMonitorSize.setWrapSelectorWheel(false);
            if (mGame.getMonitorSize() > 0) {
                bind.npGameMonitorSize.setValue(mGame.getMonitorSize());
            }
            bind.npGameMonitorSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mValuesBundle.putInt(Db.MONITOR_SIZE, numberPicker.getValue());
                }
            });

            bind.npGameMonitorPhospher.setMinValue(0);
            bind.npGameMonitorPhospher.setMaxValue(2);
            bind.npGameMonitorPhospher.setDisplayedValues(
                    getResources().getStringArray(R.array.game_monitor_phospher));
            bind.npGameMonitorPhospher.setWrapSelectorWheel(false);
            if (mGame.getMonitorPhospher() > 0) {
                bind.npGameMonitorPhospher.setValue(mGame.getMonitorPhospher());
            }
            bind.npGameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mValuesBundle.putInt(Db.MONITOR_PHOSPHER, numberPicker.getValue());
                }
            });

            bind.npGameMonitorTech.setMinValue(0);
            bind.npGameMonitorTech.setMaxValue(2);
            bind.npGameMonitorTech.setDisplayedValues(
                    getResources().getStringArray(R.array.game_monitor_tech));
            bind.npGameMonitorTech.setWrapSelectorWheel(false);
            if (mGame.getMonitorTech() > 0) {
                bind.npGameMonitorTech.setValue(mGame.getMonitorTech());
            }
            bind.npGameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mValuesBundle.putInt(Db.MONITOR_TECH, numberPicker.getValue());
                }
            });

            bind.npGameMonitorType.setMinValue(0);
            bind.npGameMonitorType.setMaxValue(2);
            bind.npGameMonitorType.setDisplayedValues(
                    getResources().getStringArray(R.array.game_monitor_type));
            bind.npGameMonitorType.setWrapSelectorWheel(false);
            if (mGame.getMonitorType() > 0) {
                bind.npGameMonitorType.setValue(mGame.getMonitorType());
            }
            bind.npGameMonitorType.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    mValuesBundle.putInt(Db.MONITOR_TYPE, numberPicker.getValue());
                }
            });

            // Setup Button
            bind.btnSave.setText(R.string.save_changes);
            bind.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {

                        // Setup database paths
                        final String gamePath = Db.GAME_PATH + uid + "/" + gameId + "/";
                        final String userGamePath =
                                Db.USER_PATH + uid + Db.GAME_LIST_PATH + gameId + "/" + Db.NAME;

                        Map<String, Object> valuesMap = new HashMap<>();

                        if (mValuesBundle.containsKey(Db.NAME)) {
                            valuesMap.put(gamePath + Db.NAME, mValuesBundle.getString(Db.NAME));
                            valuesMap.put(userGamePath, mValuesBundle.getString(Db.NAME));
                        }

                        if (mValuesBundle.containsKey(Db.TYPE)) {
                            valuesMap.put(gamePath + Db.TYPE, mValuesBundle.getInt(Db.TYPE));
                        }

                        if (mValuesBundle.containsKey(Db.CABINET)) {
                            valuesMap.put(gamePath + Db.CABINET,
                                    mValuesBundle.getInt(Db.CABINET));
                        }

                        if (mValuesBundle.containsKey(Db.WORKING)) {
                            valuesMap.put(gamePath + Db.WORKING,
                                    mValuesBundle.getInt(Db.WORKING));
                        }

                        if (mValuesBundle.containsKey(Db.OWNERSHIP)) {
                            valuesMap.put(gamePath + Db.OWNERSHIP,
                                    mValuesBundle.getInt(Db.OWNERSHIP));
                        }

                        if (mValuesBundle.containsKey(Db.CONDITION)) {
                            valuesMap.put(gamePath + Db.CONDITION,
                                    mValuesBundle.getInt(Db.CONDITION));
                        }

                        if (mValuesBundle.containsKey(Db.MONITOR_SIZE)) {
                            valuesMap.put(gamePath + Db.MONITOR_SIZE,
                                    mValuesBundle.getInt(Db.MONITOR_SIZE));
                        }

                        if (mValuesBundle.containsKey(Db.MONITOR_PHOSPHER)) {
                            valuesMap.put(gamePath + Db.MONITOR_PHOSPHER,
                                    mValuesBundle.getInt(Db.MONITOR_PHOSPHER));
                        }

                        if (mValuesBundle.containsKey(Db.MONITOR_TYPE)) {
                            valuesMap.put(gamePath + Db.MONITOR_TYPE,
                                    mValuesBundle.getInt(Db.MONITOR_TYPE));
                        }

                        if (mValuesBundle.containsKey(Db.MONITOR_TECH)) {
                            valuesMap.put(gamePath + Db.MONITOR_TECH,
                                    mValuesBundle.getInt(Db.MONITOR_TECH));
                        }

                        mListener.onEditGameButtonPressed(valuesMap);
                    }
                }
            });

        } else {
            // user is not signed in
        }

        return rootView;
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
