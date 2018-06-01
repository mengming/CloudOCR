package com.czm.cloudocr.PhotoSelect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.BuildConfig;
import com.czm.cloudocr.MainActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.Photos;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phelps on 2018/3/24.
 */

public class PhotoSelectFragment extends Fragment implements PhotoSelectContract.View{

    private RecyclerView mRecyclerView;
    public static PhotoSelectContract.Presenter mPresenter;
    private PhotoAdapter mPhotoAdapter;
    private Handler mainHandler;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            showPhotos((Photos) bundle.getSerializable("photos"));
        }
    };

    private List<String> mUrls = new ArrayList<>();
    private boolean sIsScrolling;
    private boolean advanced = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        mRecyclerView = view.findViewById(R.id.pic_recycler);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(getActivity()).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling) {
                        Glide.with(getActivity()).resumeRequests();
                    }
                    sIsScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        new PhotoSelectPresenter(getContext(),this);
        mPhotoAdapter = new PhotoAdapter(getContext(), getActivity(), mUrls, mPresenter);
        mRecyclerView.setAdapter(mPhotoAdapter);
        super.onActivityCreated(savedInstanceState);

        askPermission();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainHandler = ((MainActivity) context).mHandler;
        }
    }

    @Override
    public void setPresenter(PhotoSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPhotos(Photos photos) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("urls", photos.getUrls());
        message.setData(bundle);
        mainHandler.sendMessage(message);
        mUrls.clear();
        mUrls.addAll(photos.getGruopMap().get(photos.getUrls().get(0)));
        mPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeDirectory(List<String> urls) {
        mUrls.clear();
        mUrls.addAll(urls);
        mPhotoAdapter.notifyDataSetChanged();
    }

    private void askPermission() {
        AndPermission.with(this)
                .permission(Permission.Group.STORAGE)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        mPresenter.loadPhotos(mHandler);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                       showDialog();
                    }
                })
                .start();
    }

    private Rationale mRationale = new Rationale() {
        @Override
        public void showRationale(Context context, List<String> permissions,
                                  final RequestExecutor executor) {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage("没有权限将无法使用app")
                    .setPositiveButton("重新授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }
    };

    private void showDialog(){
        final SettingService settingService = AndPermission.permissionSetting(getContext());
        new AlertDialog.Builder(getContext())
                .setMessage("没有权限将无法使用app")
                .setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 如果用户同意去设置：
                        settingService.execute();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.cancel();
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }
}
