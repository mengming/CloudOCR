package com.czm.cloudocr.Login;

import com.czm.cloudocr.BaseView;

import java.io.IOException;

public interface LoginContract {
    interface View extends BaseView<Presenter>{
        void loading();
        void success(String message);
        void error(String message);
    }

    interface Presenter{
        void login(String account, String password) throws IOException;
        void register(String account, String password) throws IOException;
    }
}
