package com.gameaholix.coinops.toDo;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gameaholix.coinops.BaseDialogFragment;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentToDoAddBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModel;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModelFactory;
import com.gameaholix.coinops.utility.PromptUser;

public class ToDoAddEditFragment extends BaseDialogFragment {
    private static final String TAG = ToDoAddEditFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";
    private static final String EXTRA_EDIT_FLAG = "CoinOpsTodoEditFlag";

    private String mGameId;
    private String mItemId;
    private boolean mEdit;

    private ToDoItemViewModel mViewModel;
    private LiveData<ToDoItem> mItemLiveData;
    private ToDoItem mToDoItem;

    private Context mContext;
    private FragmentToDoAddBinding mBind;
    private OnFragmentInteractionListener mListener;


    /**
     * Required empty public constructor
     */
    public ToDoAddEditFragment() {
    }

    /**
     * Static factory method used to instantiate a fragment to add a new ToDoItem
     * @param gameId the ID of the parent Game that owns this ToDoItem
     * @return the fragment instance
     */
    public static ToDoAddEditFragment newAddInstance(String gameId) {
        Bundle args = new Bundle();
        ToDoAddEditFragment fragment = new ToDoAddEditFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        args.putBoolean(EXTRA_EDIT_FLAG, false);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Static factory method used to instantiate a fragement to edit an existing ToDoItem
     * @param itemId the ID of the existing ToDoItem
     * @return the fragment instance
     */
    public static ToDoAddEditFragment newEditInstance(String itemId) {
        Bundle args = new Bundle();
        ToDoAddEditFragment fragment = new ToDoAddEditFragment();
        args.putString(EXTRA_TODO_ID, itemId);
        args.putBoolean(EXTRA_EDIT_FLAG, true);
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

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                if (getArguments().containsKey(EXTRA_GAME_ID)) {
                    // we are adding a new ToDoItem
                    mGameId = getArguments().getString(EXTRA_GAME_ID);
                } else if (getArguments().containsKey(EXTRA_TODO_ID)) {
                    // we are editing an existing ToDoItem
                    mItemId = getArguments().getString(EXTRA_TODO_ID);
                }
                mEdit = getArguments().getBoolean(EXTRA_EDIT_FLAG);
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mItemId = savedInstanceState.getString(EXTRA_TODO_ID);
            mEdit = savedInstanceState.getBoolean(EXTRA_EDIT_FLAG);
        }

        // If we are editing, this should get the existing view model, and if we are adding, this
        // should create a new view model (mItemId will be null).
        mViewModel = ViewModelProviders
                .of(getActivity(), new ToDoItemViewModelFactory(mItemId))
                .get(ToDoItemViewModel.class);

        // if this is a brand new fragment instance, clear ViewModel's LiveData copy
        if (savedInstanceState == null) mViewModel.clearItemCopyLiveData();

        // get a duplicate LiveData to make changes to, this way we can maintain state of those
        // changes, and also easily revert any unsaved changes.
        mItemLiveData = mViewModel.getItemCopyLiveData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getShowsDialog()) {
            if (!mEdit) getDialog().setTitle(R.string.add_to_do_title);
        }

        // Inflate the layout for this
        mBind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_to_do_add, container, false);

        mBind.setLifecycleOwner(getActivity());
        mBind.setItem(mItemLiveData);

        if (getActivity() != null) {
            mItemLiveData.observe(getActivity(), new Observer<ToDoItem>() {
                @Override
                public void onChanged(@Nullable ToDoItem toDoItem) {
                    if (toDoItem != null) {
                        mToDoItem = toDoItem;
                        mGameId = mToDoItem.getParentId();

                        // TODO: figure out how to do this with xml
                        RadioButton priorityButton =
                                (RadioButton) mBind.rgPriority.getChildAt(toDoItem.getPriority());
                        priorityButton.setChecked(true);
                    }
                }
            });
        }

        // Setup EditTexts
        mBind.etTodoName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
        mBind.etTodoDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                    return true;
                }
                return false;
            }
        });

        // Setup RadioGroup
        mBind.rgPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedButton = radioGroup.findViewById(checkedId);
                if (mToDoItem != null) {
                    mToDoItem.setPriority(radioGroup.indexOfChild(checkedButton));
                }
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
                    mListener.onToDoAddEditCompletedOrCancelled();
                }
            }
        });

        if (mEdit) {
            mBind.btnSave.setText(R.string.save_changes);
        }
        mBind.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToDoItem.setName(mBind.etTodoName.getText().toString().trim());
                mToDoItem.setDescription(mBind.etTodoDescription.getText().toString().trim());
                addUpdateItem();
            }
        });

        return mBind.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save text input to ViewModel when configuration change occurs.
        mToDoItem.setName(mBind.etTodoName.getText().toString());
        mToDoItem.setDescription(mBind.etTodoDescription.getText().toString());

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putString(EXTRA_TODO_ID, mItemId);
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
     * Add a new or update an existing ToDoItem instance to data storage through ViewModel
     */
    private void addUpdateItem() {
        if (TextUtils.isEmpty(mToDoItem.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            Log.d(TAG, "Failed to add item! Name field was blank or invalid.");
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
                mListener.onToDoAddEditCompletedOrCancelled();
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
        void onToDoAddEditCompletedOrCancelled();
    }
}
