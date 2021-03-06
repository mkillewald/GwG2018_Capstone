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
import com.gameaholix.coinops.model.ListRow;

import java.util.List;

public class ListRowAdapter extends RecyclerView.Adapter<ListRowAdapter.ListRowAdapterViewHolder> {
    private List<ListRow> mList;
    private final ListAdapterOnClickHandler mClickHandler;

    public interface ListAdapterOnClickHandler {
        void onClick(ListRow row);
    }

    public ListRowAdapter(ListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ListRowAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemBinding mBinding;

        ListRowAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            ListRow row = mList.get(adapterPosition);
            mClickHandler.onClick(row);
        }
    }

    @NonNull
    @Override
    public ListRowAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new ListRowAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRowAdapterViewHolder holder, int position) {
        ListRow row = mList.get(position);

        holder.mBinding.tvName.setText(row.getName());
        holder.mBinding.tvName.setMaxLines(1);
        holder.mBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setList(List<ListRow> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
