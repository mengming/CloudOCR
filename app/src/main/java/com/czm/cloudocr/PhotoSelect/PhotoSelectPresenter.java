package com.czm.cloudocr.PhotoSelect;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.czm.cloudocr.model.Photos;

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
    private ArrayList<String> dirs = new ArrayList<>();
    private Photos mPhotos;

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
                final Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                if(mCursor == null){
                    return;
                }
                mGruopMap.put("所有图片", new ArrayList<String>());
                dirs.add("所有图片");
                Observable.create(new ObservableOnSubscribe<Photos>() {
                    @Override
                    public void subscribe(ObservableEmitter<Photos> emitter) throws Exception {
                        while (mCursor.moveToNext()) {
                            //获取图片的路径
                            String path = mCursor.getString(mCursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));

                            //获取该图片的父路径名
                            String parentName = new File(path).getParentFile().getName();

                            //根据父路径名将图片放入到mGruopMap中
                            if (!mGruopMap.containsKey(parentName)) {
                                dirs.add(parentName);
                                List<String> chileList = new ArrayList<String>();
                                chileList.add(path);
                                mGruopMap.put(parentName, chileList);
                            } else {
                                mGruopMap.get(parentName).add(path);
                            }
                            mGruopMap.get("所有图片").add(path);
                        }
                        mPhotos = new Photos(mGruopMap, dirs);
                        emitter.onNext(mPhotos);
                        mCursor.close();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Photos>() {
                            @Override
                            public void accept(Photos photos) throws Exception {
                                mPhotoSelectView.showPhotos(mPhotos);
                            }
                        });
            }
        }).start();

    }

    @Override
    public void getPhotos(String key) {
        mPhotoSelectView.changeDirectory(mPhotos.getGruopMap().get(key));
    }

}
