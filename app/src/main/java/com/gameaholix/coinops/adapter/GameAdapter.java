package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.GameListItemBinding;
import com.gameaholix.coinops.model.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameAdapterViewHolder> {
    private List<Game> mGames;
    private final GameAdapterOnClickHandler mClickHandler;

    public interface GameAdapterOnClickHandler {
        void onClick(Game game);
    }

    public GameAdapter (GameAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class GameAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final GameListItemBinding mBinding;

        GameAdapterViewHolder(GameListItemBinding gameListItemBinding) {
            super(gameListItemBinding.getRoot());
            mBinding = gameListItemBinding;
            mBinding.tvGameName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            Game game = mGames.get(adapterPosition);
            mClickHandler.onClick(game);
        }
    }

    @NonNull
    @Override
    public GameAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GameListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.game_list_item, parent, false);

        return new GameAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapterViewHolder holder, int position) {
        Game game = mGames.get(position);

        holder.mBinding.setGame(game);
        holder.mBinding.tvGameName.setText(game.getName());
    }

    @Override
    public int getItemCount() {
        return mGames == null ? 0 : mGames.size();
    }

    public void setGames(List<Game> games) { mGames = games; }
}
