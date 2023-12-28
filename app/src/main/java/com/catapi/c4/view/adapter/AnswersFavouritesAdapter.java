package com.catapi.c4.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catapi.c4.R;
import com.catapi.c4.data.ListCatResponse;
import com.catapi.c4.data.ListFavouritesResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnswersFavouritesAdapter extends RecyclerView.Adapter<AnswersFavouritesAdapter.ViewHolder> {

    private List<ListFavouritesResponse> data;
    private Context context;
    private OnItemClickListener itemClickListener;

    public AnswersFavouritesAdapter(Context context, List<ListFavouritesResponse> data, OnItemClickListener itemClickListener) {
        this.data = data;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener itemClickListener;
        ImageView image;

        public ViewHolder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCatList);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ListFavouritesResponse data = getItem(getBindingAdapterPosition());
            this.itemClickListener.onPostClick(data);

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public AnswersFavouritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item, parent, false);
        return new ViewHolder(view, this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListFavouritesResponse data = this.data.get(position);
        String imageUrl = data.getImage().getUrl();

        Picasso.get().load(imageUrl).centerCrop().resize(500, 500).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void updateAnswersCats(List<ListFavouritesResponse> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private ListFavouritesResponse getItem(int adapterPosition) {
        return data.get(adapterPosition);
    }

    public interface OnItemClickListener {
        void onPostClick(ListFavouritesResponse data);
    }
}
