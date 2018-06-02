package com.czm.cloudocr.TextResult;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.Translate.TranslateActivity;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.SystemUtils;

import java.util.List;

public class TextResultActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, TextResultContract.View{

    private static final String TAG = "TextResultActivity";
    private TextResultContract.Presenter mPresenter;
    private Handler mHandler = new Handler();
    private EditText mEditText;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;
    private ConstraintSet resizeConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private ConstraintLayout mConstraintLayout;
    private Toolbar toolbar;

    private boolean delay;
    private boolean compareFlag = false;
    private PhotoResult mPhotoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_result);

        toolbar = findViewById(R.id.text_toolbar);
        toolbar.setTitle("识别结果");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(this);
        mEditText = findViewById(R.id.result_edittext);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN && event.getPointerCount() == 2) {
                    Log.d(TAG, "onTouch: 双指点击");
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    mPresenter.searchWord(mPhotoResult.getText());
                }
                return false;
            }
        });
        mImageView = findViewById(R.id.iv_compare_pic);
        mConstraintLayout = findViewById(R.id.result_constraint);
        resetConstraintSet.clone(mConstraintLayout);
        resizeConstraintSet.clone(mConstraintLayout);

        new TextResultPresenter(this, this);

        mPhotoResult = (PhotoResult) getIntent().getSerializableExtra("photo_result");
        showText(mPhotoResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.text_proofread:
                comparePic();
                break;
            case R.id.text_save:
                mPresenter.updateText(mEditText.getText().toString(), mPhotoResult.getRemoteId());
                break;
            case R.id.text_translate:
                Intent intent = new Intent(TextResultActivity.this, TranslateActivity.class);
                intent.putExtra("from_text", mEditText.getText().toString());
                startActivity(intent);
                break;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected: edittext = " + mEditText.getText().toString());
            Log.d(TAG, "onOptionsItemSelected: photoresult = " + mPhotoResult.getText());
            if (!mEditText.getText().toString().equals(mPhotoResult.getText())) {
                saveDialog();
            } else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showText(PhotoResult photoResult) {
        mEditText.setText(photoResult.getText());
        Glide.with(this)
                .load(photoResult.getUri())
                .into(mImageView);
    }

    @Override
    public void waiting() {
        mProgressDialog = SystemUtils.waitingDialog(this, "正在保存中...");
        mProgressDialog.show();
    }

    @Override
    public void updated() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("云端同步成功");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    finish();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void netError() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("云端同步失败，请检查网络连接再手动同步");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    finish();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void saveDialog() {
        new AlertDialog.Builder(this)
                .setMessage("是否放弃修改")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateText(mEditText.getText().toString(), mPhotoResult.getRemoteId());
                    }
                })
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create().show();
    }

    @Override
    public void comparePic() {
        TransitionManager.beginDelayedTransition(mConstraintLayout);
        if (compareFlag) {
            compareFlag = false;
            resetConstraintSet.applyTo(mConstraintLayout);
        } else {
            compareFlag = true;
            resizeConstraintSet.connect(R.id.result_edittext, ConstraintSet.BOTTOM, R.id.resize_guideline, ConstraintSet.TOP);
            resizeConstraintSet.connect(R.id.iv_compare_pic, ConstraintSet.TOP, R.id.resize_guideline, ConstraintSet.BOTTOM);
            resizeConstraintSet.applyTo(mConstraintLayout);
        }
    }

    @Override
    public void refreshWords(final List<String> strings) {
        Log.d(TAG, "refreshWords: " + strings.size());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WordSearchDialog dialog = new WordSearchDialog(TextResultActivity.this, R.style.TransparentDialog, strings);
                dialog.show();
            }
        });
    }

    @Override
    public void setPresenter(TextResultContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
