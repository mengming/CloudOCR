package com.czm.cloudocr.OcrHistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

import java.util.List;

public class OcrHistoryFragment extends Fragment implements OcrHistoryContract.View{

    private RecyclerView mRecyclerView;
    private OcrHistoryContract.Presenter mPresenter;

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

        new OcrHistoryPresenter(getContext(), this);
        mPresenter.loadHistory();
    }

    @Override
    public void showHistory(List<PhotoResult> results) {

    }

    @Override
    public void setPresenter(OcrHistoryContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
