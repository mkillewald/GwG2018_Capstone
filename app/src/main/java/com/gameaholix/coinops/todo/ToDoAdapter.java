package com.gameaholix.coinops.todo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.TodoListItemBinding;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoAdapterViewHolder> {
    private List<ToDoItem> mToDoItems;
    private final Context mContext;
    private final ToDoAdapterOnClickHandler mClickHandler;

    public interface ToDoAdapterOnClickHandler {
        void onClick(ToDoItem toDoItem);
    }

    public ToDoAdapter(Context context, ToDoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class ToDoAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final TodoListItemBinding mBinding;

        ToDoAdapterViewHolder(TodoListItemBinding todoListItemBinding) {
            super(todoListItemBinding.getRoot());
            mBinding = todoListItemBinding;
            mBinding.tvTodoName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            ToDoItem toDoItem = mToDoItems.get(adapterPosition);
            mClickHandler.onClick(toDoItem);
        }
    }

    @Override
    public ToDoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TodoListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.todo_list_item, parent, false);

        ToDoAdapterViewHolder viewHolder = new ToDoAdapterViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ToDoAdapterViewHolder holder, int position) {
        ToDoItem toDoItem = mToDoItems.get(position);

        holder.mBinding.setToDoItem(toDoItem);
        holder.mBinding.tvTodoName.setText(toDoItem.getName());
    }

    @Override
    public int getItemCount() {
        return mToDoItems == null ? 0 : mToDoItems.size();
    }

    public void setToDoItems(List<ToDoItem> toDoItems) {
        mToDoItems = toDoItems;
    }
}
