package com.czm.cloudocr.PhotoSelect;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.Photos;

import java.util.List;

/**
 * Created by Phelps on 2018/3/25.
 */

public interface PhotoSelectContract {
    interface View extends BaseView<Presenter>{
        void showPhotos(Photos photos);
        void changeDirectory(List<String> urls);
    }

    interface Presenter{
        void loadPhotos();
        void getPhotos(String key);
    }
}
