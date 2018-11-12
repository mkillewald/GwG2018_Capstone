package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.GlideApp;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentGameDetailBinding;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.utility.Db;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

// TODO: also store full image to firebase, and display full image when click on thumb.
// TODO: add ability to store more than one image


public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final int REQUEST_IMAGE_CAPTURE = 343;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.gameaholix.coinops.fileprovider";

    private Context mContext;
    private Game mGame;
    private FirebaseUser mUser;
    private String mCurrentPhotoPath;
    private DatabaseReference mGameRef;
    private StorageReference mImageRootRef;
    private StorageReference mThumbRootRef;
    private ValueEventListener mGameListener;
    private OnFragmentInteractionListener mListener;
    private FragmentGameDetailBinding mBind;

    public GameDetailFragment() {
        // Required empty public constructor
    }

    public static GameDetailFragment newInstance(Game game) {
        Bundle args = new Bundle();
        GameDetailFragment fragment = new GameDetailFragment();
        args.putParcelable(EXTRA_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGame = getArguments().getParcelable(EXTRA_GAME);
            }
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }
        setHasOptionsMenu(true);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        mImageRootRef = storageRef
                .child(mUser.getUid())
                .child(mGame.getId());
        mThumbRootRef = storageRef
                .child(mUser.getUid())
                .child(mGame.getId())
                .child(Db.THUMB);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mGameRef = databaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGame.getId());


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_game_detail, container,
                false);

        final View rootView = mBind.getRoot();

        if (mUser != null) {
            // user is signed in

            // Setup event listener
            mGameListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gameId = dataSnapshot.getKey();

                    mGame = dataSnapshot.getValue(Game.class);
                    if (mGame == null) {
                        Log.d(TAG, "Error: Game details not found");
                    } else {
                        mGame.setId(gameId);

                        String noSelection = getString(R.string.not_available);
                        String[] typeArr = getResources().getStringArray(R.array.game_type);
                        typeArr[0] = noSelection;
                        String[] cabinetArr = getResources().getStringArray(R.array.game_cabinet);
                        cabinetArr[0] = noSelection;
                        String[] workingArr = getResources().getStringArray(R.array.game_working);
                        workingArr[0] = noSelection;
                        String[] ownershipArr =
                                getResources().getStringArray(R.array.game_ownership);
                        ownershipArr[0] = noSelection;
                        String[] conditionArr =
                                getResources().getStringArray(R.array.game_condition);
                        conditionArr[0] = noSelection;
                        String[] monitorPhospherArr =
                                getResources().getStringArray(R.array.game_monitor_phospher);
                        String[] monitorTypeArr =
                                getResources().getStringArray(R.array.game_monitor_beam);
                        String[] monitorTechArr =
                                getResources().getStringArray(R.array.game_monitor_tech);
                        String[] monitorSizeArr =
                                getResources().getStringArray(R.array.game_monitor_size);

                        if (mListener != null) {
                            mListener.onGameNameChanged(mGame.getName());
                        }

                        mBind.tvGameType.setText(typeArr[mGame.getType()]);
                        mBind.tvGameCabinet.setText(cabinetArr[mGame.getCabinet()]);
                        mBind.tvGameWorking.setText(workingArr[mGame.getWorking()]);
                        mBind.tvGameOwnership.setText(ownershipArr[mGame.getOwnership()]);
                        mBind.tvGameCondition.setText(conditionArr[mGame.getCondition()]);
                        mBind.tvGameMonitorPhospher
                                .setText(monitorPhospherArr[mGame.getMonitorPhospher()]);
                        mBind.tvGameMonitorType.setText(monitorTypeArr[mGame.getMonitorBeam()]);
                        mBind.tvGameMonitorTech.setText(monitorTechArr[mGame.getMonitorTech()]);
                        mBind.tvGameMonitorSize.setText(monitorSizeArr[mGame.getMonitorSize()]);

                        // Get thumbnail from firebase
                        StorageReference thumbRef = null;

                        if (!TextUtils.isEmpty(mGame.getImage())) {
                            thumbRef = mThumbRootRef.child(mGame.getImage());

                            mImageRootRef.child(mGame.getImage()).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    mBind.ivPhoto.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                        }
                                    });

                                }
                            });


                        }

                        GlideApp.with(mContext)
                                .load(thumbRef)
                                .placeholder(R.drawable.ic_classic_arcade_machine)
                                .into(mBind.ivPhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mGameRef.addValueEventListener(mGameListener);


            // Setup Buttons
            mBind.btnWebSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://sark.atomized.org/?s=" + mGame.getName()));
                        getActivity().startActivity(webIntent);
                    }
                }
            });


//        } else {
//            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mGameListener != null) {
            mGameRef.removeEventListener(mGameListener);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_GAME, mGame);
    }

    @Override
    public void onStart() {
        super.onStart();
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
                mListener.onEditButtonPressed(mGame);
                return true;
            case R.id.menu_add_photo:
                onLaunchCamera();
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Code used from https://developer.android.com/training/camera/photobasics

        // Create an image name from current timestamp
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

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
        // Get the dimensions of the View
//        int targetW = // mBind.ivPhoto.getWidth();
//        int targetH = // mBind.ivPhoto.getHeight();

        // Get the dimensions of the full bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

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

    // TODO: need to combine compressBitmapAndUploadToFirebase and compressThumbBitmapAndUploadToFirebase

    private void compressBitmapAndUploadToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        final String filename = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        UploadTask uploadImageTask = mImageRootRef.child(filename).putBytes(data);

        uploadImageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Full image successfully uploaded to firebase");

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListener.showSnackbar(R.string.upload_failed);
                Log.e(TAG, "Image upload failed -> ", e);
            }
        });
    }

    private void compressThumbBitmapAndUploadToFirebase(Bitmap bitmap) {
        // Compress bitmap to jpeg
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

        // Upload the thumbnail image
        final String filename = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        UploadTask uploadThumbnailTask = mThumbRootRef.child(filename).putBytes(data);

        uploadThumbnailTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Thumbnail successfully uploaded to firebase.");
                if (!TextUtils.isEmpty(mGame.getImage())) {
                    // Delete previous thumbnail image
                    mThumbRootRef.child(mGame.getImage()).delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to delete previous thumbnail image -> ", e);
                        }
                    });

                    // Delete previous full image
                    mImageRootRef.child(mGame.getImage()).delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to delete previous full image -> ", e);
                        }
                    });
                }

                // Store new image filename in database, this will trigger the ImageView to be reloaded.
                DatabaseReference filenameRef = mGameRef.child(Db.IMAGE);
                filenameRef.setValue(filename);

                // Update the mGame instance
                mGame.setImage(filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListener.showSnackbar(R.string.upload_failed);
                Log.e(TAG, "Image upload failed -> ", e);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onGameNameChanged(String name);
        void onEditButtonPressed(Game game);
        void showSnackbar(int stringResourceId);
    }


}
