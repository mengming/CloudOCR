package com.czm.cloudocr.PhotoHandle;

import android.net.Uri;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.model.SearchResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PhotoHandleContract {
    interface View extends BaseView<Presenter>{
        void waiting(String message);
        void ocrError();
        void showImage(Uri uri);
        void showText(PhotoResult result);
        void openPdf(String path);
        void showSearch(List<SearchResult> list);
    }

    interface Presenter{
        void sendPic(Uri uri, boolean advanced) throws IOException;
        void savePic(PhotoResult result);
        void searchPic(Uri uri) throws IOException;
        void savePdf(Uri uri, String name);
    }
}
