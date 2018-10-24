package com.gameaholix.coinops.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

public class AddGameFragment extends Fragment {
//    private static final String TAG = AddGameFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";

    private Context mContext;
    private Game mNewGame;
    private OnFragmentInteractionListener mListener;

    public AddGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        final FragmentAddGameBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_game, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            mNewGame = new Game();
        } else {
            mNewGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }
        // Setup EditText
        bind.etGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    mNewGame.setName(textView.getText().toString().trim());
                    hideKeyboard(textView);
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
                        mNewGame.setName(editText.getText().toString().trim());
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
        bind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewGame.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup Monitor Details Dialog
        final AlertDialog.Builder monitorDialog = createMonitorDialog(inflater, container);

        bind.tvMonitorDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monitorDialog.show();
            }
        });

        // Setup Buttons
        Button addGameButton = bind.btnSave;
        addGameButton.setText(R.string.add_game);
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAddGameButtonPressed(mNewGame);
                }
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

    private AlertDialog.Builder createMonitorDialog(LayoutInflater inflater, ViewGroup container) {
        final AlertDialog.Builder monitorDialog = new AlertDialog.Builder(mContext);
        final DialogMonitorDetailsBinding monitorDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_monitor_details, container, false);
        monitorDialog.setTitle(R.string.select_game_monitor);
        monitorDialog.setMessage("this is a message");
        final View monitorDialogView = monitorDetailsBinding.getRoot();
        monitorDialog.setView(monitorDialogView);
        monitorDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d(TAG, "onClick: " + numberPicker.getValue());
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                dialogInterface.dismiss();
            }
        });
        monitorDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
                dialogInterface.dismiss();
            }
        });

        // Setup NumberPickers
        monitorDetailsBinding.npGameMonitorSize.setMinValue(0);
        monitorDetailsBinding.npGameMonitorSize.setMaxValue(5);
        monitorDetailsBinding.npGameMonitorSize.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_size));
        monitorDetailsBinding.npGameMonitorSize.setWrapSelectorWheel(false);
        monitorDetailsBinding.npGameMonitorSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorSize(numberPicker.getValue());
            }
        });

        monitorDetailsBinding.npGameMonitorPhospher.setMinValue(0);
        monitorDetailsBinding.npGameMonitorPhospher.setMaxValue(2);
        monitorDetailsBinding.npGameMonitorPhospher.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_phospher));
        monitorDetailsBinding.npGameMonitorPhospher.setWrapSelectorWheel(false);
        monitorDetailsBinding.npGameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorPhospher(numberPicker.getValue());
            }
        });

        monitorDetailsBinding.npGameMonitorTech.setMinValue(0);
        monitorDetailsBinding.npGameMonitorTech.setMaxValue(2);
        monitorDetailsBinding.npGameMonitorTech.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_tech));
        monitorDetailsBinding.npGameMonitorTech.setWrapSelectorWheel(false);
        monitorDetailsBinding.npGameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorTech(numberPicker.getValue());
            }
        });

        monitorDetailsBinding.npGameMonitorType.setMinValue(0);
        monitorDetailsBinding.npGameMonitorType.setMaxValue(2);
        monitorDetailsBinding.npGameMonitorType.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_type));
        monitorDetailsBinding.npGameMonitorType.setWrapSelectorWheel(false);
        monitorDetailsBinding.npGameMonitorType.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mNewGame.setMonitorType(numberPicker.getValue());
            }
        });

        return monitorDialog;
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
