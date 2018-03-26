package com.czm.cloudocr.PhotoSelect;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Phelps on 2018/3/25.
 */

public class PhotoSelectPresenter implements PhotoSelectContract.Presenter {

    private final PhotoSelectContract.View mPhotoSelectView;
    private Context mContext;
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();

    public PhotoSelectPresenter(Context context, PhotoSelectContract.View view){
        mPhotoSelectView = view;
        mContext = context;
        mPhotoSelectView.setPresenter(this);
    }

    @Override
    public void loadPhotos() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                //只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                if(mCursor == null){
                    return;
                }

                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();


                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }
                Observable.create(new ObservableOnSubscribe<HashMap<String, List<String>>>() {
                    @Override
                    public void subscribe(ObservableEmitter<HashMap<String, List<String>>> emitter) throws Exception {
                        emitter.onNext(mGruopMap);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<HashMap<String, List<String>>>() {
                            @Override
                            public void accept(HashMap<String, List<String>> map) throws Exception {
                                mPhotoSelectView.showPhotos(map);
                            }
                        });
                mCursor.close();
            }
        }).start();

    }

    @Override
    public void subscribe() {
        loadPhotos();
    }

    @Override
    public void unSubscribe() {

    }
}
