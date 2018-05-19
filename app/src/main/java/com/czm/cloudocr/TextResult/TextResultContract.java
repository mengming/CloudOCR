package com.czm.cloudocr.TextResult;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

public interface TextResultContract {
    interface View extends BaseView<Presenter>{
        void showText(PhotoResult photoResult);
        void waiting();
        void updated();
        void saveDialog();
        void comparePic();
    }

    interface Presenter{
        void updateText(String text, int id);
    }
}
