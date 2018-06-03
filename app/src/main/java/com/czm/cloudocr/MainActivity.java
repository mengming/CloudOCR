package com.czm.cloudocr;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.cloudocr.Login.LoginActivity;
import com.czm.cloudocr.OcrHistory.OcrHistoryFragment;
import com.czm.cloudocr.PhotoHandle.PhotoHandleActivity;
import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;
import com.czm.cloudocr.Settings.SettingsActivity;
import com.czm.cloudocr.TextResult.WordSearchDialog;
import com.czm.cloudocr.model.HistoryResult;
import com.czm.cloudocr.widget.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.czm.cloudocr.util.MyConstConfig.TAKE_PHOTO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private BottomNavigationView mBottomNavigationView;
    private MyViewPager mViewPager;
    private PopupWindow mPopup;
    private TextView mTitle;
    private ImageView mArrow;

    private ArrayList<Fragment> mFragments;
    private PhotoSelectFragment mPhotoSelectFragment;
    private List<String> mDirs = new ArrayList<>();
    private ArrayAdapter<String> mDirAdapter;
    private Uri imageUri;

    private int lastFragmentIndex = 0;
    private int lastPathIndex = 0;
    private long[] mHits = new long[3];

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<String> list = msg.getData().getStringArrayList("urls");
            mTitle.setText(list.get(lastPathIndex));
            mDirs.clear();
            mDirs.addAll(list);
            mDirAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                    if (mHits.length >= 3) mPhotoSelectFragment.setAdvanced(false);
                }
            }
        });
        setSupportActionBar(toolbar);
        mViewPager = findViewById(R.id.main_viewpager);
        mFragments = new ArrayList<>();
        mPhotoSelectFragment = new PhotoSelectFragment();
        mFragments.add(mPhotoSelectFragment);
        mFragments.add(new OcrHistoryFragment());
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

        mTitle = findViewById(R.id.main_tv_path);
        mArrow = findViewById(R.id.main_arrow);
        View background = View.inflate(this, R.layout.popup_background, null);
        mPopup = new PopupWindow(background, 600, 800);
        ListView listView = background.findViewById(R.id.path_list);
        mDirAdapter = new ArrayAdapter<>(this, R.layout.item_path, mDirs);
        listView.setAdapter(mDirAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastPathIndex = position;
                mTitle.setText(mDirs.get(lastPathIndex));
                PhotoSelectFragment.mPresenter.getPhotos(mDirs.get(lastPathIndex));
                mPopup.dismiss();
            }
        });

        mBottomNavigationView = findViewById(R.id.main_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_pic:
                        mArrow.setVisibility(View.VISIBLE);
                        if (mDirs.size() != 0) mTitle.setText(mDirs.get(lastPathIndex));
                        mTitle.setOnClickListener(MainActivity.this);
                        mArrow.setOnClickListener(MainActivity.this);
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_camera:
                        takePhoto();
                        return true;
                    case R.id.navigation_history:
                        mArrow.setVisibility(View.INVISIBLE);
                        if (mPopup.isShowing()) mPopup.dismiss();
                        mTitle.setText("识别历史");
                        mTitle.setOnClickListener(null);
                        mArrow.setOnClickListener(null);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastFragmentIndex = mViewPager.getCurrentItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO :
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, PhotoHandleActivity.class);
                    intent.putExtra("photo", imageUri.toString());
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takePhoto(){
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM)
                + "/" + System.currentTimeMillis() + ".jpg");
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                "com.czm.cloudocr.provider", file);

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public boolean isAdvanced(){
        return mPhotoSelectFragment.isAdvanced();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv_path :
            case R.id.main_arrow :
                if (mPopup.isShowing()) {
                    mPopup.dismiss();
                } else {
                    mPopup.showAsDropDown(mTitle, (mTitle.getWidth()-600)/2, 0);
                }
                break;
        }
    }
}
