package com.czm.cloudocr.Util;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Phelps on 2018/3/23.
 */

public class HttpUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //double check lock(双重校验锁)
    private static OkHttpClient instance = null;
    private HttpUtils(){}
    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class){
                if (instance == null) {
                    instance = new OkHttpClient();
                }
            }
        }
        return instance;
    }

    private static String httpGet(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = instance.newCall(request).execute();
        return response.body().toString();
    }

    private static String httpPost(String url, String json) throws IOException{
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = instance.newCall(request).execute();
        return response.body().string();
    }

}
