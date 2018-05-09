package com.czm.cloudocr.OcrHistory;

import android.content.Context;

import com.czm.cloudocr.model.PhotoResult;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

public class OcrHistoryPresenter implements OcrHistoryContract.Presenter {

    private Context mContext;
    private OcrHistoryContract.View mView;

    public OcrHistoryPresenter(Context context, OcrHistoryContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void loadHistory() {
        DataSupport.findAllAsync(PhotoResult.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                mView.showHistory((List<PhotoResult>) t);
            }
        });
    }
}
