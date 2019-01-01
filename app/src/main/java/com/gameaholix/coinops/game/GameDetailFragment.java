package com.gameaholix.coinops.game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.utility.GlideApp;
import com.gameaholix.coinops.game.viewModel.GameViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

// TODO: add ability to store more than one image per game

public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.gameaholix.coinops.fileprovider";
    private static final String THUMB = "thumb_";
    private static final int REQUEST_IMAGE_CAPTURE = 343;

    private Context mContext;
    private GameViewModel mViewModel;
    private StorageReference mImageRootRef;
    private String mCurrentPhotoPath;
    private OnFragmentInteractionListener mListener;

    // used for image operations (uploading, etc.)
    // TODO: move this to ViewModel
    private Game mGame;
    private String mGameId;

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
        mGameId = mViewModel.getGameId();
        LiveData<Game> gameLiveData = mViewModel.getGameLiveData();

        // Initialize Firebase components for image storage
        // TODO: move this to the ViewModel
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (TextUtils.isEmpty(mGameId)) {
            mListener.onGameIdInvalid();
        }

        if (user != null) {
            mImageRootRef = storageRef
                    .child(user.getUid())
                    .child(mGameId);
        }

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
                        thumbRef = mImageRootRef.child(THUMB + game.getImage());
                    }

                    GlideApp.with(mContext)
                            .load(thumbRef)
                            .placeholder(R.drawable.ic_classic_arcade_machine)
                            .into(bind.ivPhoto);
                }
            }
        });

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
                    String imagePath = mImageRootRef.child(mGame.getImage()).getPath();
                    mListener.onGameImageClicked(imagePath);
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
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Log.e(TAG, "Error creating image file -> ", e);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
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

    private File createImageFile() throws IOException {
        // Code used from https://developer.android.com/training/camera/photobasics

        // Create an image name from current timestamp
        String filename = new SimpleDateFormat("yyyyMMdd_hhmmss_", Locale.US).format(new Date());

        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                filename,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private Bitmap scaleBitmap(String filePath, int targetW, int targetH) {
        // Get the dimensions of the full bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the target
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // return the scaled bitmap
        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Get full image
            Bitmap fullBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            if (fullBitmap != null) {
                // compress and upload to firebase
                compressBitmapAndUploadToFirebase(fullBitmap);

                // create thumbnail image
                Bitmap thumbBitmap = scaleBitmap(mCurrentPhotoPath, 140, 105);

                // compress and upload thumbnail to firebase
                compressThumbBitmapAndUploadToFirebase(thumbBitmap);
            } else {
                Log.e(TAG, "Image file could not be found.");
            }
        }
    }

    private void compressBitmapAndUploadToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        final String filename = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        UploadTask uploadImageTask = mImageRootRef.child(filename).putBytes(data);

        uploadImageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Full image successfully uploaded to firebase");

                // delete temp image file from external storage
                deleteTemporaryImageFromDisk();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListener.onShowSnackbar(R.string.error_upload_failed);
                Log.e(TAG, "Image upload failed -> ", e);
            }
        });
    }

    private void compressThumbBitmapAndUploadToFirebase(Bitmap bitmap) {
        // Compress bitmap to jpeg
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the thumbnail image
        final String filename = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        UploadTask uploadThumbnailTask = mImageRootRef.child(THUMB + filename).putBytes(data);

        uploadThumbnailTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Thumbnail successfully uploaded to firebase.");

                // delete previous images from firebase
                deleteImagesFromFirebase();

                // Store new image filename in database, this will trigger the ImageView to be reloaded.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference filenameRef = FirebaseDatabase.getInstance().getReference()
                            .child(Fb.GAME)
                            .child(uid)
                            .child(mGameId)
                            .child(Fb.IMAGE);
                    filenameRef.setValue(filename);
                }

                // Update the mGame instance
                mGame.setImage(filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListener.onShowSnackbar(R.string.error_upload_failed);
                Log.e(TAG, "Image upload failed -> ", e);
            }
        });
    }

    private void deleteTemporaryImageFromDisk() {
        // delete file from external storage
        File fdelete = new File(mCurrentPhotoPath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d(TAG,"file Deleted :" + mCurrentPhotoPath);
            } else {
                Log.e(TAG, "file not Deleted :" + mCurrentPhotoPath);
            }
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

    private void deleteImagesFromFirebase() {
        if (!TextUtils.isEmpty(mGame.getImage())) {
            // Delete thumbnail image
            Log.d(TAG, "image: " + mImageRootRef + "/" + mGame.getImage());
            mImageRootRef.child(THUMB + mGame.getImage()).delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to delete previous thumbnail image -> ", e);
                }
            });

            // Delete full size  image
            mImageRootRef.child(mGame.getImage()).delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to delete previous full image -> ", e);
                }
            });
        }
    }

    private void deleteAllGameData() {
        deleteImagesFromFirebase();
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
