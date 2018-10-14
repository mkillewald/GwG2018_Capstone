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
    private FragmentAddGameBinding mBinding;
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
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = mBinding.getRoot();

        final Spinner gameType = mBinding.spinnerGameType;
        final HintSpinnerAdapter typeAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_type));
        gameType.setAdapter(typeAdapter);
        gameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final Spinner gameCabinet = mBinding.spinnerGameCabinet;
        final HintSpinnerAdapter cabinetAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_cabinet));
        gameCabinet.setAdapter(cabinetAdapter);
        gameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final TextView gameConditionOff = mBinding.gameCondition.tvGameConditionOff;
        final TextView gameCondition1 = mBinding.gameCondition.tvGameCondition1;
        final TextView gameCondition2 = mBinding.gameCondition.tvGameCondition2;
        final TextView gameCondition3 = mBinding.gameCondition.tvGameCondition3;
        final TextView gameCondition4 = mBinding.gameCondition.tvGameCondition4;
        final TextView gameCondition5 = mBinding.gameCondition.tvGameCondition5;
        final TextView gameCondition6 = mBinding.gameCondition.tvGameCondition6;
        final TextView gameCondition7 = mBinding.gameCondition.tvGameCondition7;
        final TextView gameCondition8 = mBinding.gameCondition.tvGameCondition8;
        final TextView gameCondition9 = mBinding.gameCondition.tvGameCondition9;
        final TextView gameCondition10 = mBinding.gameCondition.tvGameCondition10;
        final TextView gameCondition11 = mBinding.gameCondition.tvGameCondition11;

        final TextView[] gameConditionLabels = { gameConditionOff, gameCondition1, gameCondition2,
                gameCondition3, gameCondition4, gameCondition5, gameCondition6, gameCondition7,
                gameCondition8, gameCondition9, gameCondition10, gameCondition11 };

        final SeekBar gameCondition = mBinding.sbGameCondition;
        gameCondition.setMax(11);

        gameCondition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        final Spinner gameWorking = mBinding.spinnerGameWorking;
        final HintSpinnerAdapter workingAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_working));
        gameWorking.setAdapter(workingAdapter);
        gameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final Spinner gameOwnership = mBinding.spinnerGameOwnership;
        final HintSpinnerAdapter ownershipAdapter = new HintSpinnerAdapter(
                getContext(), getResources().getStringArray(R.array.game_ownership));
        gameOwnership.setAdapter(ownershipAdapter);
        gameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final NumberPicker gameMonitorPhospher = mBinding.npGameMonitorPhospher;
        gameMonitorPhospher.setMinValue(0);
        gameMonitorPhospher.setMaxValue(2);
        gameMonitorPhospher.setDisplayedValues(getResources().getStringArray(R.array.game_monitor_phospher));
        gameMonitorPhospher.setValue(1);
        gameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mNewGame.setMonitorPhospher(gameMonitorPhospher.getDisplayedValues()[i];
            }
        });

        final NumberPicker gameMonitorTech = mBinding.npGameMonitorTech;
        gameMonitorTech.setMinValue(0);
        gameMonitorTech.setMaxValue(2);
        gameMonitorTech.setDisplayedValues(getResources().getStringArray(R.array.game_monitor_tech));
        gameMonitorTech.setValue(1);
        gameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mNewGanme.setMonitorTech(gameMonitorTech.getDisplayedValues()[i];
            }
        });

        final NumberPicker gameMonitorType = mBinding.npGameMonitorType;
        gameMonitorType.setMinValue(0);
        gameMonitorType.setMaxValue(2);
        gameMonitorType.setDisplayedValues(getResources().getStringArray(R.array.game_monitor_type));
        gameMonitorType.setValue(1);
        gameMonitorType.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorType(gameMonitorType.getDisplayedValues()[i]);
            }
        });

        final RadioGroup gameMonitorSize = mBinding.rgGameMonitorSize;
        gameMonitorSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = rootView.findViewById(i);
                mNewGame.setMonitorSize(radioButton.getText().toString());
            }
        });

        Button addGame = mBinding.btnAddGame;
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    final EditText gameName = mBinding.etAddGameName;
                    mNewGame.setName(gameName.getText().toString());
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
