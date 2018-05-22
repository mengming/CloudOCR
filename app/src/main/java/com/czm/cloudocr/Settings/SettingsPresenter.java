package com.czm.cloudocr.Settings;

import android.content.Context;

import com.czm.cloudocr.model.PhotoResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public SettingsPresenter(SettingsContract.View settingsView, Context context) {
        mSettingsView = settingsView;
        mContext = context;
        mSettingsView.setPresenter(this);
    }

    @Override
    public void uploadAll() {
        List<PhotoResult> results = DataSupport.findAll(PhotoResult.class);
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

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mSettingsView.uploaded();
            }
        });
    }
}
