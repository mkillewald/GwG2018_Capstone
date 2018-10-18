package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.HintSpinnerAdapter;
import com.gameaholix.coinops.databinding.FragmentAddInventoryBinding;

public class AddInventoryFragment extends Fragment {
    private static final String TAG = AddInventoryFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.inventory.InventoryItem";

    private Context mContext;
    private InventoryItem mNewItem;
    private OnFragmentInteractionListener mListener;

    public AddInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddInventoryBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_inventory, container, false);
        final View rootView = bind.getRoot();

        if (savedInstanceState == null) {
            mNewItem = new InventoryItem();
        } else {
            mNewItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
        }

        // Setup EditText
        bind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    // save text input
                    mNewItem.setName(textView.getText().toString().trim());

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) textView
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        bind.etAddInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_add_inventory_name && !hasFocus) {

                    // save text input
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        mNewItem.setName(editText.getText().toString().trim());
                    }

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) view
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        
        bind.etAddInventoryDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    // save text input
                    mNewItem.setDescription(textView.getText().toString().trim());

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) textView
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        bind.etAddInventoryDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_add_inventory_description && !hasFocus) {

                    // save text input
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        mNewItem.setDescription(editText.getText().toString().trim());
                    }

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) view
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        
        // Setup Spinners
        final HintSpinnerAdapter typeAdapter = new HintSpinnerAdapter(
                mContext, getResources().getStringArray(R.array.inventory_type));
        bind.spinnerInventoryType.setAdapter(typeAdapter);
        bind.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // First item is disabled and used for hint
                if(position > 0){
                    mNewItem.setType(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final HintSpinnerAdapter conditionAdapter = new HintSpinnerAdapter(
                mContext, getResources().getStringArray(R.array.inventory_condition));
        bind.spinnerInventoryCondition.setAdapter(conditionAdapter);
        bind.spinnerInventoryCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // First item is disabled and used for hint
                if(position > 0){
                    mNewItem.setCondition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setup Buttons
        Button addItemButton = bind.btnSave;
        addItemButton.setText(R.string.add_inventory_item);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAddItemButtonPressed(mNewItem);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY_ITEM, mNewItem);
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
        void onAddItemButtonPressed(InventoryItem item);
    }
}
