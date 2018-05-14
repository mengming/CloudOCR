package com.czm.cloudocr.Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czm.cloudocr.R;

public class LoginActivity extends AppCompatActivity implements LoginContract.View{

    private LoginContract.Presenter mPresenter;

    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRelativeLayout = findViewById(R.id.login_relative);

        new LoginPresenter(this, this);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void success(String message) {
//        showMessage();
        finish();
    }

    @Override
    public void error(String message) {

    }

    private void showMessage(String message){
//        View view = LayoutInflater.from(this).inflate(R.layout.popwindow_hint, null);
//        TextView textView = view.findViewById(R.id.window_tv);
//        textView.setText(message);
//        RelativeLayout.LayoutParams paramsS = new RelativeLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT);
//        paramsS.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        // paramsS.setMargins(0, 900, 0,0);
//        view.setLayoutParams(paramsS);
//        mRelativeLayout.addView(view);
    }
}
