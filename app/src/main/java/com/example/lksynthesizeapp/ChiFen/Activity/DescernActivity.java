package com.example.lksynthesizeapp.ChiFen.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.YoloV5Ncnn;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DescernActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE = 1;
    private String url;
    private ImageView imageView;
    private Bitmap bmp = null;
    private Bitmap bitmap = null;
    private Bitmap yourSelectedImage = null;
    private Thread mythread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    Paint paint;
    Paint textbgpaint;
    Paint textpaint;
    private int w;
    private int h;
    private float scanw;
    private float scanh;
    private MediaPlayer mediaPlayer;
    long currentTme = 0;
    long currentTme1 = 0;
    private String address = "";
    public boolean runing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_descern);
        mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.fengming);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.GREEN);

        textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);

        textpaint = new Paint();
        textpaint.setColor(Color.GREEN);
        textpaint.setTextSize(13);
        textpaint.setTextAlign(Paint.Align.LEFT);
//        w = getWindowManager().getDefaultDisplay().getWidth();
//        h = getWindowManager().getDefaultDisplay().getHeight();
//        scanw = w/364;
//        scanh = h/237;
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "yolov5ncnn Init failed");
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        try {
            address = new getIp().getConnectIp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(address == null){
            Toast.makeText(this, "获取IP为空", Toast.LENGTH_SHORT).show();
        }else {
            url = "http://"+ address + ":8080?action=snapshot";
            mythread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runing) {
                        draw();
                        currentTme = System.currentTimeMillis();
                    }
                }
            });
            mythread.start();
        }
    }

    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            videoUrl = new URL(url);
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection) videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
//            bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//            bitmap = imageScale(bitmap, 364,237);
            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false);
            showObjects(objects);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
        }
    }

    /**
     * 调整图片大小
     * @param bitmap 源
     * @param dst_w  输出宽度
     * @param dst_h  输出高度
     * @return
     */
    public Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
        return dstbmp;
    }


    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null) {
            imageView.setImageBitmap(bmp);
//            currentTme1 = System.currentTimeMillis();
//            Log.e("XXX",(currentTme1-currentTme)+"");
            return;
        }

        // draw objects on bitmap
        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(rgba);

        for (int i = 0; i < objects.length; i++) {
            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, paint);
            // draw filled text inside image
            {
                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";

                float text_width = textpaint.measureText(text)+10;
                float text_height = -textpaint.ascent() + textpaint.descent()+10;

                float x = objects[i].x;
                float y = objects[i].y - text_height;
                if (y < 0)
                    y = 0;
                if (x + text_width > rgba.getWidth())
                    x = rgba.getWidth() - text_width;
//                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);
                canvas.drawText(text, x, y - textpaint.ascent(), textpaint);
            }
        }
        if (objects.length!=0){
            mediaPlayer.start();
        }
        imageView.setImageBitmap(rgba);
//        currentTme1 = System.currentTimeMillis();
//        Log.e("XXX",(currentTme1-currentTme)+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mythread.interrupt();
        runing = false;
    }
}
