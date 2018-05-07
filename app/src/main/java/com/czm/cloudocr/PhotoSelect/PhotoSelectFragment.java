package com.czm.cloudocr.PhotoSelect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.Photos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phelps on 2018/3/24.
 */

public class PhotoSelectFragment extends Fragment implements PhotoSelectContract.View{

    private RecyclerView mRecyclerView;
    private AppCompatSpinner mSpinner;
    private PhotoSelectContract.Presenter mPresenter;
    private List<String> mUrls = new ArrayList<>();
    private List<String> mDirs = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;
    private ArrayAdapter<String> mDirAdapter;
    private boolean sIsScrolling;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mSpinner = view.findViewById(R.id.main_spinner);
        mRecyclerView = view.findViewById(R.id.pic_recycler);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mDirAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mDirs);
        mSpinner.setAdapter(mDirAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.getPhotos(mDirs.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        mPresenter.loadPhotos();
    }

    @Override
    public void setPresenter(PhotoSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPhotos(Photos photos) {
        mDirs.clear();
        mDirs.addAll(photos.getUrls());
        mDirAdapter.notifyDataSetChanged();
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
