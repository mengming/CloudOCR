package com.czm.cloudocr.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.cloudocr.Login.LoginActivity;
import com.czm.cloudocr.R;
import com.czm.cloudocr.model.HistoryResult;
import com.czm.cloudocr.util.SystemUtils;

import java.io.Serializable;
import java.util.List;

import static com.czm.cloudocr.util.MyConstConfig.LOGIN;

public class SettingsActivity extends AppCompatActivity implements SettingsContract.View,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private CardView accountCard, uploadCard, downloadCard;
    private Switch passwordSwitch, cloudSwitch;
    private TextView accountText;
    private ProgressDialog mProgressDialog;

    private SettingsContract.Presenter mPresenter;
    private Handler mHandler = new Handler();

    private String account;
    private boolean delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountCard = findViewById(R.id.settings_account_card);
        uploadCard = findViewById(R.id.settings_upload_card);
        passwordSwitch = findViewById(R.id.settings_password_switch);
        cloudSwitch = findViewById(R.id.settings_cloud_switch);
        accountText = findViewById(R.id.settings_account_tv);
        downloadCard = findViewById(R.id.settings_download_card);
        accountCard.setOnClickListener(this);
        uploadCard.setOnClickListener(this);
        passwordSwitch.setOnCheckedChangeListener(this);
        cloudSwitch.setOnCheckedChangeListener(this);
        downloadCard.setOnClickListener(this);
        if ((account = getSharedPreferences("settings", MODE_PRIVATE).getString("account","")).equals("")){
            passwordSwitch.setChecked(false);
        } else {
            accountText.setText(account);
        }
        if (!getSharedPreferences("settings", MODE_PRIVATE).getBoolean("cloud", true)) {
            cloudSwitch.setChecked(false);
        }

        new SettingsPresenter(this, this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN) {
            String account = data.getStringExtra("account");
            if (!account.equals("")) {
                accountText.setText(account);
                passwordSwitch.setChecked(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_account_card:
                if (!passwordSwitch.isChecked()) {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN);
                }
                break;
            case R.id.settings_upload_card:
                mPresenter.uploadAll();
                break;
            case R.id.settings_download_card:
                mPresenter.downloadAll(account);
                break;
        }
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.settings_password_switch:
                if (isChecked){
                    if (getSharedPreferences("settings", MODE_PRIVATE).getString("account","").equals("")){
                        Toast.makeText(SettingsActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        passwordSwitch.setChecked(false);
                    }
                } else {
                    getSharedPreferences("settings", MODE_PRIVATE).edit().remove("account").apply();
                    Toast.makeText(SettingsActivity.this, "你已退出登录", Toast.LENGTH_SHORT).show();
                    accountText.setText("用户未登录");
                }
                break;
            case R.id.settings_cloud_switch:
                if (isChecked) {
                    getSharedPreferences("settings", MODE_PRIVATE).edit().remove("cloud").putBoolean("cloud",true).apply();
                } else {
                    getSharedPreferences("settings", MODE_PRIVATE).edit().remove("cloud").putBoolean("cloud",false).apply();
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
    public void success() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("云端信息同步成功");
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
    public void netError() {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage("同步失败，请检查网络连接重试");
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

}
