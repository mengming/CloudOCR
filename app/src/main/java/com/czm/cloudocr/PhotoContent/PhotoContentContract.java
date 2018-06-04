package com.czm.cloudocr.PhotoContent;

import com.czm.cloudocr.BaseView;

public interface PhotoContentContract {
    interface View extends BaseView<Presenter>{
        void waiting();
        void success();
        void error();
    }

    interface Presenter{
        void download(String url);
    }
}
