package com.czm.cloudocr.Settings;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.HistoryResult;

import java.util.List;

public interface SettingsContract {
    interface View extends BaseView<Presenter>{
        void waiting(String message);
        void success();
        void netError();
    }

    interface Presenter{
        void uploadAll();
        void downloadAll(String account);
    }
}
