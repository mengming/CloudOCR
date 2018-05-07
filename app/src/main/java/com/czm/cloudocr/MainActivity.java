package com.czm.cloudocr;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private long startTime, endTime;
    private final static int SCAN_OK = 1;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> urls = new ArrayList<>();
    private PhotoSelectPresenter mPhotoSelectPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.main_viewpager);
        mFragments = new ArrayList<>();
        PhotoSelectFragment photoSelectFragment = new PhotoSelectFragment();
        mFragments.add(photoSelectFragment);
        mPhotoSelectPresenter = new PhotoSelectPresenter(this,photoSelectFragment);
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
                    case R.id.navigation_call:
//                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_message:
//                        mViewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }


}
