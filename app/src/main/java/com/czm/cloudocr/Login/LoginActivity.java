package com.czm.cloudocr.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.czm.cloudocr.R;
import com.czm.cloudocr.util.SystemUtils;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements LoginContract.View, View.OnClickListener{

    private LoginContract.Presenter mPresenter;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();
    private TextInputLayout mAccountInput, mPasswordInput;
    private TextInputEditText mAccountEditText, mPasswordEditText;
    private Button mBtnLogin, mBtnRegister;

    private static final String TAG = "LoginActivity";
    private boolean delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAccountInput = findViewById(R.id.til_account);
        mPasswordInput = findViewById(R.id.til_password);
        mAccountEditText = findViewById(R.id.et_account);
        mPasswordEditText = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.login_btn);
        mBtnLogin.setOnClickListener(this);
        mBtnRegister = findViewById(R.id.register_btn);
        mBtnRegister.setOnClickListener(this);
        mAccountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10) {
                    mAccountInput.setErrorEnabled(true);
                    mAccountInput.setError("账号最大长度不超过10");
                } else {
                    mAccountInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 16) {
                    mPasswordInput.setErrorEnabled(true);
                    mPasswordInput.setError("密码长度不超过16");
                } else if (s.length() < 8) {
                    mPasswordInput.setErrorEnabled(true);
                    mPasswordInput.setError("密码长度不小于8");
                } else {
                    mPasswordInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new LoginPresenter(this, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("account", "");
            setResult(0, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loading() {
        mProgressDialog = SystemUtils.waitingDialog(this, "登录中");
        mProgressDialog.show();
    }

    @Override
    public void illegal(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(final String message) {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage(message);
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent();
                    String account = LoginActivity.this.getSharedPreferences("settings", MODE_PRIVATE).getString("account","");
                    Log.d(TAG, "run: account = " + account);
                    intent.putExtra("account", account);
                    setResult(0, intent);
                    finish();
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void error(final String status, final String message) {
        delay = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (delay) {
                    mProgressDialog.setMessage(message);
                    delay = false;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mProgressDialog.dismiss();
                    if (status.equals("PW")) {
                        mPasswordInput.setError(message);
                    } else if (status.equals("NoUser")) {
                        mAccountInput.setError(message);
                    }
                    mHandler.removeCallbacks(this);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn: {
                try {
                    mPresenter.login(mAccountEditText.getText().toString(), mPasswordEditText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.register_btn: {
                try {
                    mPresenter.register(mAccountEditText.getText().toString(), mPasswordEditText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("account", "");
        setResult(0, intent);
        super.onBackPressed();
    }

}
