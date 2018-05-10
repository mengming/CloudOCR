package com.czm.cloudocr.PhotoSelect;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.MainActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.Photos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phelps on 2018/3/24.
 */

public class PhotoSelectFragment extends Fragment implements PhotoSelectContract.View{

    private RecyclerView mRecyclerView;
    public static PhotoSelectContract.Presenter mPresenter;
    private List<String> mUrls = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;
    private boolean sIsScrolling;
    private Handler mainHandler;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            showPhotos((Photos) bundle.getSerializable("photos"));
        }
    };

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
        mPhotoAdapter = new PhotoAdapter(getContext(), mUrls);
        mRecyclerView.setAdapter(mPhotoAdapter);
        super.onActivityCreated(savedInstanceState);

        new PhotoSelectPresenter(getContext(),this);
        mPresenter.loadPhotos(mHandler);
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

}
