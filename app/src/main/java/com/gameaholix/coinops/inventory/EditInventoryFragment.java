package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddGameBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class EditInventoryFragment extends Fragment {
    private static final String TAG = EditInventoryFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.inventory.InventoryItem";
    private static final String EXTRA_VALUES = "CoinOpsInventoryValuesToUpdate";

    private Context mContext;
    private InventoryItem mItem;
    private String mNewName;
    private Bundle mValuesBundle;
    private FirebaseAuth mFirebaseAuth;
    private OnFragmentInteractionListener mListener;

    public EditInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddGameBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_inventory, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();

            if (intent != null) {
                mItem = intent.getParcelableExtra(EXTRA_INVENTORY_ITEM);
            }

            mValuesBundle = new Bundle();
        } else {
            mItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
            mValuesBundle = savedInstanceState.getBundle(EXTRA_VALUES);
        }

        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in
            final String uid = user.getUid();
            final String id = mItem.getId();
            // TODO: pick up here in the morning
        } else {
            // user is not signed in
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mItem);
        outState.putBundle(EXTRA_VALUES, mValuesBundle);
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
        void onEditButtonPressed(Map<String, Object> valuesToUpdate);
    }
}
