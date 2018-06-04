package com.czm.cloudocr.TextResult;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.model.WordResult;
import com.czm.cloudocr.util.MyConstConfig;
import com.google.gson.Gson;
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

import static android.content.Context.MODE_PRIVATE;


public class TextResultPresenter implements TextResultContract.Presenter {

    private static final String TAG = "TextResultPresenter";
    private TextResultContract.View mTextResultView;
    private Context mContext;

    private OkHttpClient mClient;

    public TextResultPresenter(TextResultContract.View textResultView, Context context) {
        mTextResultView = textResultView;
        mContext = context;
        mTextResultView.setPresenter(this);
    }

    @Override
    public void updateText(final String text, final String id) {
        mTextResultView.waiting();
        if (mContext.getSharedPreferences("settings", MODE_PRIVATE).getBoolean("cloud", true)) {
            JsonArray array = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("id", id);
            object.addProperty("text", text);
            array.add(object);
            mClient = new OkHttpClient();
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("UText", array.toString());
            Request request = new Request.Builder()
                    .url(MyConstConfig.SERVER_URL + "TxtUpdate")
                    .post(builder.build())
                    .build();
            Call call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mTextResultView.netError();
                    ContentValues values = new ContentValues();
                    values.put("text", text);
                    values.put("isCloud", 0);
                    int localId = DataSupport.where("remoteId = ?", id).find(PhotoResult.class).get(0).getId();
                    DataSupport.update(PhotoResult.class, values, localId);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "onResponse: " + response.body().string());
                    mTextResultView.updated();
                    ContentValues values = new ContentValues();
                    values.put("text", text);
                    values.put("isCloud", 1);
                    int localId = DataSupport.where("remoteId = ?", id).find(PhotoResult.class).get(0).getId();
                    DataSupport.update(PhotoResult.class, values, localId);
                }
            });
        } else {
            ContentValues values = new ContentValues();
            values.put("text", text);
            values.put("isCloud", 0);
            int localId = DataSupport.where("remoteId = ?", id).find(PhotoResult.class).get(0).getId();
            DataSupport.update(PhotoResult.class, values, localId);
            mTextResultView.netError();
        }
    }

    @Override
    public void searchWord(String text) {
        mClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("text", text.replaceAll("\n", " "))
                .addFormDataPart("showapi_appid", MyConstConfig.SEARCH_WORD_APPID)
                .addFormDataPart("showapi_sign", MyConstConfig.SEARCH_WORD_SIGN);
        Request request = new Request.Builder()
                .url(MyConstConfig.SEARCH_WORD_URL)
                .post(builder.build())
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "onResponse: " + res);
                Gson gson = new Gson();
                WordResult result = gson.fromJson(res, WordResult.class);
                mTextResultView.refreshWords(result.getShowapi_res_body().getList());
            }
        });
    }
}
