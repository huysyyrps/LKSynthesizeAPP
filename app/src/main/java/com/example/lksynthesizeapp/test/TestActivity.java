package com.example.lksynthesizeapp.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;

import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class TestActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    MediaProjectionManager mMediaProjectionManager;
    MediaProjection mediaProjection;
    VirtualDisplay mVirtualDisplay;
    ImageReader mImageReader;
    Image image = null;

    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    @BindView(R.id.webView)
    MyWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        webView.loadUrl("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4");
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            // 已经申请过权限，做想做的事
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (null == mMediaProjectionManager) {
                //TODO 错误处理
                return;
            }
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.TAG_ONE);
        } else {
            // 没有申请过权限，现在去申请
            EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
        }
    }

    // 实现“onRequestPermissionsResult”函数接收校验权限结果。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // 授予权限后操作
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (null == mMediaProjectionManager) {
            //TODO 错误处理
            return;
        }
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.TAG_ONE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 请求权限被拒
        Toast.makeText(TestActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backdata) {
        super.onActivityResult(requestCode, resultCode, backdata);
        switch (requestCode) {
            case Constant.TAG_ONE:
                if (resultCode == Activity.RESULT_OK) {
                    mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
                    if (mediaProjection == null) {
                        return;
                    }
                    startRecord();
                } else {
                    finish();
                }
                break;
        }
    }

    private void startRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mediaProjection != null) {
                    //最少2张
                    mImageReader = ImageReader.newInstance(640, 480, PixelFormat.RGBA_8888, 2);
                    mVirtualDisplay = mediaProjection.createVirtualDisplay("screen", 640, 480, 720,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mImageReader.getSurface(), null, null);
                    getBuffer();
                }
            }
        }).start();
    }

    private void getBuffer() {
        if (null != mVirtualDisplay && null != mImageReader) {
            //当画面没有差异时不会返回数据
            try {
                image = mImageReader.acquireLatestImage();
            } catch (UnsupportedOperationException e){
                //TODO 终止线程并反馈错误
                //MX3 NB！
            }

//            if (null == image){
//                continue;
//            }

            //提取数据
            int width = image.getWidth();
            int height = image.getHeight();
            Image.Plane[] planes = image.getPlanes();
//            if (null == planes || planes.length <= 0 || null == planes[0]) {
//                image.close();
//                continue;
//            }

            ByteBuffer buffer = planes[0].getBuffer();
//            if (null == buffer){
//                image.close();
//                continue;
//            }

            //在ByteBuffer存放的数据存在像素间隔，如果不做考虑画面会不全
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int newWidth = width + ((rowStride - pixelStride * width) / pixelStride);

            //转码容器
            byte[] yuv = new byte[width * height * 3 >> 1 ];

//            //将rgb转为YUV420，此处做了一次裁剪，裁掉ByteBuffer中像素间隔中的数据
//            Convert.ABGRToI420Clip(buffer, newWidth, height, yuvWidth, yuvHeight, yuv);

            //TODO 转发给编码线程

            image.close();
        }
    }
}