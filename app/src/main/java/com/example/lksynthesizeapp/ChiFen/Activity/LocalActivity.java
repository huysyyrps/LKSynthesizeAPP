package com.example.lksynthesizeapp.ChiFen.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lksynthesizeapp.BuildConfig;
import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.GetDate;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Media.AudioEncodeConfig;
import com.example.lksynthesizeapp.ChiFen.Media.CreateConfig;
import com.example.lksynthesizeapp.ChiFen.Media.GetRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.MediaCallBack;
import com.example.lksynthesizeapp.ChiFen.Media.Notifications;
import com.example.lksynthesizeapp.ChiFen.Media.ScreenRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.VideoEncodeConfig;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.ProgressDialogUtil;
import com.example.lksynthesizeapp.Constant.Net.SSHCallBack;
import com.example.lksynthesizeapp.Constant.Net.SSHExcuteCommandHelper;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbSound)
    RadioButton rbSound;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
    @BindView(R.id.rbRefresh)
    RadioButton rbRefresh;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tvCompName)
    TextView tvCompName;
    @BindView(R.id.tvWorkName)
    TextView tvWorkName;
    @BindView(R.id.tvWorkCode)
    TextView tvWorkCode;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @BindView(R.id.linearLayoutStop)
    LinearLayout linearLayoutStop;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.linlayoutData)
    LinearLayout linlayoutData;

    private Toast toast;
    String address;
    String toastData = "";
    private int mWindowWidth;
    private int mWindowHeight;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private WindowManager mWindowManager;
    private ScreenRecorder mRecorder;
    private Notifications mNotifications;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private String project = "", workName = "", workCode = "";
    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";

    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    private Thread mythread;
    URL videoUrl;
    HttpURLConnection conn;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setWorkData();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.densityDpi;
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, 0x1, 2);
        frameLayout.setBackgroundColor(getResources().getColor(R.color.black));
        //全屏设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mNotifications = new Notifications(getApplicationContext());
        if (mMediaProjection == null) {
            requestMediaProjection();
        }


        try {
            address = new getIp().getConnectIp();
            address = "http://" + address + ":8080?action=snapshot";
            mythread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        draw();
                    }
                }
            });
            mythread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            videoUrl = new URL(address);
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection) videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bitmap = BitmapFactory.decodeStream(inputstream);
            handlerSetting.sendEmptyMessage(Constant.TAG_THERE);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
        }
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_local;
    }

    @Override
    protected boolean isHasHeader() {
        return false;
    }

    @Override
    protected void rightClient() {

    }

    private void setWorkData() {
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            linlayoutData.setVisibility(View.GONE);
        }
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }
    }

    @OnClick({R.id.rbCamera, R.id.rbSound, R.id.rbAlbum, R.id.rbRefresh, R.id.linearLayoutStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
                radioGroup.setVisibility(View.GONE);
                if (toast != null) {
                    toast.cancel();
                }
                new BottomUI().hideBottomUIMenu(this.getWindow());
                if (mMediaProjection != null) {
                    setUpVirtualDisplay();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startCapture();
                        }
                    }, 200);
                }


//                radioGroup.setVisibility(View.GONE);
//                new BottomUI().hideBottomUIMenu(this.getWindow());
//                View imageView = view.getRootView();
//                imageView.setDrawingCacheEnabled(true);
//                imageView.buildDrawingCache();
//                Bitmap mBitmap = imageView.getDrawingCache();
//                boolean backstate = new ImageSave().saveBitmap(project, workName, workCode, radioGroup, this, mBitmap);
//                if (backstate) {
//                    radioGroup.setVisibility(View.VISIBLE);
//                    new BottomUI().BottomUIMenu(this.getWindow());
//                    Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
//                } else {
//                    radioGroup.setVisibility(View.VISIBLE);
//                    new BottomUI().BottomUIMenu(this.getWindow());
//                    Toast.makeText(this, R.string.save_faile, Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.rbSound:
                if (EasyPermissions.hasPermissions(this, PERMS)) {
                    // 已经申请过权限，做想做的事
                    if (mMediaProjection == null) {
                        requestMediaProjection();
                    } else {
                        new TirenSet().checkTirem(ivTimer);
                        startCapturing(mMediaProjection);
                    }
                } else {
                    // 没有申请过权限，现在去申请
                    EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                }
                break;
            case R.id.linearLayoutStop:
                new BottomUI().BottomUIMenu(this.getWindow());
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                new BottomUI().BottomUIMenu(this.getWindow());
                if (mRecorder != null) {
                    stopRecordingAndOpenFile();
                }
                break;
            case R.id.rbAlbum:
                new MainUI().showPopupMenu(rbAlbum, this);
                break;
            case R.id.rbRefresh:
