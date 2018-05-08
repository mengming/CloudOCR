package com.czm.cloudocr.PhotoHandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.HttpUtils;
import com.czm.cloudocr.util.SystemUtils;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoHandlePresenter implements PhotoHandleContract.Presenter {

    private PhotoHandleContract.View mPhotoHandleView;
    private Context mContext;

    public PhotoHandlePresenter(PhotoHandleContract.View photoHandleView, Context context) {
        mPhotoHandleView = photoHandleView;
        mContext = context;
        mPhotoHandleView.setPresenter(this);
    }

    @Override
    public void compressPic(Uri uri) {
        try {
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "account" + DataSupport.count(PhotoResult.class) + ".jpg");
            Log.d("php", "compressPic: uri=" + file.toURI().toString());
            Bitmap mBitmap = SystemUtils.compressImage(bitmap, file);
            savePic(new PhotoResult(file.toURI().toString(), "识别后的文字"));
            Log.d("php", "compress:" + DataSupport.count(PhotoResult.class));
            Log.d("php", "compressPic: " + file.length()/1024 + "kb");
//            sendPic(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPic(File file) throws IOException{
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("account", "mengming");
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("www.baidu.com")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
//        PhotoResult result = new PhotoResult();
//        savePic(result);
    }

    @Override
    public void savePic(PhotoResult result) {
        result.saveThrows();
        mPhotoHandleView.showText(result);
    }

}
