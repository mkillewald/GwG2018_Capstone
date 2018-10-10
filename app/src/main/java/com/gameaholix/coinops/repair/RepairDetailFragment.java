package com.gameaholix.coinops.repair;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentRepairDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RepairDetailFragment extends Fragment {
    private static final String TAG = RepairDetailFragment.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.repair.RepairLog";

    private RepairLog mRepairLog;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private OnFragmentInteractionListener mListener;

    public RepairDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mRepairLog = intent.getParcelableExtra(EXTRA_REPAIR);
            }
        } else {
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentRepairDetailBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repair_detail, container, false);

        final View rootView = binding.getRoot();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();

            // Setup database references

            // Read repair log details

        } else {
            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
