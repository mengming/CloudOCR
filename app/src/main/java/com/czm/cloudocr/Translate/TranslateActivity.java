package com.czm.cloudocr.Translate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.czm.cloudocr.R;

public class TranslateActivity extends AppCompatActivity implements TranslateContract.View, View.OnClickListener {

    private EditText fromEt, toEt;
    private TextView fromTv, toTv;
    private String fromText, toText;
    private Button mBtnTrans;

    private TranslateContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Toolbar toolbar = findViewById(R.id.translate_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fromEt = findViewById(R.id.trans_from_et);
        toEt = findViewById(R.id.trans_to_et);
        fromTv = findViewById(R.id.trans_from_tv);
        toTv = findViewById(R.id.trans_to_tv);
        mBtnTrans = findViewById(R.id.trans_btn);
        mBtnTrans.setOnClickListener(this);

        fromText = getIntent().getStringExtra("from_text");
        fromEt.setText(fromText);

        new TranslatePresenter(this, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trans_btn:
                mPresenter.send(fromEt.getText().toString());
                break;
        }
    }

    @Override
    public void showTranslated(String text) {
        toEt.setText(text);
    }

    @Override
    public void setPresenter(TranslateContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
