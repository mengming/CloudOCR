package com.czm.cloudocr.TextResult;

import android.content.ContentValues;
import android.content.Context;

import com.czm.cloudocr.model.PhotoResult;

import org.litepal.crud.DataSupport;

import java.util.List;


public class TextResultPresenter implements TextResultContract.Presenter {

    private TextResultContract.View mTextResultView;
    private Context mContext;

    public TextResultPresenter(TextResultContract.View textResultView, Context context) {
        mTextResultView = textResultView;
        mContext = context;
        mTextResultView.setPresenter(this);
    }

    @Override
    public void loadText(String uri) {
        List<PhotoResult> results = DataSupport.where("uri = ?", uri).find(PhotoResult.class);
        if (results.size() != 0) mTextResultView.showText(results.get(0));
    }

    @Override
    public void updateText(String text, int id) {
        ContentValues values = new ContentValues();
        values.put("text", text);
        DataSupport.update(PhotoResult.class, values, id);
    }
}
