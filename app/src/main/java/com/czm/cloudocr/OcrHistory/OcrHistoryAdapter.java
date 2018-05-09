package com.czm.cloudocr.OcrHistory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

import java.util.List;

public class OcrHistoryAdapter extends RecyclerView.Adapter<OcrHistoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private List<PhotoResult> mResults;

    public OcrHistoryAdapter(Context context, List<PhotoResult> results) {
        mContext = context;
        mResults = results;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Log.d("oha", "onBindViewHolder: "+position);
        PhotoResult result = mResults.get(position);
        Glide.with(mContext)
                .load(result.getUri())
                .into(holder.mImageView);
        holder.mTextView.setText(result.getText());
    }

    @Override
    public int getItemCount() {
        Log.d("oha", "getItemCount: ");
        return mResults.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_history_photo);
            mTextView = itemView.findViewById(R.id.item_history_text);
        }
    }
}
