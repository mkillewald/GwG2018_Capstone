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
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryAddBinding;
import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.inventory.viewModel.InventoryItemViewModel;
import com.gameaholix.coinops.inventory.viewModel.InventoryItemViewModelFactory;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.PromptUser;

public class InventoryAddEditFragment extends BaseDialogFragment {
    private static final String TAG = InventoryAddEditFragment.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsInventoryEditFlag";

    private Context mContext;
    private FragmentInventoryAddBinding mBind;
    private InventoryItem mItem;
    private String mItemId;
    private InventoryItemViewModel mViewModel;
    private LiveData<InventoryItem> mItemLiveData;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit;

    /**
     * Required empty public constructor
     */
    public InventoryAddEditFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment to add a new InventoryItem
     * @return the fragment instance
     */
    public static InventoryAddEditFragment newAddInstance() {
        Bundle args = new Bundle();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Static factory method used to instantiate a fragment to edit an existing InventoryItem
     * @param itemId the ID of the existing InventoryItem
     * @return the fragment instance
     */
    public static InventoryAddEditFragment newEditInstance(String itemId) {
        Bundle args = new Bundle();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        args.putString(EXTRA_INVENTORY_ID, itemId);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (getActivity() == null) return;

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_INVENTORY_ID);
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_INVENTORY_ID);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        // If we are editing, this should get the existing view model, and if we are adding, this
        // should create a new view model (mItemId will be null).
        mViewModel = ViewModelProviders
                .of(getActivity(), new InventoryItemViewModelFactory(mItemId))
                .get(InventoryItemViewModel.class);
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

        // Name field cannot be blank, add listeners to validate Name input
        mBind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (!textInputIsValid(input)) {
                        textView.setText(mItem.getName());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        mBind.etAddInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_add_inventory_name && !hasFocus) {
                    if (view instanceof EditText) {
                        String input = ((EditText) view).getText().toString().trim();
                        if (!textInputIsValid(input)) {
                            ((EditText) view).setText(mItem.getName());
                        }
                    }
                }
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
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
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onInventoryAddEditCompletedOrCancelled();
                }
                mViewModel.clearItemCopyLiveData();
            }
        });

        if (mEdit) mBind.btnSave.setText(R.string.save_changes);
        mBind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mBind.etAddInventoryName.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mItem.setName(input);
                }

                mItem.setDescription(mBind.etAddInventoryDescription.getText().toString().trim());
                addUpdateItem();
                mViewModel.clearItemCopyLiveData();
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

        outState.putString(EXTRA_INVENTORY_ID, mItemId);
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
        mViewModel.clearItemCopyLiveData();
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
            if (getShowsDialog()) getDialog().dismiss();
            mListener.onInventoryAddEditCompletedOrCancelled();
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
