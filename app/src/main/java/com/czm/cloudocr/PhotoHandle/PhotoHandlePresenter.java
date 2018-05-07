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
//        try {
//            InputStream is = mContext.getContentResolver().openInputStream(uri);
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            File file = new File(Environment.getExternalStorageDirectory(),
//                    "account" + RealmOperationHelper.getInstance(MyApp.REALM_INSTANCE).queryAll(PhotoResult.class).size() + ".jpg");
//            FileOutputStream out = new FileOutputStream(file);
//            Bitmap mBitmap = SystemUtils.compressImage(bitmap);
//            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            savePic(new PhotoResult(file.toURI().toString(), "识别后的文字"));
//            Log.d("php", "compressPic: " + file.length()/1024 + "kb");
////            sendPic(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
//        RealmOperationHelper.getInstance(MyApp.REALM_INSTANCE).add(result);
//        mPhotoHandleView.showText(result);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }
}
