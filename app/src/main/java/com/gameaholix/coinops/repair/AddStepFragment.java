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
import com.gameaholix.coinops.databinding.FragmentAddStepBinding;
import com.gameaholix.coinops.model.RepairStep;

public class AddStepFragment extends Fragment {
//    private static final String TAG = AddStepFragment.class.getSimpleName();
    private static final String EXTRA_STEP = "com.gameaholix.coinops.model.RepairStep";

    private RepairStep mNewStep;
    private OnFragmentInteractionListener mListener;

    public AddStepFragment() {
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
        final FragmentAddStepBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_step, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            mNewStep = new RepairStep();
        } else {
            mNewStep = savedInstanceState.getParcelable(EXTRA_STEP);
        }

        //Setup EditText
//        bind.etAddStepEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    mNewStep.setEntry(textView.getText().toString().trim());
//                    hideKeyboard(textView);
//                }
//                return false;
//            }
//        });
//        bind.etAddStepEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (view.getId() == R.id.et_add_game_name && !hasFocus) {
//                    if (view instanceof EditText) {
//                        EditText editText = (EditText) view;
//                        mNewStep.setEntry(editText.getText().toString().trim());
//                        hideKeyboard(editText);
//                    }
//                }
//            }
//        });

        // Setup Button
        Button addStepButton = bind.btnSave;
        addStepButton.setText(R.string.add_repair_entry);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mNewStep.setEntry(bind.etAddStepEntry.getText().toString().trim());
                    mListener.onAddButtonPressed(mNewStep);
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

        outState.putParcelable(EXTRA_STEP, mNewStep);
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
        void onAddButtonPressed(RepairStep step);
    }
}
