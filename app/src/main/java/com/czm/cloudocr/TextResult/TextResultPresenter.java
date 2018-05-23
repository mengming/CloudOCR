package com.czm.cloudocr.TextResult;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.czm.cloudocr.model.PhotoResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TextResultPresenter implements TextResultContract.Presenter {

    private static final String TAG = "TextResultPresenter";
    private TextResultContract.View mTextResultView;
    private Context mContext;

    public TextResultPresenter(TextResultContract.View textResultView, Context context) {
        mTextResultView = textResultView;
        mContext = context;
        mTextResultView.setPresenter(this);
    }

    @Override
    public void updateText(final String text, final int id) {
        mTextResultView.waiting();
        JsonArray array = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("id", String.valueOf(id));
        object.addProperty("text", text);
        array.add(object);
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UText", array.toString());
        Request request = new Request.Builder()
                .url("http://192.168.199.234:8080/TxtUpdate")
                .post(builder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mTextResultView.netError();
                ContentValues values = new ContentValues();
                values.put("text", text);
                values.put("isCloud", false);
                DataSupport.update(PhotoResult.class, values, id);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mTextResultView.updated();
                ContentValues values = new ContentValues();
                values.put("text", text);
                values.put("isCloud", true);
                DataSupport.update(PhotoResult.class, values, id);
            }
        });
    }
}
