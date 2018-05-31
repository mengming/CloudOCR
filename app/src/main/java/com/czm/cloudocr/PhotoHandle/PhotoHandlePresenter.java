package com.czm.cloudocr.PhotoHandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.czm.cloudocr.model.PhotoResult;
import com.czm.cloudocr.util.MyConstConfig;
import com.czm.cloudocr.util.PdfBackground;
import com.czm.cloudocr.util.SystemUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.czm.cloudocr.util.MyConstConfig.BORDER_WIDTH;

public class PhotoHandlePresenter implements PhotoHandleContract.Presenter {

    private static final String TAG = "PhotoHandlePresenter";
    private PhotoHandleContract.View mPhotoHandleView;
    private Context mContext;

    public PhotoHandlePresenter(PhotoHandleContract.View photoHandleView, Context context) {
        mPhotoHandleView = photoHandleView;
        mContext = context;
        mPhotoHandleView.setPresenter(this);
    }

    private File compressPic(Uri uri) {
        try {
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    mContext.getSharedPreferences("settings", MODE_PRIVATE).getString("account","")
                            + "_"+ DataSupport.count(PhotoResult.class) + ".jpg");
            Log.d(TAG, "compressPic: uri=" + file.toURI().toString());
            SystemUtils.compressImage(bitmap, file);
            Log.d(TAG, "compress:" + DataSupport.count(PhotoResult.class));
            Log.d(TAG, "compressPic: " + file.length()/1024 + "kb");
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sendPic(final Uri uri, boolean advanced) throws IOException{
        Log.d(TAG, "sendPic: advanced = " + advanced);
        mPhotoHandleView.waiting();
        final File file = compressPic(uri);
        if (file == null) return;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myFile", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("username", mContext.getSharedPreferences("settings", MODE_PRIVATE).getString("account",""));
        RequestBody requestBody = builder.build();
        final Request request = new Request.Builder()
                .url(MyConstConfig.SERVER_URL + (advanced ? "imgUploadAPI": "imgUpload"))
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mPhotoHandleView.ocrError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "sendPic: body = " + string);
                Log.d(TAG, "sendPic: code = " + response.code());
                if (response.code() == 200) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(PhotoResult.class, new TypeAdapter<PhotoResult>() {
                        @Override
                        public void write(JsonWriter out, PhotoResult value) throws IOException {

                        }

                        @Override
                        public PhotoResult read(JsonReader in) throws IOException {
                            final PhotoResult result = new PhotoResult();

                            in.beginObject();
                            while (in.hasNext()) {
                                switch (in.nextName()) {
                                    case "id":
                                        result.setRemoteId(in.nextString());
                                        break;
                                    case "text":
                                        result.setText(in.nextString());
                                        break;
                                    case "date":
                                        result.setDate(in.nextString());
                                        break;
                                    default:
                                        in.nextString();
                                        break;
                                }
                            }
                            in.endObject();
                            return result;
                        }
                    });
                    Gson gson = gsonBuilder.create();
                    PhotoResult result = gson.fromJson(string, PhotoResult.class);
                    result.setUri(file.toURI().toString());
                    result.setRootUri(uri.toString());
                    result.setCloud(true);
                    savePic(result);
                    Log.d(TAG, "onResponse: " + result.toString());
                }
            }
        });

    }

    @Override
    public void savePic(PhotoResult result) {
        result.saveThrows();
        mPhotoHandleView.showText(result);
    }

    @Override
    public void savePdf(Uri uri, String name) {
        try {
            createPdf(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/" + name + ".pdf", uri.getPath());
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
