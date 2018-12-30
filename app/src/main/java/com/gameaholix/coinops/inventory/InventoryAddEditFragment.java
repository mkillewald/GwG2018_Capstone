package com.gameaholix.coinops.inventory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryAddBinding;
import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.inventory.viewModel.InventoryItemViewModel;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.PromptUser;

public class InventoryAddEditFragment extends BaseDialogFragment {
    private static final String TAG = InventoryAddEditFragment.class.getSimpleName();
    private static final String EXTRA_EDIT_FLAG = "CoinOpsInventoryEditFlag";

    private boolean mEdit;

    private InventoryItemViewModel mViewModel;
    private LiveData<InventoryItem> mItemLiveData;
    private InventoryItem mItem;

    private Context mContext;
    private FragmentInventoryAddBinding mBind;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public InventoryAddEditFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment to add a new InventoryItem
     * @param editFlag set true for editing an existing and false if adding a new InventoryItem
     * @return the fragment instance
     */
    public static InventoryAddEditFragment newInstance(boolean editFlag) {
        Bundle args = new Bundle();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        args.putBoolean(EXTRA_EDIT_FLAG, editFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (getActivity() == null) { return; }

        // this will cause the Activity's onPrepareOptionsMenu() method to be called
        getActivity().invalidateOptionsMenu();

        // If we are editing, this should get the existing view model, and if we are adding, this
        // should create a new view model (mItemId will be null at creation).
        mViewModel = ViewModelProviders
                .of(getActivity())
                .get(InventoryItemViewModel.class);

        if (savedInstanceState == null) {
            // if this is a brand new fragment instance, clear ViewModel's LiveData copy
            mViewModel.clearItemCopyLiveData();

            if (getArguments() != null) {
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        // get a duplicate LiveData to make changes to, this way we can maintain state of those
        // changes, and also easily revert any unsaved changes.
        mItemLiveData = mViewModel.getItemCopyLiveData();
        mItemLiveData.observe(getActivity(), new Observer<InventoryItem>() {
            @Override
            public void onChanged(@Nullable InventoryItem inventoryItem) {
                mItem = inventoryItem;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog() && !mEdit) {
            getDialog().setTitle(R.string.add_inventory_title);
        }

        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_add, container, false);

        mBind.setLifecycleOwner(getActivity());
        mBind.setItem(mItemLiveData);

        // Setup EditText
        mBind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        mBind.etAddInventoryDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });

        // Setup Spinners
        final ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerInventoryType.setAdapter(typeAdapter);
        mBind.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mItem.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBind.spinnerInventoryCondition.setAdapter(conditionAdapter);
        mBind.spinnerInventoryCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mItem.setCondition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Setup Buttons
        mBind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.clearItemCopyLiveData();
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onInventoryAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) mBind.btnSave.setText(R.string.save_changes);
        mBind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setName(mBind.etAddInventoryName.getText().toString().trim());
                mItem.setDescription(mBind.etAddInventoryDescription.getText().toString().trim());
                addUpdateItem();
            }
        });

        return mBind.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save text input to ViewModel when configuration change occurs.
        mItem.setName(mBind.etAddInventoryName.getText().toString());
        mItem.setDescription(mBind.etAddInventoryDescription.getText().toString());

        outState.putBoolean(EXTRA_EDIT_FLAG, mEdit);
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
     * Add a new or update an existing InventoryItem instance to data storage through ViewModel
     */
    private void addUpdateItem() {
        if (TextUtils.isEmpty(mItem.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_inventory_failed,
                    R.string.error_name_empty);
            Log.d(TAG, "Failed to add part! Name field was blank.");
            return;
        }

        boolean resultOk;
        if (mEdit) {
            resultOk = mViewModel.update();
        } else {
            resultOk = mViewModel.add();
        }

        if (resultOk) {
            mViewModel.clearItemCopyLiveData();
            if (getShowsDialog()) {
                getDialog().dismiss();
            } else {
                mListener.onInventoryAddEditCompletedOrCancelled();
            }
        } else {
            Log.d(TAG, "The add or edit operation has failed!");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onInventoryAddEditCompletedOrCancelled();
    }
}
