package com.czm.cloudocr.Login;

import android.content.Context;
import android.util.Log;

import com.czm.cloudocr.model.LoginResult;
import com.czm.cloudocr.util.MyConstConfig;
import com.google.gson.Gson;

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
    public void login(final String account, String password) throws IOException{
        if (isLeagle(account, password)) {
            mView.loading();
            postServer(account, password, "login").enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mView.error("net", "网络连接失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Gson gson = new Gson();
                    String res = response.body().string();
                    Log.d(TAG, "onResponse: " + res);
                    LoginResult result = gson.fromJson(res, LoginResult.class);
                    Log.d(TAG, "onResponse: " + result.toString());
                    if (result.getStatus().equals("OK")) {
                        mContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit().putString("account", account).apply();
                        mView.success(result.getNote());
                    } else if (result.getStatus().equals("PW") || result.getStatus().equals("NoUser")) {
                        mView.error(result.getStatus(), result.getNote());
                    }
                }
            });
        }
    }

    @Override
    public void register(final String account, String password) throws IOException {
        if (isLeagle(account, password)) {
            mView.loading();
            postServer(account, password, "regist").enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mView.error("net", "网络连接失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Gson gson = new Gson();
                    LoginResult result = gson.fromJson(response.body().string(), LoginResult.class);
                    if (result.getStatus().equals("OK")) {
                        mContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit().putString("account", account).apply();
                        mView.success(result.getNote());
                    } else {
                        mView.error(result.getStatus(), result.getNote());
                    }
                }
            });
        }
    }

    private Call postServer(String account, String password, String param){
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", account)
                .addFormDataPart("password", password);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(MyConstConfig.SERVER_URL + param)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    private boolean isLeagle(String account, String password){
        if (account.length() > 10) {
            mView.illegal("账号长度不符合");
            return false;
        } else if (password.length() > 16 || password.length() < 8){
            mView.illegal("密码长度不符合");
            return false;
        }
        return true;
    }
}
