package com.czm.cloudocr.PhotoSelect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.czm.cloudocr.R;
import com.czm.cloudocr.util.GlideApp;

import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Created by Phelps on 2018/3/25.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> mUrls;

    public PhotoAdapter(Context context, List<String> list){
        mContext = context;
        mUrls = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        outMetrics =  mContext.getApplicationContext().getResources().getDisplayMetrics();
        int width = (outMetrics.widthPixels-dip2px(mContext,64))/3;

        GlideApp.with(mContext)
                .asBitmap()
                .override(width,dip2px(mContext,100))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(mUrls.get(position))
                .transition(withCrossFade())
                .into(holder.mImageView);
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

    public static int[] getImageWidthHeight(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
