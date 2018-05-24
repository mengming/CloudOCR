package com.czm.cloudocr.PhotoSelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.czm.cloudocr.GlideApp;
import com.czm.cloudocr.MainActivity;
import com.czm.cloudocr.PhotoHandle.PhotoHandleActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

import java.io.File;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.czm.cloudocr.util.SystemUtils.dip2px;

/**
 * Created by Phelps on 2018/3/25.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> mUrls;
    private PhotoSelectContract.Presenter mPresenter;
    private MainActivity mActivity;

    public PhotoAdapter(Context context, Activity activity, List<String> urls, PhotoSelectContract.Presenter presenter) {
        mContext = context;
        mUrls = urls;
        mPresenter = presenter;
        mActivity = (MainActivity) activity;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        DisplayMetrics outMetrics = mContext.getApplicationContext().getResources().getDisplayMetrics();
        int width = (outMetrics.widthPixels - dip2px(mContext,64))/3;

        GlideApp.with(mContext)
                .asBitmap()
                .override(width, dip2px(mContext,100))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(mUrls.get(position))
                .transition(withCrossFade())
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoHandleActivity.class);
                Uri uri = Uri.fromFile(new File(mUrls.get(position)));
                PhotoResult result = mPresenter.checkPhoto(uri);
                intent.putExtra("advanced", mActivity.isAdvanced());
                intent.putExtra("flag", result != null);
                intent.putExtra("photo_result", result);
                intent.putExtra("photo", uri.toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_item_photo);
        }

    }

}
