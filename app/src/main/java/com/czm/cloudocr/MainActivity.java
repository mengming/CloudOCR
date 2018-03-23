package com.czm.cloudocr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czm.cloudocr.Util.HttpUtils;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Picasso picasso = new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(HttpUtils.getInstance()))
                .build();
        Picasso.setSingletonInstance(picasso);
    }
}
