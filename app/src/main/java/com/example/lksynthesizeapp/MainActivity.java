//package com.example.lksynthesizeapp;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.media.ExifInterface;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.WindowManager;
//import android.widget.ImageView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class MainActivity extends AppCompatActivity {
//    private static final int SELECT_IMAGE = 1;
//    private String url;
//    private ImageView imageView;
//    private Bitmap bmp = null;
//    private Bitmap bitmap = null;
//    private Bitmap yourSelectedImage = null;
//    private Thread mythread;
//    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
//    URL videoUrl;
//    HttpURLConnection conn;
//    Paint paint;
//    Paint textbgpaint;
//    Paint textpaint;
//    private int w;
//    private int h;
//    private float scanw;
//    private float scanh;
//    final int[] colors = new int[]{
//            Color.rgb(54, 67, 244),
//            Color.rgb(99, 30, 233),
//            Color.rgb(176, 39, 156),
//            Color.rgb(183, 58, 103),
//            Color.rgb(181, 81, 63),
//            Color.rgb(243, 150, 33),
//            Color.rgb(244, 169, 3),
//            Color.rgb(212, 188, 0),
//            Color.rgb(136, 150, 0),
//            Color.rgb(80, 175, 76),
//            Color.rgb(74, 195, 139),
//            Color.rgb(57, 220, 205),
//            Color.rgb(59, 235, 255),
//            Color.rgb(7, 193, 255),
//            Color.rgb(0, 152, 255),
//            Color.rgb(34, 87, 255),
//            Color.rgb(72, 85, 121),
//            Color.rgb(158, 158, 158),
//            Color.rgb(139, 125, 96)
//    };
//    private MediaPlayer mediaPlayer;
//    long currentTme = 0;
//    long currentTme1 = 0;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        ActionBar actionbar = getActionBar();
////        actionbar.hide();
//        //不息屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        // 设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //隐藏底部按钮
//        new BottomUI().hideBottomUIMenu(this.getWindow());
//        setContentView(R.layout.activity_main);
//        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.fengming);
//        paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(2);
//        paint.setColor(Color.CYAN);
//
//        textbgpaint = new Paint();
//        textbgpaint.setColor(Color.WHITE);
//        textbgpaint.setStyle(Paint.Style.FILL);
//
//        textpaint = new Paint();
//        textpaint.setColor(Color.BLACK);
//        textpaint.setTextSize(13);
//        textpaint.setTextAlign(Paint.Align.LEFT);
////        w = getWindowManager().getDefaultDisplay().getWidth();
////        h = getWindowManager().getDefaultDisplay().getHeight();
////        scanw = w/364;
////        scanh = h/237;
//        boolean ret_init = yolov5ncnn.Init(getAssets());
//        if (!ret_init) {
//            Log.e("MainActivity", "yolov5ncnn Init failed");
//        }
//        imageView = (ImageView) findViewById(R.id.imageView);
//        url = "http://192.168.43.104:8080?action=snapshot";
//        mythread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    draw();
//                    currentTme = System.currentTimeMillis();
//                }
//            }
//        });
//        mythread.start();
//    }
//
//    private void draw() {
//        // TODO Auto-generated method stub
//        try {
//            InputStream inputstream = null;
//            //创建一个URL对象
//            videoUrl = new URL(url);
//            //利用HttpURLConnection对象从网络中获取网页数据
//            conn = (HttpURLConnection) videoUrl.openConnection();
//            //设置输入流
//            conn.setDoInput(true);
//            //连接
//            conn.connect();
//            //得到网络返回的输入流
//            inputstream = conn.getInputStream();
//            //创建出一个bitmap
//            bmp = BitmapFactory.decodeStream(inputstream);
////            bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//            currentTme1 = System.currentTimeMillis();
//            Log.e("XXX",(currentTme1-currentTme)+"");
////            bitmap = imageScale(bitmap, 364,237);
//            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false);
//            showObjects(objects);
//            //关闭HttpURLConnection连接
//            conn.disconnect();
//        } catch (Exception ex) {
//            Log.e("XXX", ex.toString());
//        } finally {
//        }
//    }
//
//    /**
//     * 调整图片大小
//     *
//     * @param bitmap 源
//     * @param dst_w  输出宽度
//     * @param dst_h  输出高度
//     * @return
//     */
//    public Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
//        int src_w = bitmap.getWidth();
//        int src_h = bitmap.getHeight();
//        float scale_w = ((float) dst_w) / src_w;
//        float scale_h = ((float) dst_h) / src_h;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale_w, scale_h);
//        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
//        return dstbmp;
//    }
//
//
//    private void showObjects(YoloV5Ncnn.Obj[] objects) {
//        if (objects == null) {
//            imageView.setImageBitmap(bmp);
//            return;
//        }
//
//        // draw objects on bitmap
//        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);
//
//
//        Canvas canvas = new Canvas(rgba);
//
//        for (int i = 0; i < objects.length; i++) {
////            canvas.drawRect(objects[i].x*scanw, objects[i].y*scanh, (objects[i].x + objects[i].w)*scanw, (objects[i].y + objects[i].h)*scanh, paint);
//            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, paint);
////            // draw filled text inside image
////            {
////                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";
////
////                float text_width = textpaint.measureText(text);
////                float text_height = -textpaint.ascent() + textpaint.descent();
////
////                float x = objects[i].x;
////                float y = objects[i].y - text_height;
////                if (y < 0)
////                    y = 0;
////                if (x + text_width > rgba.getWidth())
////                    x = rgba.getWidth() - text_width;
////                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);
////                canvas.drawText(text, x, y - textpaint.ascent(), textpaint);
////            }
//        }
//        if (objects.length!=0){
//            mediaPlayer.start();
//        }
//        imageView.setImageBitmap(rgba);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//
//            try {
//                if (requestCode == SELECT_IMAGE) {
//                    bitmap = decodeUri(selectedImage);
//
//                    yourSelectedImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//                    imageView.setImageBitmap(bitmap);
//                }
//            } catch (FileNotFoundException e) {
//                Log.e("MainActivity", "FileNotFoundException");
//                return;
//            }
//        }
//    }
//
//    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
//
//        // The new size we want to scale to
//        final int REQUIRED_SIZE = 640;
//
//        // Find the correct scale value. It should be the power of 2.
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp / 2 < REQUIRED_SIZE
//                    || height_tmp / 2 < REQUIRED_SIZE) {
//                break;
//            }
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
//
//        // Rotate according to EXIF
//        int rotate = 0;
//        try {
//            ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(selectedImage));
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotate = 270;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotate = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotate = 90;
//                    break;
//            }
//        } catch (IOException e) {
//            Log.e("MainActivity", "ExifInterface IOException");
//        }
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotate);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }
//
//}
