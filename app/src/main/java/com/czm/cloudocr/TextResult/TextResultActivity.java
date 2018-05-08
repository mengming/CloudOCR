package com.czm.cloudocr.TextResult;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.czm.cloudocr.R;
import com.czm.cloudocr.model.PhotoResult;

public class TextResultActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, TextResultContract.View{

    private TextResultContract.Presenter mPresenter;
    private PhotoResult mPhotoResult;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_result);

        Toolbar toolbar = findViewById(R.id.text_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(this);

        mEditText = findViewById(R.id.result_edittext);

        mPhotoResult = (PhotoResult) getIntent().getSerializableExtra("photo_result");
        showText(mPhotoResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.text_proofread:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
        }

        return false;
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
    public void showText(PhotoResult photoResult) {
        mEditText.setText(photoResult.getText());
    }

    @Override
    public void comparePic() {

    }

    @Override
    public void setPresenter(TextResultContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
