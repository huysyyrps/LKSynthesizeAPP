package com.example.lksynthesizeapp.Constant.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Activity.BroadcastMediaCodecActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.DescernActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.LocalActivity;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.DialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.Net.SSHCallBack;
import com.example.lksynthesizeapp.Constant.Net.SSHExcuteCommandHelper;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.View.StatusBarUtils;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 磁粉检测上传方式选择页
 */
public class SendSelectActivity extends AppCompatActivity {
    @BindView(R.id.tv_tittle)
    TextView tvTittle;
    @BindView(R.id.relativeLayoutHeader)
    RelativeLayout relativeLayoutHeader;
    @BindView(R.id.etProject)
    EditText etProject;
    @BindView(R.id.etWorkName)
    EditText etWorkName;
    @BindView(R.id.etWorkCode)
    EditText etWorkCode;
    @BindView(R.id.linLocak)
    LinearLayout linLocak;
    @BindView(R.id.linSocket)
    LinearLayout linSocket;
    @BindView(R.id.linDiscern)
    LinearLayout linDiscern;
    //富有动感的Sheet弹窗
    Intent intent;
    private String address = "";
    private static AlertDialogUtil alertDialogUtil;
    SharePreferencesUtils sharePreferencesUtils;
    MediaProjectionManager projectionManager;
    private WifiManager mWifiManager;
    private String sid = "", pwd = "", max = "";
    private WifiManager wifiMgr;
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    //推出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new ExitApp().exit(alertDialogUtil, SendSelectActivity.this);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_select);
        ButterKnife.bind(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        alertDialogUtil = new AlertDialogUtil(this);
        new StatusBarUtils().setWindowStatusBarColor(SendSelectActivity.this, R.color.color_bg_selected);
        new EditTextLengClient().textLeng(etProject, this);
        new EditTextLengClient().textLeng(etWorkCode, this);
        new EditTextLengClient().textLeng(etWorkName, this);
        alertDialogUtil.showWifiSetting(this, "office", sharePreferencesUtils.getString(SendSelectActivity.this, "max", ""), new DialogCallBack() {
            @Override
            public void confirm(String data, Dialog dialog) {
//                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
//                startActivity(intent);
            }

            @Override
            public void cancel() {

            }
        });
    }

    @OnClick({R.id.linLocak, R.id.linSocket, R.id.linDiscern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linLocak:
                seSPData();
                SelectActivity("本地存储");
                break;
            case R.id.linSocket:
                seSPData();
                SelectActivity("实时上传");
                break;
            case R.id.linDiscern:
                seSPData();
                SelectActivity("在线检测");
                break;
        }
    }

    public void seSPData(){
        sharePreferencesUtils.setString(SendSelectActivity.this, "project", etProject.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workName", etWorkName.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workCode", etWorkCode.getText().toString());
    }

    public void SelectActivity(String data) {
        if (etProject.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工程名称", Toast.LENGTH_SHORT).show();
        } else if (etWorkName.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工件名称", Toast.LENGTH_SHORT).show();
        } else if (etWorkCode.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工件编号", Toast.LENGTH_SHORT).show();
        } else {
            if (data.equals("本地存储")) {
                sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "本地存储");
                intent = new Intent(SendSelectActivity.this, LocalActivity.class);
                intent.putExtra("project", etProject.getText().toString().trim());
                intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
                intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
                startActivity(intent);
            } else if (data.equals("实时上传")) {
                sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "实时上传");
                intent = new Intent(SendSelectActivity.this, BroadcastMediaCodecActivity.class);
                intent.putExtra("project", etProject.getText().toString().trim());
                intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
                intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
                startActivity(intent);
            }else if (data.equals("在线检测")) {
                sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "实时上传");
                intent = new Intent(SendSelectActivity.this, DescernActivity.class);
                intent.putExtra("project", etProject.getText().toString().trim());
                intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
                intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
                startActivity(intent);
            }
        }
    }

    //获取设备基础信息
    public void getNewData() {
        try {
            address = new getIp().getConnectIp();
            new Thread(new Runnable() {
                @Override
                public void run() {
//                            cat /data.json  /write_data.sh
                    SSHExcuteCommandHelper.writeBefor(address, "cat /data.json", new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            if (data != null && !data.equals("\n") && !data.equals("")) {
                                Gson gson = new Gson();
//                                Setting setting = gson.fromJson(data, Setting.class);
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "acdc", setting.getData().getAcdc());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "auto", setting.getData().getAuto());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "auto_time", setting.getData().getAuto_time());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "bw", setting.getData().getBw());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "id", setting.getData().getId());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "mac", setting.getData().getMac());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "power", setting.getData().getPower());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "ip", setting.getData().getIp());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "date", setting.getData().getDate());
//                                sharePreferencesUtils.setString(SendSelectActivity.this, "mode", setting.getData().getMode());
                            }
                        }

                        @Override
                        public void error(String s) {
//                            Toast.makeText(SendSelectActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}