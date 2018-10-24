package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddRepairBinding;
import com.gameaholix.coinops.model.RepairLog;

public class AddRepairFragment extends Fragment {
//    private static final String TAG = AddRepairFragment.class.getSimpleName();
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.RepairLog";

    private RepairLog mNewLog;
    private OnFragmentInteractionListener mListener;

    public AddRepairFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddRepairBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_repair, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            mNewLog = new RepairLog();
        } else {
            mNewLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        //Setup EditText
//        bind.etRepairLogDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    mNewLog.setDescription(textView.getText().toString().trim());
//                    hideKeyboard(textView);
//                }
//                return false;
//            }
//        });
//        bind.etRepairLogDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (view.getId() == R.id.et_add_game_name && !hasFocus) {
//                    if (view instanceof EditText) {
//                        EditText editText = (EditText) view;
//                        mNewLog.setDescription(editText.getText().toString().trim());
//                        hideKeyboard(editText);
//                    }
//                }
//            }
//        });

        // Setup Button
        Button addLogButton = bind.btnSave;
        addLogButton.setText(R.string.add_repair_entry);
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mNewLog.setDescription(bind.etRepairLogDescription.getText().toString().trim());
                    mListener.onAddButtonPressed(mNewLog);
                }
            }
        });

        return rootView;
    }

//    private void hideKeyboard(TextView view) {
//        InputMethodManager imm = (InputMethodManager) view
//                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_REPAIR, mNewLog);
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
        void onAddButtonPressed(RepairLog log);
    }
}
