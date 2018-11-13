package com.gameaholix.coinops.game;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.utility.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayImageFragment extends Fragment {
//    private static final String TAG = DisplayImageFragment.class.getSimpleName();
    private static final String EXTRA_IMAGE_PATH = "CoinOpsImagePath";

    private Context mContext;
    private String mImagePath;

    public DisplayImageFragment() {
        // Required empty public constructor
    }

    public static DisplayImageFragment newInstance(String imagePath) {
        DisplayImageFragment fragment = new DisplayImageFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mImagePath = getArguments().getString(EXTRA_IMAGE_PATH);
            }
        } else {
            mImagePath = savedInstanceState.getString(EXTRA_IMAGE_PATH);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_display_image, container, false);

        ImageView imageView = rootView.findViewById(R.id.iv_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        GlideApp.with(mContext)
                .load(storageRef.child(mImagePath))
                .into(imageView);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_IMAGE_PATH, mImagePath);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
