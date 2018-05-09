package com.czm.cloudocr;

import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.czm.cloudocr.OcrHistory.OcrHistoryFragment;
import com.czm.cloudocr.PhotoHandle.PhotoHandleActivity;
import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;
import com.czm.cloudocr.widget.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private MyViewPager mViewPager;
    private AppCompatSpinner mSpinner;

    private ArrayList<Fragment> mFragments;
    private List<String> mDirs = new ArrayList<>();
    private ArrayAdapter<String> mDirAdapter;
    private Uri imageUri;

    public static final int TAKE_PHOTO = 1;
    private int lastFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mSpinner = findViewById(R.id.main_spinner);
        mDirAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mDirs);
        mSpinner.setAdapter(mDirAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PhotoSelectFragment.mPresenter.getPhotos(mDirs.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mViewPager = findViewById(R.id.main_viewpager);
        mFragments = new ArrayList<>();
        PhotoSelectFragment photoSelectFragment = new PhotoSelectFragment();
        OcrHistoryFragment ocrHistoryFragment = new OcrHistoryFragment();
        mFragments.add(photoSelectFragment);
        mFragments.add(ocrHistoryFragment);
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

        mBottomNavigationView = findViewById(R.id.main_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_pic:
                        mSpinner.setVisibility(View.VISIBLE);
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_camera:
                        takePhoto();
                        return true;
                    case R.id.navigation_history:
                        mSpinner.setVisibility(View.INVISIBLE);
                        mViewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(lastFragmentIndex);
        mBottomNavigationView.setSelectedItemId(lastFragmentIndex == 0 ? R.id.navigation_pic : R.id.navigation_history);
        Log.d("main", "main: resume = " + mViewPager.getCurrentItem());
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastFragmentIndex = mViewPager.getCurrentItem();
        Log.d("main", "main:  = " + mViewPager.getCurrentItem());
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

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDirs.clear();
            ArrayList<String> list = msg.getData().getStringArrayList("urls");
            mDirs.addAll(list);
            mDirAdapter.notifyDataSetChanged();
        }
    };



}