//                ShowDialog("/etc/init.d/mjpg-streamer restart");
                ShowDialog("uci set mjpg-streamer.core.fps=20", "uci commit", "/etc/init.d/mjpg-streamer restart");
                break;
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWindowWidth, mWindowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Log.e("MainActivity", "image is null.");
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(buffer);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);
        image.close();
        stopScreenCapture();
        if (mBitmap != null) {
            boolean backstate = new ImageSave().saveBitmap(project, workName, workCode, radioGroup, this, mBitmap);
            if (backstate) {
                radioGroup.setVisibility(View.VISIBLE);
                new BottomUI().BottomUIMenu(this.getWindow());
                toast = Toast.makeText(LocalActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                radioGroup.setVisibility(View.VISIBLE);
                new BottomUI().BottomUIMenu(this.getWindow());
                toast = Toast.makeText(LocalActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            System.out.println("bitmap is NULL!");
        }
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
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
        requestMediaProjection();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 请求权限被拒
        Toast.makeText(LocalActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    //创建申请录屏的 Intent
    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Constant.TAG_ONE);
    }

    private void startCapturing(MediaProjection mediaProjection) {
        radioGroup.setVisibility(View.GONE);
        new BottomUI().hideBottomUIMenu(this.getWindow());
        linearLayoutStop.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    LocalActivity.this.runOnUiThread(() -> {
                        VideoEncodeConfig video = new CreateConfig().createVideoConfig();
                        AudioEncodeConfig audio = new CreateConfig().createAudioConfig(); // audio can be null
                        if (video == null) {
                            Toast.makeText(LocalActivity.this, "Create ScreenRecorder failure", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        File dir = new File(Environment.getExternalStorageDirectory() + "/LUKEVideo/" + project + "/" + "设备/" + workName + "/" + workCode + "/");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, new GetDate().getNowDate() + ".mp4");
//                        checkTirem();
                        mRecorder = new GetRecorder().newRecorder(mediaProjection, video, audio, file, new MediaCallBack() {
                            @Override
                            public void onStop(Throwable error, File output) {
                                runOnUiThread(() -> stopRecorder());
                                if (error != null) {
                                    error.printStackTrace();
                                    output.delete();
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                            .addCategory(Intent.CATEGORY_DEFAULT)
                                            .setData(Uri.fromFile(output));
                                    sendBroadcast(intent);
                                }
                            }

                            @Override
                            public void onStart() {
                                mNotifications.recording(0);
                            }

                            @Override
                            public void onRecording(Long presentationTimeUs) {
                                long startTime = 0;
                                if (startTime <= 0) {
                                    startTime = presentationTimeUs;
                                }
                                long time = (presentationTimeUs - startTime) / 1000;
                                mNotifications.recording(time);
                            }
                        });
                        startRecorder();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startRecorder() {
        if (mRecorder == null) return;
        mRecorder.start();
        registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));
    }

    private void stopRecorder() {
        mNotifications.clear();
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
        try {
            unregisterReceiver(mStopActionReceiver);
        } catch (Exception e) {
            //ignored
        }
    }

    private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_STOP.equals(intent.getAction())) {
                stopRecordingAndOpenFile();
            }
        }
    };

    private void stopRecordingAndOpenFile() {
        stopRecorder();
        StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
        try {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        } finally {
            StrictMode.setVmPolicy(vmPolicy);
        }
    }

    /**
     * 重启服务刷新视频
     * @param data1
     */
    private void ShowDialog(String data1) {
        try {
            String address = new getIp().getConnectIp();
            ProgressDialogUtil.startLoad(this, "重启中");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowDialog(String data1, String data2, String data3) {
        try {
            String address = new getIp().getConnectIp();
            ProgressDialogUtil.startLoad(this, "重启中");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            SSHExcuteCommandHelper.writeBefor(address, data2, new SSHCallBack() {
                                @Override
                                public void confirm(String data) {
                                    SSHExcuteCommandHelper.writeBefor(address, data3, new SSHCallBack() {
                                        @Override
                                        public void confirm(String data) {
                                            handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                                        }

                                        @Override
                                        public void error(String s) {
                                            toastData = s;
                                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                        }
                                    });
                                }

                                @Override
                                public void error(String s) {
                                    toastData = s;
                                    handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                }
                            });
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backdata) {
        super.onActivityResult(requestCode, resultCode, backdata);
        switch (requestCode) {
            case Constant.TAG_ONE:
                if (resultCode == Activity.RESULT_OK) {
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
//                    new TirenSet().checkTirem(ivTimer);
//                    startCapturing(mMediaProjection);
                } else {
                    finish();
                }
                break;
        }
    }


    Handler handlerSetting = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TAG_ONE:
                    Toast.makeText(LocalActivity.this, "重启成功", Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.stopLoad();
                    address = "http://" + address + ":8080";
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(LocalActivity.this, toastData, Toast.LENGTH_LONG).show();
                    ProgressDialogUtil.stopLoad();
                    break;
                case Constant.TAG_THERE:
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };
}