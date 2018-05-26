package com.czm.cloudocr.Translate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;
import com.czm.cloudocr.R;

import java.util.HashMap;

public class TranslateActivity extends AppCompatActivity implements TranslateContract.View, View.OnClickListener {

    private EditText fromEt, toEt;
    private TextView fromLan, toLan;
    private String fromText, toText;
    private Button mBtnTrans;
    private PopupWindow mPopup;

    private TranslateContract.Presenter mPresenter;

    private HashMap<String, String> mLanMap;
    private String[] lanText, lanCode;
    private ArrayAdapter<String> mLanAdapter;
    private String fromCode, toCode;
    private static final String TAG = "TranslateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Toolbar toolbar = findViewById(R.id.translate_toolbar);
        toolbar.setTitle("翻译");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fromEt = findViewById(R.id.trans_from_et);
        toEt = findViewById(R.id.trans_to_et);
        fromLan = findViewById(R.id.trans_from_lan);
        toLan = findViewById(R.id.trans_to_lan);
        mBtnTrans = findViewById(R.id.trans_btn);
        mBtnTrans.setOnClickListener(this);
        fromLan.setOnClickListener(this);
        toLan.setOnClickListener(this);

        fromText = getIntent().getStringExtra("from_text");
        fromEt.setText(fromText);

        mLanMap = new HashMap<>();
        lanText = getResources().getStringArray(R.array.language_text);
        lanCode = getResources().getStringArray(R.array.language_code);
        for (int i = 0; i < lanCode.length; i++) {
            mLanMap.put(lanText[i], lanCode[i]);
        }
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
                if (fromCode == null || toCode == null) {
                    Toast.makeText(TranslateActivity.this, "请选择语言", Toast.LENGTH_SHORT).show();
                } else if (fromEt.getText().toString().equals("")) {
                    Toast.makeText(TranslateActivity.this, "翻译文本不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    mPresenter.send(fromEt.getText().toString(), fromCode, toCode);
                }
                break;
            case R.id.trans_from_lan:
                if (mPopup != null && mPopup.isShowing()) {
                    mPopup.dismiss();
                } else {
                    showPopup(true);
                }
                break;
            case R.id.trans_to_lan:
                if (mPopup != null && mPopup.isShowing()) {
                    mPopup.dismiss();
                } else {
                    showPopup(false);
                }
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

    private void showPopup(final boolean isFrom){
        View background = View.inflate(this, R.layout.popup_background, null);
        mPopup = new PopupWindow(background, 500, 600);
        ListView listView = background.findViewById(R.id.path_list);
        mLanAdapter = new ArrayAdapter<>(this, R.layout.item_path, lanText);
        listView.setAdapter(mLanAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFrom) {
                    fromLan.setText(lanText[position]);
                    fromCode = mLanMap.get(lanText[position]);
                    Log.d(TAG, "onItemClick: from = " + fromCode);
                } else {
                    toLan.setText(lanText[position]);
                    toCode = mLanMap.get(lanText[position]);
                    Log.d(TAG, "onItemClick: to = " + toCode);
                }
                mPopup.dismiss();
            }
        });
        mPopup.showAsDropDown((isFrom ? fromLan : toLan) , ((isFrom ? fromLan : toLan).getWidth()-500)/2, 0);
    }

}
