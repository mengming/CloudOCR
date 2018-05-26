package com.czm.cloudocr.Translate;

import com.czm.cloudocr.BaseView;

public interface TranslateContract {
    interface View extends BaseView<Presenter>{
        void showTranslated(String text);
    }

    interface Presenter{
        void send(String text, String from, String to);
    }
}
