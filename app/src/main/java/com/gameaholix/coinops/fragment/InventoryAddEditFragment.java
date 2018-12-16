package com.gameaholix.coinops.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentInventoryAddBinding;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.PromptUser;
import com.gameaholix.coinops.viewModel.InventoryItemViewModel;
import com.gameaholix.coinops.viewModel.InventoryItemViewModelFactory;

public class InventoryAddEditFragment extends BaseDialogFragment {
    private static final String TAG = InventoryAddEditFragment.class.getSimpleName();
    private static final String EXTRA_IVENTORY_ID = "CoinOpsInventoryId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsInventoryEditFlag";

    private Context mContext;
    private String mItemId;
    private InventoryItem mItem;
    private InventoryItemViewModel mViewModel;
    private OnFragmentInteractionListener mListener;
    private boolean mEdit = false;

    public InventoryAddEditFragment() {
        // Required empty public constructor
    }

    public static InventoryAddEditFragment newEditInstance(String itemId) {
        Bundle args = new Bundle();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        args.putString(EXTRA_IVENTORY_ID, itemId);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mItemId = getArguments().getString(EXTRA_IVENTORY_ID);
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mItemId = savedInstanceState.getString(EXTRA_IVENTORY_ID);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        if (getActivity() != null) {
            // If we are editing, this should get the existing view model that was created by
            // InventoryDetailFragment with InventoryDetailActivity as its lifecycle owner.

            // If we are adding, this should create a new view model with InventoryListActivity as
            // its lifecycle owner.
            mViewModel = ViewModelProviders
                    .of(getActivity(), new InventoryItemViewModelFactory(mItemId))
                    .get(InventoryItemViewModel.class);

            LiveData<InventoryItem> itemLiveData = mViewModel.getItemLiveData();

            if (itemLiveData != null) {
                mItem = itemLiveData.getValue();
            } else {
                mItem = new InventoryItem();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            getDialog().setTitle(R.string.add_inventory_title);
        }

        // Inflate the layout for this fragment
        final FragmentInventoryAddBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_inventory_add, container, false);
        final View rootView = bind.getRoot();

        // Setup EditTexts
        if (mEdit) bind.etAddInventoryName.setText(mItem.getName());
        bind.etAddInventoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        bind.etAddInventoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        if (mEdit) bind.etAddInventoryDescription.setText(mItem.getDescription());
        bind.etAddInventoryDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String input = textView.getText().toString().trim();
                    if (!textInputIsValid(input)) {
                        textView.setText(mItem.getDescription());
                    }
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        bind.etAddInventoryDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_add_inventory_description && !hasFocus) {
                    if (view instanceof EditText) {
                        String input = ((EditText) view).getText().toString().trim();
                        if (!textInputIsValid(input)) {
                            ((EditText) view).setText(mItem.getDescription());
                        }
                    }
                }
            }
        });

        // Setup Spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryType.setAdapter(typeAdapter);
        if (mEdit) bind.spinnerInventoryType.setSelection(mItem.getType());

        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.inventory_condition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerInventoryCondition.setAdapter(conditionAdapter);
        if (mEdit) bind.spinnerInventoryCondition.setSelection(mItem.getCondition());

        // Setup Buttons
        bind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onInventoryAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) bind.btnSave.setText(R.string.save_changes);
        bind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(bind.etAddInventoryName.getText())) {
                    PromptUser.displayAlert(mContext,
                            R.string.error_add_inventory_failed,
                            R.string.error_name_empty);
                    Log.d(TAG, "Failed to add part! Name field was blank.");
                    return;
                }

                if (getShowsDialog()) getDialog().dismiss();

                String input = bind.etAddInventoryName.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mItem.setName(input);
                } else {
                    bind.etAddInventoryName.setText(mItem.getName());
                }

                input = bind.etAddInventoryDescription.getText().toString().trim();
                if (textInputIsValid(input)) {
                    mItem.setDescription(input);
                } else {
                    bind.etAddInventoryDescription.setText(mItem.getDescription());
                }

                mItem.setType(bind.spinnerInventoryType.getSelectedItemPosition());
                mItem.setCondition(bind.spinnerInventoryCondition.getSelectedItemPosition());

                if (mEdit) {
                    mViewModel.edit();
                } else {
                    mViewModel.add(mItem);
                }

                mListener.onInventoryAddEditCompletedOrCancelled();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_IVENTORY_ID, mItemId);
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
        void onInventoryAddEditCompletedOrCancelled();
    }
}
