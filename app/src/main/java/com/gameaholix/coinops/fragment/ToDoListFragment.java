package com.gameaholix.coinops.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.ListRowAdapter;
import com.gameaholix.coinops.databinding.FragmentListBinding;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.viewModel.ToDoListViewModel;
import com.gameaholix.coinops.viewModel.ToDoListViewModelFactory;

import java.util.List;

// TODO: need to figure out a way to order the to do list by priority
// The todo_list (game and global) in firebase will need to be modified to hold name and priority

public class ToDoListFragment extends Fragment implements ListRowAdapter.ListAdapterOnClickHandler {
//    private static final String TAG = ToDoListFragment.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private String mGameId;
    private ListRowAdapter mToDoAdapter;
    private OnFragmentInteractionListener mListener;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    public static ToDoListFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        ToDoListFragment fragment = new ToDoListFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            } else {
                mGameId = null;
            }
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate view for this fragment
        final FragmentListBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false);
        final View rootView = bind.getRoot();


        mToDoAdapter = new ListRowAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bind.rvList.setLayoutManager(linearLayoutManager);
        bind.rvList.setAdapter(mToDoAdapter);
        bind.rvList.setHasFixedSize(true);

        if (TextUtils.isEmpty(mGameId)) {
            // Global list, hide FAB
            bind.fab.setEnabled(false);
            bind.fab.setClickable(false);
            bind.fab.setAlpha(0.0f);
        } else {
            // Game specific list
            bind.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onToDoFabPressed();
                }
            });
        }

        // read list of to do items
        if (getActivity() != null) {
            ToDoListViewModel toDoListViewModel = ViewModelProviders
                    .of(getActivity(), new ToDoListViewModelFactory(mGameId))
                    .get(ToDoListViewModel.class);
            LiveData<List<ListRow>> toDoListLiveData = toDoListViewModel.getListLiveData();
            toDoListLiveData.observe(getActivity(), new Observer<List<ListRow>>() {
                @Override
                public void onChanged(@Nullable List<ListRow> list) {
                    if (list != null) {
                        mToDoAdapter.setList(list);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
    }

    @Override
    public void onClick(ListRow row) {
        if (mListener != null) {
            mListener.onToDoItemSelected(row);
        }
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
        void onToDoItemSelected(ListRow row);
        void onToDoFabPressed();
    }
}
