package com.czm.cloudocr.TextResult;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.SystemUtils;

public class TextResultActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, TextResultContract.View{

    private static final String TAG = "TextResultActivity";
    private TextResultContract.Presenter mPresenter;
    private PhotoResult mPhotoResult;
    private EditText mEditText;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;
    private ConstraintSet resizeConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private ConstraintLayout mConstraintLayout;
    private Boolean compareFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_result);

        Toolbar toolbar = findViewById(R.id.text_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(this);
        mEditText = findViewById(R.id.result_edittext);
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
                mPresenter.updateText(mEditText.getText().toString(), mPhotoResult.getId());
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
        mProgressDialog.setMessage("云端同步成功");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                finish();
            }
        },1000);
    }

    @Override
    public void saveDialog() {
        new AlertDialog.Builder(this)
                .setMessage("是否放弃修改")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateText(mEditText.getText().toString(), mPhotoResult.getId());
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
    public void setPresenter(TextResultContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
