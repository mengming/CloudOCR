package com.czm.cloudocr.PhotoContent;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.SearchResult;

import java.io.FileNotFoundException;

public class PhotoContentActivity extends AppCompatActivity implements View.OnClickListener, PhotoContentContract.View{

    private SearchResult mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_content);

        mSearchResult = (SearchResult) getIntent().getSerializableExtra("search_result");
        ImageView imageView = findViewById(R.id.search_content_iv);
        Button btnDownload = findViewById(R.id.search_download_btn);
        btnDownload.setOnClickListener(this);
        Button btnWeb = findViewById(R.id.search_website_btn);
        btnWeb.setOnClickListener(this);
        Button btnBack = findViewById(R.id.search_back_btn);
        btnBack.setOnClickListener(this);

        Glide.with(this)
                .load(mSearchResult.getObjURL())
                .into(imageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back_btn:
                finish();
                break;
            case R.id.search_download_btn:
                break;
            case R.id.search_website_btn:
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(mSearchResult.getFromURL());
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void waiting() {

    }

    @Override
    public void success() {

    }

    @Override
    public void error() {

    }

    @Override
    public void setPresenter(PhotoContentContract.Presenter presenter) {

    }
}
