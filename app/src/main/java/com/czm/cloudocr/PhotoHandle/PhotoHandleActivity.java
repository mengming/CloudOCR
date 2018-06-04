package com.czm.cloudocr.PhotoHandle;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.Login.LoginActivity;
import com.czm.cloudocr.MainActivity;
import com.czm.cloudocr.PhotoSearch.PhotoSearchActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.TextResult.TextResultActivity;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.model.SearchResult;
import com.czm.cloudocr.util.SystemUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.czm.cloudocr.util.MyConstConfig.LOGIN;

public class PhotoHandleActivity extends AppCompatActivity implements View.OnClickListener, PhotoHandleContract.View{

    private static final String TAG = "PhotoHandleActivity";
    private ImageView mContentView;
    private Uri imgUri;
    private Button btnOcr;
    private TextView tvOcr;
    private ProgressDialog mProgressDialog;

    private PhotoHandleContract.Presenter mPresenter;
    private Handler mHandler = new Handler();
    private boolean flag;  //是否已经识别过
    private boolean delay;
    private boolean advanced;
    private PhotoResult mPhotoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_handle);

        mContentView = findViewById(R.id.handle_iv);
        new PhotoHandlePresenter(this, this);
        Log.d(TAG, "uri = " + getIntent().getStringExtra("photo"));
        imgUri = Uri.parse(getIntent().getStringExtra("photo"));
        advanced = getIntent().getBooleanExtra("advanced", false);
        flag = getIntent().getBooleanExtra("flag", false);
        showImage(imgUri);
        Button btnCrop = findViewById(R.id.handle_crop_btn);
        btnCrop.setOnClickListener(this);
        Button btnBack = findViewById(R.id.handle_back_btn);
        btnBack.setOnClickListener(this);
        btnOcr = findViewById(R.id.handle_ocr_btn);
        btnOcr.setOnClickListener(this);
        Button btnPdf = findViewById(R.id.handle_pdf_btn);
        btnPdf.setOnClickListener(this);
        Button btnSearch = findViewById(R.id.handle_search_btn);
        btnSearch.setOnClickListener(this);
        tvOcr = findViewById(R.id.handle_ocr_text);
        if (flag) {
            btnOcr.setBackgroundResource(R.drawable.ic_text);
            tvOcr.setText("查看文字");
            mPhotoResult = (PhotoResult) getIntent().getSerializableExtra("photo_result");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imgUri = result.getUri();
                    showImage(imgUri);
                    flag = false;
                    btnOcr.setBackgroundResource(R.drawable.ic_ocr);
                    tvOcr.setText("文字识别");
                    mPhotoResult = null;
                    Log.d(TAG, "crop_uri = " + imgUri.toString());
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
            case LOGIN:
                try {
                    mPresenter.sendPic(imgUri, advanced);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
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
            case R.id.handle_crop_btn :
                CropImage.activity(imgUri)
                        .start(PhotoHandleActivity.this);
                break;
            case R.id.handle_back_btn :
                finish();
                break;
            case R.id.handle_ocr_btn :
                if (flag) {
                    showText(mPhotoResult);
                } else {
                    if (getSharedPreferences("settings", MODE_PRIVATE).getString("account","").equals("")){
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivityForResult(new Intent(PhotoHandleActivity.this, LoginActivity.class), LOGIN);
                            }
                        },1000);
                    }
                    try {
                        mPresenter.sendPic(imgUri, advanced);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.handle_pdf_btn :
                pdfDialog();
                break;
            case R.id.handle_search_btn:
                try {
                    mPresenter.searchPic(imgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void waiting(String message) {
        mProgressDialog = SystemUtils.waitingDialog(this, message);
        mProgressDialog.show();
    }

    @Override
    public void ocrError() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("识别失败，请检查网络连接或重试");
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
    public void showImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(mContentView);
    }

    @Override
    public void showText(final PhotoResult result) {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("识别成功");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(PhotoHandleActivity.this, TextResultActivity.class);
                    intent.putExtra("photo_result", result);
                    startActivity(intent);
                    finish();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void openPdf(String path) {
        File file = new File(path);
        if (file.exists()) {
            Uri uri;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, "com.czm.cloudocr.provider", file);
                // 给目标应用一个临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        "No Application Available to View PDF",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void showSearch(final List<SearchResult> list) {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("搜索成功");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(PhotoHandleActivity.this, PhotoSearchActivity.class);
                    intent.putExtra("search_results", (Serializable) list);
                    startActivity(intent);
                    finish();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void setPresenter(PhotoHandleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void pdfDialog(){
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setMessage("请输入pdf名称")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.savePdf(imgUri, editText.getText().toString());
                    }
                })
                .create().show();
    }
}
