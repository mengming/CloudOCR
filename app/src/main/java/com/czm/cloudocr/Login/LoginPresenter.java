package com.czm.cloudocr.Login;

import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPresenter implements LoginContract.Presenter {

    private Context mContext;
    private LoginContract.View mView;

    public LoginPresenter(Context context, LoginContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void login(String account, String password) throws IOException{
        mView.loading();
        Response response = postServer(account, password).execute();
        if (response.code() == 200) {
            mView.success(response.message());
        } else {
            mView.error(response.message());
        }
    }

    @Override
    public void register(String account, String password) throws IOException {
        mView.loading();
        Response response = postServer(account, password).execute();
        if (response.code() == 200) {
            mView.success(response.message());
        } else {
            mView.error(response.message());
        }
    }

    private Call postServer(String account, String password){
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", account)
                .addFormDataPart("password", password);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("www.baidu.com")
                .post(requestBody)
                .build();
        return client.newCall(request);
    }
}
