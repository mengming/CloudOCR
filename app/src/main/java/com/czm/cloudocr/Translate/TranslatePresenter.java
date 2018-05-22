package com.czm.cloudocr.Translate;

import android.content.Context;
import android.util.Log;

import com.czm.cloudocr.model.TransResult;
import com.czm.cloudocr.util.SystemUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslatePresenter implements TranslateContract.Presenter {

    private TranslateContract.View mTranslateView;
    private Context mContext;

    private static final String BAIDU_TRANS_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static final String APPID = "20180521000163629";
    private static final String SALT = "1435660288";
    private static final String KEY = "EPas6azGPgb7dc3xlDqo";
    private static final String TAG = "TranslatePresenter";

    public TranslatePresenter(TranslateContract.View translateView, Context context) {
        mTranslateView = translateView;
        mContext = context;
        mTranslateView.setPresenter(this);
    }

    @Override
    public void send(String text) {
        String spliceStr = null;
        spliceStr = APPID + text + SALT + KEY;
        String mdStr = SystemUtils.md5(spliceStr);
        Log.d(TAG, "send: spliceStr = " + spliceStr);
        Log.d(TAG, "send: md5 = " + mdStr);
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("q", text)
                .addFormDataPart("from", "zh")
                .addFormDataPart("to", "en")
                .addFormDataPart("appid", APPID)
                .addFormDataPart("salt",SALT)
                .addFormDataPart("sign", mdStr);
        Request request = new Request.Builder()
                .url(BAIDU_TRANS_URL)
                .post(builder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: code = " + response.code());
                String body = response.body().string();
                Log.d(TAG, "onResponse: body = " + body);
                Gson gson = new Gson();
                TransResult result = gson.fromJson(body, TransResult.class);
                mTranslateView.showTranslated(result.getTrans_result().get(0).getDst());
            }
        });
    }
}
