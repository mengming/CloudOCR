package com.czm.cloudocr.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.czm.cloudocr.model.HistoryResult;
import com.czm.cloudocr.model.PhotoResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mSettingsView;
    private Context mContext;
    private static final String TAG = "SettingsPresenter";

    public SettingsPresenter(SettingsContract.View settingsView, Context context) {
        mSettingsView = settingsView;
        mContext = context;
        mSettingsView.setPresenter(this);
    }

    @Override
    public void uploadAll() {
        mSettingsView.waiting("正在上传中...");
        List<PhotoResult> results = DataSupport.where("isCloud = ?", "false").find(PhotoResult.class);
        JsonArray array = new JsonArray();
        for (PhotoResult result : results) {
            JsonObject object = new JsonObject();
            object.addProperty("id", result.getRemoteId());
            object.addProperty("text", result.getText());
            array.add(object);
        }
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
                mSettingsView.netError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ContentValues values = new ContentValues();
                values.put("isCloud", true);
                DataSupport.updateAll(PhotoResult.class, values, "isCloud = ?", "false");
                mSettingsView.success();
            }
        });
    }

    @Override
    public void downloadAll(String account) {
        mSettingsView.waiting("正在同步中...");
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", account);
        final Request request = new Request.Builder()
                .url("http://192.168.199.234:8080/imgSynchro")
                .post(builder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mSettingsView.netError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JsonObject bigObj = new JsonParser().parse(response.body().string()).getAsJsonObject();
                int count = bigObj.get("total").getAsInt();
                Gson gson = new Gson();
                for (int i = 0; i < count; i++) {
                    HistoryResult result = gson.fromJson(bigObj.getAsJsonObject(String.valueOf(i)).toString(), HistoryResult.class);
                    Log.d(TAG, "onResponse: " + result.toString());
                }
                mSettingsView.success();
            }
        });
    }

}
