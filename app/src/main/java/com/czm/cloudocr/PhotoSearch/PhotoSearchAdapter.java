package com.czm.cloudocr.PhotoSearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.PhotoContent.PhotoContentActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.SearchResult;

import java.util.List;

public class PhotoSearchAdapter extends RecyclerView.Adapter<PhotoSearchAdapter.PhotoSearchHolder> {

    private Context mContext;
    private List<SearchResult> mSearchResults;

    public PhotoSearchAdapter(Context context, List<SearchResult> searchResults) {
        mContext = context;
        mSearchResults = searchResults;
    }

    @NonNull
    @Override
    public PhotoSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new PhotoSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoSearchHolder holder, final int position) {
        Glide.with(mContext)
                .load(mSearchResults.get(position).getThumbnailURL())
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoContentActivity.class);
                intent.putExtra("search_result", mSearchResults.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSearchResults.size();
    }

    public class PhotoSearchHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        public PhotoSearchHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_item_photo);
        }
    }
}
