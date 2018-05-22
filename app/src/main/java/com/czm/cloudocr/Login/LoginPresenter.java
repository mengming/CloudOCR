package com.czm.cloudocr.Login;

import android.content.Context;
import android.util.Log;

import com.czm.cloudocr.model.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Path;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "LoginPresenter";
    private Context mContext;
    private LoginContract.View mView;

    public LoginPresenter(Context context, LoginContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void login(String account, String password) throws IOException{
        mView.loading();
        postServer(account, password, "login").enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                LoginResult result = gson.fromJson(response.body().string(), LoginResult.class);
                Log.d(TAG, "onResponse: " + result.toString());
                if (result.getStatus().equals("OK")) {
                    mView.success(result.getNote());
                } else if (result.getStatus().equals("PW") || result.getStatus().equals("NoUser")){
                    mView.error(result.getStatus(), result.getNote());
                }
            }
        });
    }

    @Override
    public void register(String account, String password) throws IOException {
        mView.loading();
        postServer(account, password, "regist").enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                LoginResult result = gson.fromJson(response.body().string(), LoginResult.class);
                if (result.getStatus().equals("OK")) {
                    mView.success(result.getNote());
                } else {
                    mView.error(result.getStatus(), result.getNote());
                }
            }
        });
    }

    private Call postServer(String account, String password, String param){
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", account)
                .addFormDataPart("password", password);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://192.168.199.234:8080/" + param)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }
}
