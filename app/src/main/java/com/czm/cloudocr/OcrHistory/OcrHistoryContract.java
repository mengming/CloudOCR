package com.czm.cloudocr.OcrHistory;

import android.os.Handler;

import com.czm.cloudocr.BaseView;
import com.czm.cloudocr.model.PhotoResult;

import java.util.List;

public interface OcrHistoryContract {
    interface View extends BaseView<Presenter> {
        void showHistory(List<PhotoResult> results);
    }
    interface Presenter{
        void loadHistory(Handler handler);
    }
}
