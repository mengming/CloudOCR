package com.czm.cloudocr.OcrHistory;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.czm.cloudocr.model.PhotoResult;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class OcrHistoryPresenter implements OcrHistoryContract.Presenter {

    private Context mContext;
    private OcrHistoryContract.View mView;

    public OcrHistoryPresenter(Context context, OcrHistoryContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loadHistory(Handler handler) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("results", (Serializable) DataSupport.findAll(PhotoResult.class));
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
