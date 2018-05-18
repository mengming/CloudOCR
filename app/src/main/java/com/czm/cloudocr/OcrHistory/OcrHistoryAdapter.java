package com.czm.cloudocr.OcrHistory;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.TextResult.TextResultActivity;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.SystemUtils;

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
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        PhotoResult result = mResults.get(position);
        Glide.with(mContext)
                .load(result.getUri())
                .into(holder.mImageView);
        holder.mTextView.setText(result.getText());
        holder.mDateView.setText(SystemUtils.dateFormat(result.getDate()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TextResultActivity.class);
                intent.putExtra("photo_result", mResults.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        TextView mDateView;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_history_photo);
            mTextView = itemView.findViewById(R.id.item_history_text);
            mDateView = itemView.findViewById(R.id.item_history_date);
        }
    }
}
