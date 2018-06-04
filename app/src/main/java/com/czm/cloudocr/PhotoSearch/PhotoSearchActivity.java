package com.czm.cloudocr.PhotoSearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.czm.cloudocr.R;
import com.czm.cloudocr.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class PhotoSearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private PhotoSearchAdapter mAdapter;
    private List<SearchResult> mSearchResults = new ArrayList<>();
    private static final String TAG = "PhotoSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_search);

        mSearchResults.addAll((List<SearchResult>) getIntent().getSerializableExtra("search_results"));
        Log.d(TAG, "onCreate: size = " + mSearchResults.size());

        Toolbar toolbar = findViewById(R.id.photo_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("相似图片");

        mRecyclerView = findViewById(R.id.photo_search_recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new PhotoSearchAdapter(this, mSearchResults);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
