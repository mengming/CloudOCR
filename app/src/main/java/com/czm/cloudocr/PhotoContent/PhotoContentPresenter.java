package com.czm.cloudocr.PhotoContent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.MyConstConfig;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoContentPresenter implements PhotoContentContract.Presenter {

    private Context mContext;
    private PhotoContentContract.View mContentView;

    public PhotoContentPresenter(Context context, PhotoContentContract.View contentView) {
        mContext = context;
        mContentView = contentView;
        mContentView.setPresenter(this);
    }

    @Override
    public void download(String url) {
        mContentView.waiting();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mContentView.error();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                byte[] buf = new byte[2048];
                int len = 0;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "search" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                while((len = is.read(buf)) != -1){
                    fos.write(buf,0,len);
                }
                fos.flush();
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                mContentView.success();
            }
        });
    }
}
