package com.gameaholix.coinops.game;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddGameBinding;
import com.gameaholix.coinops.utility.HintSpinnerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGameFragment extends Fragment {
    private static final String TAG = AddGameFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Game mNewGame;

    public AddGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewGame = new Game();

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        final FragmentAddGameBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = binding.getRoot();

        // Bind UI elements
        final EditText mGameNameEditText = binding.etAddGameName;

        final Spinner gameTypeSpinner = binding.spinnerGameType;
        final Spinner gameCabinetSpinner = binding.spinnerGameCabinet;
        final Spinner gameWorkingSpinner = binding.spinnerGameWorking;
        final Spinner gameOwnershipSpinner = binding.spinnerGameOwnership;

        final TextView gameConditionOff = binding.gameConditionLabels.tvGameConditionOff;
        final TextView gameCondition1 = binding.gameConditionLabels.tvGameCondition1;
        final TextView gameCondition2 = binding.gameConditionLabels.tvGameCondition2;
        final TextView gameCondition3 = binding.gameConditionLabels.tvGameCondition3;
        final TextView gameCondition4 = binding.gameConditionLabels.tvGameCondition4;
        final TextView gameCondition5 = binding.gameConditionLabels.tvGameCondition5;
        final TextView gameCondition6 = binding.gameConditionLabels.tvGameCondition6;
        final TextView gameCondition7 = binding.gameConditionLabels.tvGameCondition7;
        final TextView gameCondition8 = binding.gameConditionLabels.tvGameCondition8;
        final TextView gameCondition9 = binding.gameConditionLabels.tvGameCondition9;
        final TextView gameCondition10 = binding.gameConditionLabels.tvGameCondition10;
        final TextView gameCondition11 = binding.gameConditionLabels.tvGameCondition11;
        final TextView[] gameConditionLabels = { gameConditionOff, gameCondition1, gameCondition2,
                gameCondition3, gameCondition4, gameCondition5, gameCondition6, gameCondition7,
                gameCondition8, gameCondition9, gameCondition10, gameCondition11 };

        final SeekBar gameConditionSeekBar = binding.sbGameCondition;

        final NumberPicker gameMonitorPhospherPicker = binding.npGameMonitorPhospher;
        final NumberPicker gameMonitorTechPicker = binding.npGameMonitorTech;
        final NumberPicker gameMonitorTypePicker = binding.npGameMonitorType;

        final RadioGroup gameMonitorSizeRadioGroup = binding.rgGameMonitorSize;

        // Setup Spinners
        final HintSpinnerAdapter typeAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_type));
        gameTypeSpinner.setAdapter(typeAdapter);
        gameTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    mNewGame.setType(selectedItemText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final HintSpinnerAdapter cabinetAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_cabinet));
        gameCabinetSpinner.setAdapter(cabinetAdapter);
        gameCabinetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    mNewGame.setCabinet(selectedItemText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final HintSpinnerAdapter workingAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_working));
        gameWorkingSpinner.setAdapter(workingAdapter);
        gameWorkingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    mNewGame.setWorking(selectedItemText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final HintSpinnerAdapter ownershipAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_ownership));
        gameOwnershipSpinner.setAdapter(ownershipAdapter);
        gameOwnershipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    mNewGame.setStatus(selectedItemText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup SeekBar
        gameConditionSeekBar.setMax(11);
        gameConditionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                        mNewGame.setCondition(textViewAtPosition.getText().toString());

                        seekBar.setSecondaryProgress(progress);
                    }
                }

            }
        });

        // Setup NumberPickers
        gameMonitorPhospherPicker.setMinValue(0);
        gameMonitorPhospherPicker.setMaxValue(2);
        gameMonitorPhospherPicker.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_phospher));
        gameMonitorPhospherPicker.setValue(1);
        gameMonitorPhospherPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mNewGame.setMonitorPhospher(gameMonitorPhospher.getDisplayedValues()[i];
            }
        });

        gameMonitorTechPicker.setMinValue(0);
        gameMonitorTechPicker.setMaxValue(2);
        gameMonitorTechPicker.setDisplayedValues(getResources().getStringArray(R.array.game_monitor_tech));
        gameMonitorTechPicker.setValue(1);
        gameMonitorTechPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mNewGanme.setMonitorTech(gameMonitorTech.getDisplayedValues()[i];
            }
        });

        gameMonitorTypePicker.setMinValue(0);
        gameMonitorTypePicker.setMaxValue(2);
        gameMonitorTypePicker.setDisplayedValues(getResources().getStringArray(R.array.game_monitor_type));
        gameMonitorTypePicker.setValue(1);
        gameMonitorTypePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorType(gameMonitorTypePicker.getDisplayedValues()[i]);
            }
        });

        // Setuo RadioGroup
        gameMonitorSizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = rootView.findViewById(i);
                mNewGame.setMonitorSize(radioButton.getText().toString());
            }
        });

        // Setup Buttons
        Button addGameButton = binding.btnAddGame;
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mNewGame.setName(mGameNameEditText.getText().toString());
                    mListener.onAddGameButtonPressed(mNewGame);
                }
            }
        });

        return rootView;
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
        void onAddGameButtonPressed(Game game);
    }
}
