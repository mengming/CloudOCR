package com.czm.cloudocr.TextResult;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czm.cloudocr.R;
import com.czm.cloudocr.util.SystemUtils;

import java.util.List;

public class WordSearchDialog extends Dialog {

    private Context mContext;
    private LinearLayout mWordLinear;
    private List<String> mStrings;
    private int filledWidth;
    private int defDistance;
    private int row;

    private static final String TAG = "WordSearchDialog";

    public WordSearchDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public WordSearchDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public WordSearchDialog(Context context, int themeResId, List<String> strings){
        super(context, themeResId);
        mContext = context;
        mStrings = strings;
    }

    protected WordSearchDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_word, null);
        setContentView(view);

        mWordLinear = findViewById(R.id.words_container);

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = width;
        win.setAttributes(lp);
        this.setTitle("以下是搜索到的关键词");

        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearLp);
        mWordLinear.addView(linearLayout);
        defDistance = SystemUtils.dip2px(mContext, 8);
        row = 0;
        for (final String word : mStrings) {
            View itemView = View.inflate(mContext, R.layout.item_word, null);
            TextView wordTv = itemView.findViewById(R.id.word_tv);
            wordTv.setText(word);
            wordTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.baidu.com/s?wd=" + word);
                    intent.setData(content_url);
                    mContext.startActivity(intent);
                }
            });
            itemView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            if ((filledWidth + itemView.getMeasuredWidth() + 3 * defDistance) > width) {
                filledWidth = 0;
                linearLayout = new LinearLayout(mContext);
                linearLayout.setLayoutParams(linearLp);
                mWordLinear.addView(linearLayout);
            }
            Log.d(TAG, "onCreate: filledWidth = " + filledWidth);
            LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //margin和padding不计入measuredWidth
            if (filledWidth == 0) {
                row++;
                tvLp.setMargins(defDistance, (row == 1 ? defDistance : 0), defDistance, defDistance);
                filledWidth += (itemView.getMeasuredWidth() + 4 * defDistance);
            } else {
                tvLp.setMargins(0, (row == 1 ? defDistance : 0), defDistance, defDistance);
                filledWidth += (itemView.getMeasuredWidth() + 3 * defDistance);
            }
            itemView.setPadding(defDistance, defDistance, defDistance, defDistance);
            linearLayout.addView(itemView, tvLp);
        }
    }
}
