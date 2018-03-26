package com.czm.cloudocr.PhotoSelect;

import com.czm.cloudocr.BasePresenter;
import com.czm.cloudocr.BaseView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Phelps on 2018/3/25.
 */

public interface PhotoSelectContract {
    interface View extends BaseView<Presenter>{
        void showPhotos(HashMap<String, List<String>> map);
    }

    interface Presenter extends BasePresenter{
        void loadPhotos();
    }
}
