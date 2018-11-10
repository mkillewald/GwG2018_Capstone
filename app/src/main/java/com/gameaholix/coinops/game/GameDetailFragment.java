package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;


public class GameDetailFragment extends Fragment {
    private static final String TAG = GameDetailFragment.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final int REQUEST_IMAGE_CAPTURE = 343;

    private Game mGame;
    private FirebaseUser mUser;
    private DatabaseReference mGameRef;
    private StorageReference mStorageRef;
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
        mStorageRef = storage.getReference();

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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                mBind.ivPhoto.setVisibility(View.VISIBLE);
                mBind.ivPhoto.setImageBitmap(bitmap);
                saveBitmapToFirebase(bitmap);
            }
        }
    }

    private void saveBitmapToFirebase(Bitmap bitmap) {
        // Compress bitmap to jpeg
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // get reference to thubnail image file
        StorageReference imageRef = mStorageRef
                .child(mUser.getUid())
                .child(mGame.getId())
                .child(Db.THUMB)
                .child("thumb.jpg");

        //uploading the jpeg image
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mListener.showSnackbar(R.string.upload_failed);
                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mListener.showSnackbar(R.string.upload_failed);
                Log.e(TAG, "Image upload failed -> ", e);
                Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });
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
        void onGameNameChanged(String name);
        void onEditButtonPressed(Game game);
        void showSnackbar(int stringResourceId);
    }


}
