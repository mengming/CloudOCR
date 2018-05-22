package com.czm.cloudocr.Settings;

import com.czm.cloudocr.BaseView;

public interface SettingsContract {
    interface View extends BaseView<Presenter>{
        void uploaded();
    }

    interface Presenter{
        void uploadAll();
    }
}
