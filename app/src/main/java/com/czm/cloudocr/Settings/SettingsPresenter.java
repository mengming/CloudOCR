package com.czm.cloudocr.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.czm.cloudocr.model.HistoryResult;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.MyConstConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private OkHttpClient client = new OkHttpClient();
    private List<HistoryResult> mHistoryResults;
    private int downloadCount = 0;

    public SettingsPresenter(SettingsContract.View settingsView, Context context) {
        mSettingsView = settingsView;
        mContext = context;
        mSettingsView.setPresenter(this);
    }

    @Override
    public void uploadAll() {
        mSettingsView.waiting("正在上传中...");
        List<PhotoResult> results = DataSupport.where("isCloud = ?", "false").find(PhotoResult.class);
        if (results.size() == 0) {
            mSettingsView.success();
            return;
        }
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
                .url(MyConstConfig.SERVER_URL + "TxtUpdate")
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
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", account);
        final Request request = new Request.Builder()
                .url(MyConstConfig.SERVER_URL + "imgSynchro")
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
                String res = response.body().string();
                Log.d(TAG, "onResponse: " + res);
                JsonObject bigObj = new JsonParser().parse(res).getAsJsonObject();
                int count = bigObj.get("total").getAsInt();
                Gson gson = new Gson();
                mHistoryResults = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    HistoryResult result = gson.fromJson(bigObj.getAsJsonObject(String.valueOf(i)).toString(), HistoryResult.class);
                    if (DataSupport.where("remoteId = ?", result.getId()).find(PhotoResult.class).size() == 0) {
                        mHistoryResults.add(result);
                    }
                }
                if (mHistoryResults.size() != 0) {
                    downloadCount = 0;
                    downloadOne();
                } else {
                    mSettingsView.success();
                }
            }
        });
    }

    private void downloadOne(){
        Log.d(TAG, "downloadOne: ");
        final HistoryResult historyResult = mHistoryResults.get(downloadCount);
        Request request = new Request.Builder().get()
                .url(MyConstConfig.SERVER_URL + historyResult.getImgPath())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mSettingsView.netError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                byte[] buf = new byte[2048];
                int len = 0;
                File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "account" + DataSupport.count(PhotoResult.class) + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                while((len = is.read(buf)) != -1){
                    fos.write(buf,0,len);
                }
                fos.flush();
                PhotoResult result = new PhotoResult(
                        historyResult.getId(),
                        file.toURI().toString(),
                        file.toURI().toString(),
                        historyResult.getImgText(),
                        historyResult.getDate(),
                        true);
                result.saveThrows();
                downloadCount++;
                if (downloadCount == mHistoryResults.size()) {
                    mSettingsView.success();
                    return;
                }
                Log.d(TAG, "onResponse: downloaded");
                downloadOne();
            }
        });
    }
}
