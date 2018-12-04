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
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.model.ListRow;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameAdapterViewHolder> {
    private List<ListRow> mGames;
    private final GameAdapterOnClickHandler mClickHandler;

    public interface GameAdapterOnClickHandler {
        void onClick(ListRow game);
    }

    public GameAdapter (GameAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class GameAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemBinding mBinding;

        GameAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            ListRow game = mGames.get(adapterPosition);
            mClickHandler.onClick(game);
        }
    }

    @NonNull
    @Override
    public GameAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new GameAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapterViewHolder holder, int position) {
        ListRow game = mGames.get(position);

        holder.mBinding.tvName.setText(game.getName());
        holder.mBinding.tvName.setMaxLines(1);
        holder.mBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getItemCount() {
        return mGames == null ? 0 : mGames.size();
    }

    public void setGames(List<ListRow> games) {
        mGames = games;
        notifyDataSetChanged();
    }

}
