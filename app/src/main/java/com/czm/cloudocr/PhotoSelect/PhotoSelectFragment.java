package com.czm.cloudocr.PhotoSelect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phelps on 2018/3/24.
 */

public class PhotoSelectFragment extends Fragment implements PhotoSelectContract.View{

    private RecyclerView mRecyclerView;
    private PhotoSelectContract.Presenter mPresenter;
    private List<String> mUrls = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;
    private boolean sIsScrolling;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        mPresenter.unSubscribe();
        super.onPause();
    }

    @Override
    public void setPresenter(PhotoSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPhotos(HashMap<String, List<String>> map) {
        mUrls.clear();
        mUrls.addAll(map.get("Screenshots"));
        mPhotoAdapter.notifyDataSetChanged();
    }
}
