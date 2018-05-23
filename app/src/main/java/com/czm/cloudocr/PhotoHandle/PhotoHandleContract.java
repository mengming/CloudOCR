package com.czm.cloudocr.PhotoHandle;

import android.net.Uri;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

import java.io.File;
import java.io.IOException;

public interface PhotoHandleContract {
    interface View extends BaseView<Presenter>{
        void waiting();
        void ocrError();
        void showImage(Uri uri);
        void showText(PhotoResult result);
        void openPdf(String path);
    }

    interface Presenter{
        void compressPic(Uri uri);
        void sendPic(File file, Uri uri) throws IOException;
        void savePic(PhotoResult result);
        void savePdf(Uri uri);
    }
}
