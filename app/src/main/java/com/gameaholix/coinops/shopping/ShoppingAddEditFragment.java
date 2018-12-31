package com.gameaholix.coinops.shopping;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentItemAddBinding;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.shopping.viewModel.ShoppingItemViewModel;
import com.gameaholix.coinops.shopping.viewModel.ShoppingItemViewModelFactory;
import com.gameaholix.coinops.utility.PromptUser;

public class ShoppingAddEditFragment extends BaseDialogFragment {
    private static final String TAG = ShoppingAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_ITEM_ID = "CoinOpsItemId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsShoppingEditFlag";

    private String mGameId;
    private String mItemId;
    private boolean mEdit;

    private ShoppingItemViewModel mViewModel;
    private LiveData<Item> mItemLiveData;
    private Item mItem;

    private Context mContext;
    private FragmentItemAddBinding mBind;
    private OnFragmentInteractionListener mListener;


    /**
     * Required empty public constructor
     */
    public ShoppingAddEditFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment to add or update a shopping Item
     * @param editFlag set true for editing an existing Item and false if adding a new Item.
     * @return the fragment instance
     */
    public static ShoppingAddEditFragment newInstance(String gameId, String itemId, boolean editFlag) {
        Bundle args = new Bundle();
        ShoppingAddEditFragment fragment = new ShoppingAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putString(EXTRA_ITEM_ID, itemId);
        args.putBoolean(EXTRA_EDIT_FLAG, editFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (getActivity() == null) { return; }

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
                mItemId = getArguments().getString(EXTRA_ITEM_ID);
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mItemId = savedInstanceState.getString(EXTRA_ITEM_ID);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        mViewModel = ViewModelProviders
                .of(this, new ShoppingItemViewModelFactory(mGameId, mItemId))
                .get(ShoppingItemViewModel.class);

        mItemLiveData = mViewModel.getItemLiveData();
        mItemLiveData.observe(getActivity(), new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if (item != null) {
                    mItem = item;
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            if (mEdit) {
                getDialog().setTitle(R.string.edit_shopping_title);
            } else {
                getDialog().setTitle(R.string.add_shopping_title);
            }
        }

        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_item_add, container, false);

        mBind.setLifecycleOwner(this);
        mBind.setItem(mItemLiveData);

        // Setup EditText
        mBind.etEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Verify input and hide keyboard if IME_ACTION_DONE
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });

        // Setup Buttons
        mBind.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getShowsDialog()) {
                    getDialog().dismiss();
                } else {
                    mListener.onShoppingAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) {
            mBind.btnSave.setText(R.string.save_changes);
            mBind.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getShowsDialog()) getDialog().dismiss();
                    showDeleteAlert();
                    mListener.onShoppingAddEditCompletedOrCancelled();
                }
            });
        } else {
            mBind.btnSave.setText(R.string.add_item);
            mBind.btnDelete.setVisibility(View.GONE);
        }
        mBind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setName(mBind.etEntry.getText().toString().trim());
                addUpdateItem();
            }
        });

        return mBind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save text input to ViewModel when configuration change occurs.
        mItem.setName(mBind.etEntry.getText().toString());

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

    private void addUpdateItem() {
        if (TextUtils.isEmpty(mItem.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            Log.d(TAG, "Failed to add item! Name field was blank.");
            return;
        }

        boolean resultOk;
        if (mEdit) {
            resultOk = mViewModel.update();
        } else {
            resultOk = mViewModel.add();
        }

        if (resultOk) {
            if (getShowsDialog()) {
                getDialog().dismiss();
            } else {
                mListener.onShoppingAddEditCompletedOrCancelled();
            }
        } else {
            Log.d(TAG, "The add or edit operation has failed!");
        }
    }

    private void showDeleteAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle(R.string.really_delete_item)
                .setMessage(R.string.item_will_be_deleted)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getShowsDialog()) {
                            dialogInterface.dismiss();
                        } else {
                            mListener.onShoppingAddEditCompletedOrCancelled();
                        }
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mViewModel.delete();
                        if (getShowsDialog()) {
                            dialogInterface.dismiss();
                        } else {
                            mListener.onShoppingAddEditCompletedOrCancelled();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onShoppingAddEditCompletedOrCancelled();
    }
}
