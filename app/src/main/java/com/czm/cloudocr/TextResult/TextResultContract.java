package com.czm.cloudocr.TextResult;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

import java.util.List;

public interface TextResultContract {
    interface View extends BaseView<Presenter>{
        void showText(PhotoResult photoResult);
        void waiting();
        void updated();
        void netError();
        void saveDialog();
        void comparePic();
        void showWordWindow();
        void refreshWords(List<String> strings);
    }

    interface Presenter{
        void updateText(String text, String id);
        void searchWord(String text);
    }
}
