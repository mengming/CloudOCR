package com.czm.cloudocr.OcrHistory;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.czm.cloudocr.model.PhotoResult;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
//        Observable.create(new ObservableOnSubscribe<List<PhotoResult>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<PhotoResult>> emitter) throws Exception {
//                emitter.onNext(DataSupport.findAll(PhotoResult.class));
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<PhotoResult>>() {
//                    @Override
//                    public void accept(List<PhotoResult> results) throws Exception {
//                        mView.showHistory(results);
//                    }
//                });
    }
}
