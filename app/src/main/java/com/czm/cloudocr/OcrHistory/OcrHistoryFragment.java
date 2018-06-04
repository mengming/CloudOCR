package com.czm.cloudocr.OcrHistory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

import java.util.ArrayList;
import java.util.List;

public class OcrHistoryFragment extends Fragment implements OcrHistoryContract.View{

    private RecyclerView mRecyclerView;
    private OcrHistoryContract.Presenter mPresenter;
    private OcrHistoryAdapter mOcrHistoryAdapter;
    private static final String TAG = "OcrHistoryFragment";
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showHistory((List<PhotoResult>) msg.getData().getSerializable("results"));
        }
    };
    private List<PhotoResult> mPhotoResults = new ArrayList<>();

    public OcrHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocr_history, container, false);
        mRecyclerView = view.findViewById(R.id.history_recycler);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOcrHistoryAdapter = new OcrHistoryAdapter(getContext(), mPhotoResults);
        mRecyclerView.setAdapter(mOcrHistoryAdapter);

        new OcrHistoryPresenter(getContext(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mPresenter.loadHistory(mHandler);
    }

    @Override
    public void showHistory(List<PhotoResult> results) {
        mPhotoResults.clear();
        mPhotoResults.addAll(results);
        mOcrHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(OcrHistoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
