package com.gameaholix.coinops.game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentGameDetailBinding;
import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.ImageUtils;
import com.gameaholix.coinops.utility.GlideApp;
import com.gameaholix.coinops.game.viewModel.GameViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

// TODO: add ability to store more than one image per game

public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.gameaholix.coinops.fileprovider";
    private static final int REQUEST_IMAGE_CAPTURE = 343;

    private Context mContext;
    private GameViewModel mViewModel;
    private Game mGame;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public GameDetailFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment instance
     * @return the fragment instance
     */
    public static GameDetailFragment newInstance() {
        return new GameDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentGameDetailBinding bind = DataBindingUtil.inflate(inflater, R.layout.fragment_game_detail, container,
                false);

        if (getActivity() == null) { return bind.getRoot(); }

        // this will cause the Activity's onPrepareOptionsMenu() method to be called
        getActivity().invalidateOptionsMenu();

        mViewModel = ViewModelProviders
                .of(getActivity())
                .get(GameViewModel.class);
        LiveData<Game> gameLiveData = mViewModel.getGameLiveData();

        // Initialize Firebase components for image storage
        // TODO: move this to the ViewModel/Repository
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (TextUtils.isEmpty(mViewModel.getGameId())) {
            mListener.onGameIdInvalid();
        }

        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // read game details
            gameLiveData.observe(getActivity(), new Observer<Game>() {
                @Override
                public void onChanged(@Nullable Game game) {
                    if (game != null) {
                        // TODO: figure out how to do this with xml
                        mGame = game;

                        if (mListener != null) {
                            mListener.onGameNameChanged(game.getName());
                        }

                        // Get thumbnail from firebase
                        StorageReference thumbRef = null;

                        if (!TextUtils.isEmpty(game.getImage())) {
                            thumbRef = Fb.getImageThumbRef(uid, mViewModel.getGameId(), game.getImage());
                        }

                        GlideApp.with(mContext)
                                .load(thumbRef)
                                .placeholder(R.drawable.ic_classic_arcade_machine)
                                .into(bind.ivPhoto);
                    }
                }
            });
//        } else {
//            // user is not signed in
        }

        bind.setLifecycleOwner(getActivity());
        bind.setGame(gameLiveData);

        String noSelection = getString(R.string.not_available);

        final String[] typeArray = getResources().getStringArray(R.array.game_type);
        typeArray[0] = noSelection;
        bind.setTypeArray(typeArray);

        final String[] cabinetArray = getResources().getStringArray(R.array.game_cabinet);
        cabinetArray[0] = noSelection;
        bind.setCabinetArray(cabinetArray);

        final String[] statusArray = getResources().getStringArray(R.array.game_working);
        statusArray[0] = noSelection;
        bind.setStatusArray(statusArray);

        final String[] ownershipArray =
                getResources().getStringArray(R.array.game_ownership);
        ownershipArray[0] = noSelection;
        bind.setOwnershipArray(ownershipArray);

        final String[] conditionArray =
                getResources().getStringArray(R.array.game_condition);
        conditionArray[0] = noSelection;
        bind.setConditionArray(conditionArray);

        final String[] monitorPhospherArray =
                getResources().getStringArray(R.array.game_monitor_phospher);
        bind.setMonitorPhospherArray(monitorPhospherArray);

        final String[] monitorBeamArray =
                getResources().getStringArray(R.array.game_monitor_beam);
        bind.setMonitorBeamArray(monitorBeamArray);

        final String[] monitorTechArray =
                getResources().getStringArray(R.array.game_monitor_tech);
        bind.setMonitorTechArray(monitorTechArray);

        final String[] monitorSizeArray =
                getResources().getStringArray(R.array.game_monitor_size);
        bind.setMonitorSizeArray( monitorSizeArray);

        // Setup ImageView click listener
        bind.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mGame.getImage())) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        // user is signed in
                        String uid = user.getUid();
                        String gameId = mViewModel.getGameId();
                        String imagePath = Fb.getImageRef(uid, gameId, mGame.getImage()).getPath();
                        mListener.onGameImageClicked(imagePath);
                    }
                }
            }
        });

        // Setup Buttons
        bind.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });

        bind.btnWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://sark.atomized.org/?s=" + mGame.getName()));
                    getActivity().startActivity(webIntent);
                }
            }
        });

        bind.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onGameEditButtonPressed();
            }
        });

        bind.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert();
            }
        });

        return bind.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
            case R.id.menu_edit_game:
                mListener.onGameEditButtonPressed();
                return true;
            case R.id.menu_delete_game:
                showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onLaunchCamera() {
        // Code used from https://developer.android.com/training/camera/photobasics

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(mContext);
            } catch (IOException e) {
                // Error occurred while creating the File
                Log.e(TAG, "Error creating image file -> ", e);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mViewModel.setCurrentPhotoPath(photoFile.getAbsolutePath());
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        CAPTURE_IMAGE_FILE_PROVIDER,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // used from https://stackoverflow.com/questions/24467696/android-file-provider-permission-denial
                if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ) {
                    takePictureIntent.setClipData( ClipData.newRawUri( "", photoURI ) );
                    takePictureIntent.addFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION );
                }

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // compress image and upload to firebase
            mViewModel.uploadImage();

        }
    }

    private void showDeleteAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(mContext,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        }
        builder.setTitle(R.string.really_delete_game)
                .setMessage(R.string.game_will_be_deleted)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllGameData();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllGameData() {
        mViewModel.delete();
        mListener.onGameDeleteCompleted();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onGameIdInvalid();
        void onGameNameChanged(String name);
        void onGameEditButtonPressed();
        void onGameDeleteCompleted();
        void onGameImageClicked(String imagePath);
        void onShowSnackbar(int stringResourceId);
    }


}
