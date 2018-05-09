package com.czm.cloudocr;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.czm.cloudocr.PhotoHandle.PhotoHandleActivity;
import com.czm.cloudocr.PhotoSelect.PhotoSelectPresenter;
import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;

    private Uri imageUri;

    public static final int TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.main_viewpager);
        mFragments = new ArrayList<>();
        PhotoSelectFragment photoSelectFragment = new PhotoSelectFragment();
        mFragments.add(photoSelectFragment);
        final FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mBottomNavigationView = findViewById(R.id.main_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_pic:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_camera:
                        takePhoto();
//                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_history:
//                        mViewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO :
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, PhotoHandleActivity.class);
                    Log.d("pha", "uri = "+imageUri);
                    intent.putExtra("photo", imageUri.toString());
                    startActivity(intent);
                }
                break;
        }
    }

    private void takePhoto(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                "com.czm.cloudocr.provider", file);

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PHOTO);
    }

}
