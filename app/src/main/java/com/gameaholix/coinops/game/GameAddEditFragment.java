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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private FragmentGameAddBinding mBind;
    private String mGameId;
    private Game mGame;
    private GameViewModel mViewModel;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    /**
     * Required empty public constructor
     */
    public GameAddEditFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment to add a new Game
     * @return the fragment instance
     */
    public static GameAddEditFragment newAddInstance() {
        Bundle args = new Bundle();
        GameAddEditFragment fragment = new GameAddEditFragment();
        args.putBoolean(EXTRA_GAME_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Static factory method used to instantiate a fragment to edit an existing Game
     * @param gameId the ID of the existing Game
     * @return the fragment instance
     */
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog() && !mEdit) {
            getDialog().setTitle(R.string.add_game_title);
        }

        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_add, container, false);

        if (getActivity() == null) { return mBind.getRoot(); }

        // If we are editing, this should get the existing view model, and if we are adding, this
        // should create a new view model (mItemId will be null).
        mViewModel = ViewModelProviders
                .of(getActivity(), new GameViewModelFactory(mGameId))
                .get(GameViewModel.class);

        // if this is a brand new fragment instance, clear ViewModel's LiveData copy
        if (savedInstanceState == null) mViewModel.clearGameCopyLiveData();

        // get a duplicate LiveData to make changes to, this way we can maintain state of those
        // changes, and also easily revert any unsaved changes.
        LiveData<Game> gameLiveData = mViewModel.getGameCopyLiveData();
        gameLiveData.observe(getActivity(), new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game game) {
                mGame = game;
            }
        });

        mBind.setLifecycleOwner(getActivity());
        mBind.setGame(gameLiveData);

        gameLiveData.observe(this, new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game game) {
                if (game != null) {
                    mGame = game;

                    // Setup Monitor Details TextView
                    updateMonitorDetails(mGame, mBind.tvMonitorDetails);
                }
            }
        });

        // Setup EditTexts
        mBind.etGameName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerGameType.setAdapter(typeAdapter);
        mBind.spinnerGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGame.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> cabinetAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_cabinet, android.R.layout.simple_spinner_item);
        cabinetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerGameCabinet.setAdapter(cabinetAdapter);
        mBind.spinnerGameCabinet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGame.setCabinet(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> workingAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_working, android.R.layout.simple_spinner_item);
        workingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerGameWorking.setAdapter(workingAdapter);
        mBind.spinnerGameWorking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGame.setWorking(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_ownership, android.R.layout.simple_spinner_item);
        ownershipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerGameOwnership.setAdapter(ownershipAdapter);
        mBind.spinnerGameOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGame.setOwnership(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.game_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerGameCondition.setAdapter(conditionAdapter);
        mBind.spinnerGameCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGame.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Setup Monitor Details Dialog
        // TODO: there has to be a better way of doing this, at least move this to its own fragment
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

                mGame.setMonitorSize(dialogBind.npGameMonitorSize.getValue());
                mGame.setMonitorPhospher(dialogBind.npGameMonitorPhospher.getValue());
                mGame.setMonitorBeam(dialogBind.npGameMonitorBeam.getValue());
                mGame.setMonitorTech(dialogBind.npGameMonitorTech.getValue());

                updateMonitorDetails(mGame, mBind.tvMonitorDetails);
                dialogInterface.dismiss();
            }
        });
        monitorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // this gets called if user taps outside of dialog
                ((ViewGroup) monitorDialogView.getParent()).removeView(monitorDialogView);

                dialogBind.npGameMonitorSize.setValue(mGame.getMonitorSize());
                dialogBind.npGameMonitorPhospher.setValue(mGame.getMonitorPhospher());
                dialogBind.npGameMonitorBeam.setValue(mGame.getMonitorBeam());
                dialogBind.npGameMonitorTech.setValue(mGame.getMonitorTech());

                updateMonitorDetails(mGame, mBind.tvMonitorDetails);
                dialogInterface.dismiss();
            }
        });

        if (mEdit) {
            dialogBind.setLifecycleOwner(getActivity());
        } else {
            dialogBind.setLifecycleOwner(this);
        }
        dialogBind.setGame(gameLiveData);

        // Setup NumberPickers in dialog
        dialogBind.npGameMonitorSize.setMinValue(0);
        dialogBind.npGameMonitorSize.setMaxValue(5);
        dialogBind.npGameMonitorSize.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_size));
        dialogBind.npGameMonitorSize.setWrapSelectorWheel(false);

        dialogBind.npGameMonitorPhospher.setMinValue(0);
        dialogBind.npGameMonitorPhospher.setMaxValue(2);
        dialogBind.npGameMonitorPhospher.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_phospher));
        dialogBind.npGameMonitorPhospher.setWrapSelectorWheel(false);

        dialogBind.npGameMonitorBeam.setMinValue(0);
        dialogBind.npGameMonitorBeam.setMaxValue(2);
        dialogBind.npGameMonitorBeam.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_beam));
        dialogBind.npGameMonitorBeam.setWrapSelectorWheel(false);

        dialogBind.npGameMonitorTech.setMinValue(0);
        dialogBind.npGameMonitorTech.setMaxValue(2);
        dialogBind.npGameMonitorTech.setDisplayedValues(
                getResources().getStringArray(R.array.game_monitor_tech));
        dialogBind.npGameMonitorTech.setWrapSelectorWheel(false);

        // Setup monitor details onClickListener
        View.OnClickListener monitorDetailsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monitorDialog.show();
            }
        };
        mBind.tvMonitorDetails.setOnClickListener(monitorDetailsListener);
        mBind.ibMonitorDetailsArrow.setOnClickListener(monitorDetailsListener);

        // Setup Buttons
        mBind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.clearGameCopyLiveData();
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onGameAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) mBind.btnSave.setText(R.string.save_changes);
        mBind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.setName(mBind.etGameName.getText().toString().trim());
                mGame.setType(mBind.spinnerGameType.getSelectedItemPosition());
                mGame.setCabinet(mBind.spinnerGameCabinet.getSelectedItemPosition());
                mGame.setWorking(mBind.spinnerGameWorking.getSelectedItemPosition());
                mGame.setOwnership(mBind.spinnerGameOwnership.getSelectedItemPosition());
                mGame.setCondition(mBind.spinnerGameCondition.getSelectedItemPosition());

                addUpdateGame();
                mViewModel.clearGameCopyLiveData();
                mListener.onGameAddEditCompletedOrCancelled();
            }
        });

        return mBind.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save text input to ViewModel when configuration change occurs.
        mGame.setName(mBind.etGameName.getText().toString());

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

    // TODO: copy this to GameDetailFragment, replace NA in array with blank string.
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
     * Add a new or update an existing Game instance to data storage through ViewModel
     */
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
