package com.czm.cloudocr.TextResult;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

public interface TextResultContract {
    interface View extends BaseView<Presenter>{
        void showText(PhotoResult photoResult);
        void comparePic();
    }

    interface Presenter{
        void loadText(String uri);
        void updateText(String text, int id);
    }
}
