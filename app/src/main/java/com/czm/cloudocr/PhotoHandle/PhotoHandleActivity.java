package com.czm.cloudocr.PhotoHandle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

import java.io.File;

public class PhotoHandleActivity extends AppCompatActivity implements View.OnClickListener, PhotoHandleContract.View{

    private ImageView mContentView;
    private Uri imgUri;
    private PhotoHandleContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_handle);

        mContentView = findViewById(R.id.handle_iv);
        new PhotoHandlePresenter(this, this);
        Log.d("pha", "uri = " + getIntent().getStringExtra("photo"));
        imgUri = Uri.fromFile(new File(getIntent().getStringExtra("photo")));
        showImage(Uri.parse(getIntent().getStringExtra("photo")));
        Button btnCrop = findViewById(R.id.handle_crop_btn);
        btnCrop.setOnClickListener(this);
        Button btnBack = findViewById(R.id.handle_back_btn);
        btnBack.setOnClickListener(this);
        Button btnOcr = findViewById(R.id.handle_ocr_btn);
        btnOcr.setOnClickListener(this);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                imgUri = result.getUri();
//                showImage(imgUri);
//                Log.d("pha", "crop_uri = " + imgUri.toString());
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }

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
            case R.id.handle_crop_btn :
//                CropImage.activity(imgUri)
//                        .start(PhotoHandleActivity.this);
                break;
            case R.id.handle_back_btn :
                finish();
                break;
            case R.id.handle_ocr_btn :
                mPresenter.compressPic(imgUri);
                break;
        }
    }

    @Override
    public void showImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(mContentView);
    }

    @Override
    public void showText(PhotoResult result) {
//        Intent intent = new Intent(this, TextResultActivity.class);
//        intent.putExtra("uri", result.getUri());
//        intent.putExtra("text", result.getText());
//        startActivity(intent);
    }


    @Override
    public void setPresenter(PhotoHandleContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
