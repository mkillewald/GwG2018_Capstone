package com.gameaholix.coinops.game;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.DialogMonitorDetailsBinding;
import com.gameaholix.coinops.databinding.FragmentGameAddBinding;
import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.game.viewModel.GameViewModel;
import com.gameaholix.coinops.game.viewModel.GameViewModelFactory;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.PromptUser;

public class GameAddEditFragment extends BaseDialogFragment {
    private static final String TAG = GameAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_GAME_EDIT_FLAG = "CoinOpsGameEditFlag";

    private Context mContext;
    private String mGameId;
    private Game mGame;
    private GameViewModel mViewModel;
    private LiveData<Game> mGameLiveData;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    public GameAddEditFragment() {
        // Required empty public constructor
    }

    // add a new Game
    public static GameAddEditFragment newAddInstance() {
        Bundle args = new Bundle();
        GameAddEditFragment fragment = new GameAddEditFragment();
        args.putBoolean(EXTRA_GAME_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    // update an existing Game
    public static GameAddEditFragment newEditInstance(String gameId) {
        Bundle args = new Bundle();
        GameAddEditFragment fragment = new GameAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
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
                mGameId = getArguments().getString(EXTRA_GAME_ID);
                mEdit = getArguments().getBoolean(EXTRA_GAME_EDIT_FLAG);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mEdit = savedInstanceState.getBoolean(EXTRA_GAME_EDIT_FLAG);
        }

        if (getActivity() != null) {
            // If we are editing, get the existing view model (or create new view model if one
            // doesn't already exist) with the parent activity as the lifecycle owner.
            // If we are adding, always create a new view model (mItemId will be null) with this
            // fragment instance as the lifecycle owner
            if (mEdit) {
                mViewModel = ViewModelProviders
                        .of(getActivity(), new GameViewModelFactory(mGameId))
                        .get(GameViewModel.class);
            } else {
                mViewModel = ViewModelProviders
                        .of(this)
                        .get(GameViewModel.class);
            }
            mGameLiveData = mViewModel.getGameLiveData();
            mGameLiveData.observe(this, new Observer<Game>() {
                @Override
                public void onChanged(@Nullable Game game) {
                    mGame = game;
                    Log.d(TAG, "mGame set: " + mGame);
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog() && !mEdit) {
            getDialog().setTitle(R.string.add_game_title);
        }

        // Inflate the layout for this fragment
        final FragmentGameAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_add, container, false);
        final View rootView = bind.getRoot();

        if (mEdit) {
            bind.setLifecycleOwner(getActivity());
        } else {
            bind.setLifecycleOwner(this);
        }
        bind.setGame(mGameLiveData);

        // Name field cannot be blank, add listeners to validate Name input
        bind.etGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (!textInputIsValid(input)) {
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
                        String input = ((EditText) view).getText().toString().trim();
                        if (!textInputIsValid(input)) {
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

        ArrayAdapter<CharSequence> cabinetAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_cabinet, android.R.layout.simple_spinner_item);
        cabinetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCabinet.setAdapter(cabinetAdapter);

        ArrayAdapter<CharSequence> workingAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_working, android.R.layout.simple_spinner_item);
        workingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameWorking.setAdapter(workingAdapter);

        ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_ownership, android.R.layout.simple_spinner_item);
        ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameOwnership.setAdapter(ownershipAdapter);

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerGameCondition.setAdapter(conditionAdapter);

//        // Setup Monitor Detais TextView
//        updateMonitorDetails(mGame, bind.tvMonitorDetails);
//
//        // Setup Monitor Details Dialog
//        final AlertDialog.Builder monitorDialog = new AlertDialog.Builder(mContext);
//        final DialogMonitorDetailsBinding dialogBind = DataBindingUtil.inflate(
//                inflater, R.layout.dialog_monitor_details, container, false);
//        monitorDialog.setTitle(R.string.select_game_monitor_details);
//        final View monitorDialogView = dialogBind.getRoot();
//        monitorDialog.setView(monitorDialogView);
//        monitorDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // this gets called if user taps on Done button
//                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
//                updateMonitorDetails(mGame, bind.tvMonitorDetails);
//                dialogInterface.dismiss();
//            }
//        });
//        monitorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                // this gets called if user taps outside of dialog
//                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);
//                updateMonitorDetails(mGame, bind.tvMonitorDetails);
//                dialogInterface.dismiss();
//            }
//        });
//
//        // Setup NumberPickers in dialog
//        dialogBind.npGameMonitorSize.setMinValue(0);
//        dialogBind.npGameMonitorSize.setMaxValue(5);
//        dialogBind.npGameMonitorSize.setDisplayedValues(
//                getResources().getStringArray(R.array.game_monitor_size));
//        dialogBind.npGameMonitorSize.setWrapSelectorWheel(false);
//        dialogBind.npGameMonitorSize.setValue(mGame.getMonitorSize());
//        dialogBind.npGameMonitorSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mGame.setMonitorSize(numberPicker.getValue());
//            }
//        });
//
//        dialogBind.npGameMonitorPhospher.setMinValue(0);
//        dialogBind.npGameMonitorPhospher.setMaxValue(2);
//        dialogBind.npGameMonitorPhospher.setDisplayedValues(
//                getResources().getStringArray(R.array.game_monitor_phospher));
//        dialogBind.npGameMonitorPhospher.setWrapSelectorWheel(false);
//        dialogBind.npGameMonitorPhospher.setValue(mGame.getMonitorPhospher());
//        dialogBind.npGameMonitorPhospher.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mGame.setMonitorPhospher(numberPicker.getValue());
//            }
//        });
//
//        dialogBind.npGameMonitorBeam.setMinValue(0);
//        dialogBind.npGameMonitorBeam.setMaxValue(2);
//        dialogBind.npGameMonitorBeam.setDisplayedValues(
//                getResources().getStringArray(R.array.game_monitor_beam));
//        dialogBind.npGameMonitorBeam.setWrapSelectorWheel(false);
//        dialogBind.npGameMonitorBeam.setValue(mGame.getMonitorBeam());
//        dialogBind.npGameMonitorBeam.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mGame.setMonitorBeam(numberPicker.getValue());
//            }
//        });
//
//        dialogBind.npGameMonitorTech.setMinValue(0);
//        dialogBind.npGameMonitorTech.setMaxValue(2);
//        dialogBind.npGameMonitorTech.setDisplayedValues(
//                getResources().getStringArray(R.array.game_monitor_tech));
//        dialogBind.npGameMonitorTech.setWrapSelectorWheel(false);
//        dialogBind.npGameMonitorTech.setValue(mGame.getMonitorTech());
//        dialogBind.npGameMonitorTech.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                mGame.setMonitorTech(numberPicker.getValue());
//            }
//        });
//
//        // Setup monitor details onClickListener
//        View.OnClickListener monitorDetailsListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                monitorDialog.show();
//            }
//        };
//        bind.tvMonitorDetails.setOnClickListener(monitorDetailsListener);
//        bind.ibMonitorDetailsArrow.setOnClickListener(monitorDetailsListener);

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
                String input = bind.etGameName.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mGame.setName(input);
                }

                mGame.setType(bind.spinnerGameType.getSelectedItemPosition());
                mGame.setCabinet(bind.spinnerGameCabinet.getSelectedItemPosition());
                mGame.setWorking(bind.spinnerGameWorking.getSelectedItemPosition());
                mGame.setOwnership(bind.spinnerGameOwnership.getSelectedItemPosition());
                mGame.setCondition(bind.spinnerGameCondition.getSelectedItemPosition());

                addUpdateGame();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
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

    private void addUpdateGame() {
        if (TextUtils.isEmpty(mGame.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_game_failed,
                    R.string.error_name_empty);
            Log.e(TAG, "Failed to add game! Name field was blank.");
            return;
        }

        boolean resultOk;
        if (mEdit) {
            resultOk = mViewModel.update();
        } else {
            resultOk = mViewModel.add();
        }

        if (resultOk) {
            if (getShowsDialog()) getDialog().dismiss();
            mListener.onGameAddEditCompletedOrCancelled();
        } else {
            Log.d(TAG, "The add or edit operation has failed!");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onGameAddEditCompletedOrCancelled();
    }
}
