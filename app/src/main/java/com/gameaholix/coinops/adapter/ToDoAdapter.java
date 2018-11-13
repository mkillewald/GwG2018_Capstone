package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ListItemBinding;
import com.gameaholix.coinops.model.ToDoItem;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoAdapterViewHolder> {
    private List<ToDoItem> mToDoItems;
    private final ToDoAdapterOnClickHandler mClickHandler;

    public interface ToDoAdapterOnClickHandler {
        void onClick(ToDoItem toDoItem);
    }

    public ToDoAdapter(ToDoAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ToDoAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemBinding mBinding;

        ToDoAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            ToDoItem toDoItem = mToDoItems.get(adapterPosition);
            mClickHandler.onClick(toDoItem);
        }
    }

    @NonNull
    @Override
    public ToDoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new ToDoAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapterViewHolder holder, int position) {
        ToDoItem toDoItem = mToDoItems.get(position);
        holder.mBinding.tvName.setText(toDoItem.getName());
        holder.mBinding.tvName.setMaxLines(1);
        holder.mBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getItemCount() {
        return mToDoItems == null ? 0 : mToDoItems.size();
    }

    public void setToDoItems(List<ToDoItem> toDoItems) {
        mToDoItems = toDoItems;
    }
}
