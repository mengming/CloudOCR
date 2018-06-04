package com.czm.cloudocr.PhotoContent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.SearchResult;
import com.czm.cloudocr.util.SystemUtils;

import java.io.FileNotFoundException;

public class PhotoContentActivity extends AppCompatActivity implements View.OnClickListener, PhotoContentContract.View{

    private ProgressDialog mProgressDialog;
    private SearchResult mSearchResult;
    private PhotoContentContract.Presenter mPresenter;
    private boolean delay;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
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

        new PhotoContentPresenter(this, this);
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
                mPresenter.download(mSearchResult.getObjURL());
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
        mProgressDialog = SystemUtils.waitingDialog(this, "正在下载...");
        mProgressDialog.show();
    }

    @Override
    public void success() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("下载原图完成");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void error() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("下载失败，请检查网络连接或重试");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void setPresenter(PhotoContentContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
