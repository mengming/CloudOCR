package com.czm.cloudocr.TextResult;

import android.content.ContentValues;
import android.content.Context;

import com.czm.cloudocr.model.PhotoResult;
import com.google.gson.JsonObject;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TextResultPresenter implements TextResultContract.Presenter {

    private TextResultContract.View mTextResultView;
    private Context mContext;

    public TextResultPresenter(TextResultContract.View textResultView, Context context) {
        mTextResultView = textResultView;
        mContext = context;
        mTextResultView.setPresenter(this);
    }

    @Override
    public void updateText(String text, int id) {
        mTextResultView.waiting();
        ContentValues values = new ContentValues();
        values.put("text", text);
        DataSupport.update(PhotoResult.class, values, id);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("text", text);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        Request request = new Request.Builder()
                .url("www.baidu.com")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mTextResultView.updated();
            }
        });
    }
}
