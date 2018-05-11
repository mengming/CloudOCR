package com.czm.cloudocr.TextResult;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

public interface TextResultContract {
    interface View extends BaseView<Presenter>{
        void showText(PhotoResult photoResult);
        void saveDialog();
        void comparePic();
    }

    interface Presenter{
        PhotoResult refreshResult(int id);
        void updateText(String text, int id);
    }
}
