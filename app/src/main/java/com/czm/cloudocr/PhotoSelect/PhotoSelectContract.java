package com.czm.cloudocr.PhotoSelect;

import android.net.Uri;
import android.os.Handler;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;
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
        PhotoResult checkPhoto(Uri uri);
        void loadPhotos(Handler handler);
        void getPhotos(String key);
    }
}
