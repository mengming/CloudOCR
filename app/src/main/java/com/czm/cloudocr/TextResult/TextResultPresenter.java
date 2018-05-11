package com.czm.cloudocr.TextResult;

import android.content.ContentValues;
import android.content.Context;

import com.czm.cloudocr.model.PhotoResult;

import org.litepal.crud.DataSupport;


public class TextResultPresenter implements TextResultContract.Presenter {

    private TextResultContract.View mTextResultView;
    private Context mContext;

    public TextResultPresenter(TextResultContract.View textResultView, Context context) {
        mTextResultView = textResultView;
        mContext = context;
        mTextResultView.setPresenter(this);
    }

    @Override
    public PhotoResult refreshResult(int id) {
        return DataSupport.find(PhotoResult.class, id);
    }

    @Override
    public void updateText(String text, int id) {
        ContentValues values = new ContentValues();
        values.put("text", text);
        DataSupport.update(PhotoResult.class, values, id);
    }
}
