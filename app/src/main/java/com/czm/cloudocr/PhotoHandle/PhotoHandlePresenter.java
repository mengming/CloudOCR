package com.czm.cloudocr.PhotoHandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.czm.cloudocr.PhotoSelect.PhotoSelectFragment;
import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.HttpUtils;
import com.czm.cloudocr.util.PdfBackground;
import com.czm.cloudocr.util.SystemUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoHandlePresenter implements PhotoHandleContract.Presenter {

    private static final String TAG = "PhotoHandlePresenter";
    private PhotoHandleContract.View mPhotoHandleView;
    private Context mContext;
    public static final int BORDER_WIDTH = 10;

    public PhotoHandlePresenter(PhotoHandleContract.View photoHandleView, Context context) {
        mPhotoHandleView = photoHandleView;
        mContext = context;
        mPhotoHandleView.setPresenter(this);
    }

    @Override
    public void compressPic(Uri uri) {
        try {
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "account" + DataSupport.count(PhotoResult.class) + ".jpg");
            Log.d(TAG, "compressPic: uri=" + file.toURI().toString());
            Bitmap mBitmap = SystemUtils.compressImage(bitmap, file);
            Log.d(TAG, "compress:" + DataSupport.count(PhotoResult.class));
            Log.d(TAG, "compressPic: " + file.length()/1024 + "kb");
            sendPic(file, uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPic(File file, Uri uri) throws IOException{
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myFile", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("username", "mengming");
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://192.168.199.234:8080/imgUpload")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: e = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//            JSONObject object = new JSONObject();
//            PhotoResult photoResult = new PhotoResult(file.toURI().toString(), uri.toString(), )
//            savePic();

                }
                Log.d(TAG, "sendPic: body = " + response.body().string());
                Log.d(TAG, "sendPic: code = " + response.code());
            }
        });

    }

    @Override
    public void savePic(PhotoResult result) {
        result.saveThrows();
        mPhotoHandleView.showText(result);
    }

    @Override
    public void savePdf(Uri uri) {
        try {
            createPdf(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/test.pdf", uri.getPath());
            mPhotoHandleView.openPdf(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/test.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图片生成PDF
     *
     * @param pdfPath 生成的PDF文件的路径
     * @param imagePath 待生成PDF文件的图片集合
     * @throws IOException 可能出现的IO操作异常
     * @throws DocumentException PDF生成异常
     */
    private void createPdf(String pdfPath, String imagePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

        //设置pdf背景
        PdfBackground event = new PdfBackground();
        writer.setPageEvent(event);

        document.open();
        document.newPage();
        Image img = Image.getInstance(imagePath);
        //设置图片缩放到A4纸的大小
        img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
        //设置图片的显示位置（居中）
        img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
        document.add(img);
        document.close();
    }

}
