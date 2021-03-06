package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.itemName;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.mItemImgs;
import static com.example.lksynthesizeapp.Constant.Base.Constant.CE_CONTROL;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MOB_DISTANCE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MOB_SPEED;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MOB_TIME;
import static com.example.lksynthesizeapp.Constant.Base.Constant.READ_FLOAT;
import static com.example.lksynthesizeapp.Constant.Base.Constant.READ_HEX;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TZD_CONTROL;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Modbus.BytesHexChange;
import com.example.lksynthesizeapp.ChiFen.Modbus.Crc16Util;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.SocketForModbusTCP;
import com.example.lksynthesizeapp.ChiFen.Modbus.SocketForModbusTCPIsNull;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotActivity extends BaseActivity {

    @BindView(R.id.webView)
    MyWebView webView;
    @BindView(R.id.ivVisition)
    ImageView ivVisition;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvCHTime)
    TextView tvCHTime;
    @BindView(R.id.tvActualDistance)
    TextView tvActualDistance;
    @BindView(R.id.tvCEControl)
    TextView tvCEControl;
    @BindView(R.id.tvLightSelect)
    TextView tvLightSelect;
    @BindView(R.id.tvSearchlightControl)
    TextView tvSearchlightControl;
    @BindView(R.id.tvCHControl)
    TextView tvCHControl;
    @BindView(R.id.linSetting)
    LinearLayout linSetting;
    @BindView(R.id.btnCEControl)
    Button btnCEControl;
    @BindView(R.id.btnCHControl)
    Button btnCHControl;
    @BindView(R.id.btnSearchlightControl)
    Button btnSearchlightControl;
    @BindView(R.id.btnLightSelect)
    Button btnLightSelect;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.ivGone)
    ImageView ivGone;
    @BindView(R.id.cm_main)
    CircleMenu cmMain;
    @BindView(R.id.btnStop)
    Button btnStop;
    String address = null;
    SocketForModbusTCP socketForModbusTCP;
    Message message;
    List<ItemInfo> data = new ArrayList<>();
    private CircleMenuAdapter circleMenuAdapternew;
    private boolean isConnect = false;
    private boolean isConfigure = true;
    private int TIME = 2000;
    Handler handlerConfigure = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // ????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //??????????????????
        new BottomUI().hideBottomUIMenu(this.getWindow());
        ButterKnife.bind(this);
        try {
            address = new getIp().getConnectIp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.setBackgroundColor(Color.BLACK);
        WebSettings WebSet = webView.getSettings();    //??????webview??????
        WebSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //???????????????
        //????????????????????????(??????????????????)
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        if (address != null) {
            webView.loadUrl("http://" + address + ":8080");
        } else {
            Toast.makeText(this, "IP??????", Toast.LENGTH_SHORT).show();
        }
        instanceModBus();
        setUiData();

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_robot;
    }

    @Override
    protected boolean isHasHeader() {
        return false;
    }

    @Override
    protected void rightClient() {

    }

    /**
     * ??????????????????
     */
    private void getConfigure() {
//        while (isConnect&&isConfigure){
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        handlerConfigure.postDelayed(this, TIME);
//                        if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
//                            socketForModbusTCP.sendData(READ_FLOAT + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(READ_FLOAT)), socketForModbusTCP);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            handlerConfigure.postDelayed(runnable, TIME); // ?????????????????????.
//        }
        socketForModbusTCP.sendData(READ_FLOAT + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(READ_FLOAT)), socketForModbusTCP);
    }

    /**
     * modbus????????????
     */
    private void instanceModBus() {
        if (address != null) {
            //172.16.16.171
            socketForModbusTCP = new SocketForModbusTCP(address, 502, this) {
                @Override
                protected void onDataReceived(byte[] readBuffer, int size) {
                    Message message = new Message();
                    message.obj = new BytesHexChange().bytes2hex(readBuffer);
                    message.what = Constant.TAG_THERE;
                    handler.sendMessage(message);
                    //todo ????????????????????????
                    if (readBuffer[1] == 3) {
                        //???????????????16??????  ??????10??????10??????16
                        if (readBuffer[2] == 10) {
                            //????????????
                            String actualDistance = new String(readBuffer,3,6);
                            Message message1 = new Message();
                            message1.what = TAG_THERE;
                            message1.obj = actualDistance;
                            handler.sendMessage(message1);
                            actualDistance = actualDistance.substring(4, 8) + actualDistance.substring(0, 4);
                            int intActualDistance = Integer.parseInt(actualDistance, 16);
                            //????????????????????????????????????float?????????????????????????????????IEEE754????????????????????????????????????????????????????????????
                            float floatActualDistance = Float.intBitsToFloat(intActualDistance);
                            tvActualDistance.setText(floatActualDistance + "mm");

                            //??????
                            String speen = new String(readBuffer,7,10);
                            speen = speen.substring(4, 8) + speen.substring(0, 4);
                            int intSpeen = Integer.parseInt(speen, 16);
                            //????????????????????????????????????float?????????????????????????????????IEEE754????????????????????????????????????????????????????????????
                            float floatSpeen = Float.intBitsToFloat(intSpeen);
                            tvSpeed.setText(floatSpeen + "mm");

                            //????????????
                            String distance = new String(readBuffer,11,14);
                            distance = distance.substring(4, 8) + distance.substring(0, 4);
                            int intDistance = Integer.parseInt(distance, 16);
                            //????????????????????????????????????float?????????????????????????????????IEEE754????????????????????????????????????????????????????????????
                            float floatDistance = Float.intBitsToFloat(intDistance);
                            tvDistance.setText(floatDistance + "mm");

                            //????????????
                            String time = new String(readBuffer,15,18);
                            time = time.substring(4, 8) + time.substring(0, 4);
                            int intTime = Integer.parseInt(time, 16);
                            //????????????????????????????????????float?????????????????????????????????IEEE754????????????????????????????????????????????????????????????
                            float floatTime = Float.intBitsToFloat(intTime);
                            tvCHTime.setText(floatTime + "mm");

                            if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                                socketForModbusTCP.sendData(READ_HEX + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(READ_HEX)), socketForModbusTCP);
                            }

                        }
                    } else if (readBuffer[1] == 4) {
                        if (readBuffer[2] == 6) {
                            //????????????
                            String ceControl = new String(readBuffer,3,4);
                            Message message1 = new Message();
                            message1.what = TAG_THERE;
                            message1.obj = ceControl;
                            handler.sendMessage(message1);
                            if (ceControl.equals("0001")) {
                                tvCEControl.setText("????????????");
                                btnCEControl.setText("????????????");
                            } else if (ceControl.equals("0003")) {
                                tvCEControl.setText("????????????");
                                btnCEControl.setText("????????????");
                            }

                            //????????????
                            String lightSelect = new String(readBuffer,5,6);
                            if (lightSelect.equals("0001")) {
                                tvLightSelect.setText("??????");
                                btnLightSelect.setText("??????");
                            } else if (lightSelect.equals("0003")) {
                                tvLightSelect.setText("??????");
                                btnLightSelect.setText("??????");
                            } else {
                                tvLightSelect.setText("??????");
                                btnLightSelect.setText("??????");
                            }

                            //????????????
                            String light = new String(readBuffer,7,8);
                            if (light.equals("0001")) {
                                tvSearchlightControl.setText("???????????????");
                                btnSearchlightControl.setText("???????????????");
                            } else if (light.equals("0003")) {
                                tvSearchlightControl.setText("???????????????");
                                btnSearchlightControl.setText("???????????????");
                            }
                        }
                    }else {
                        isConnect = true;
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    socketForModbusTCP.connect(new ModbusCallBack() {
                        @Override
                        public void success(String s) {
                            isConnect = true;
                            getConfigure();
                            message = new Message();
                            message.what = TAG_ONE;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void fail(String s) {
                            isConnect = false;
                            message = new Message();
                            message.what = Constant.TAG_TWO;
                            handler.sendMessage(message);
                        }
                    });
                }
            }).start();

        }
    }

    /**
     * ?????????
     */
    private void setUiData() {
        ItemInfo item = null;
        for (int i = 0; i < itemName.length; i++) {
            item = new ItemInfo(mItemImgs[i], itemName[i]);
            data.add(item);
        }
        circleMenuAdapternew = new CircleMenuAdapter(this, data);
        cmMain.setAdapter(circleMenuAdapternew);
        setClient();
    }

    private void setClient() {
        cmMain.setOnItemClickListener(new CircleMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (isConnect) {
                    if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                        if (itemName[position].equals("???")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + TAG_ONE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + TAG_ONE)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("???")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_THERE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_THERE)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("???")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_FOUR + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_FOUR)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("???")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_TWO + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_TWO)), socketForModbusTCP);
                        }
                    }
                } else {
                    message = new Message();
                    message.what = Constant.TAG_TWO;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @OnClick({R.id.ivVisition, R.id.ivGone, R.id.btnStop, R.id.tvSpeed, R.id.tvDistance, R.id.tvCHTime,
            R.id.tvCEControl, R.id.tvLightSelect, R.id.tvSearchlightControl, R.id.tvCHControl,
            R.id.btnCEControl, R.id.btnCHControl, R.id.btnSearchlightControl, R.id.btnLightSelect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivVisition:
                linSetting.setVisibility(View.VISIBLE);
                ivVisition.setVisibility(View.GONE);
                ivGone.setVisibility(View.VISIBLE);
                break;
            case R.id.ivGone:
                linSetting.setVisibility(View.GONE);
                ivVisition.setVisibility(View.VISIBLE);
                ivGone.setVisibility(View.GONE);
                break;
            case R.id.btnStop:
                if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                    isConfigure = false;
                    socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_SEVEN + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_SEVEN)), socketForModbusTCP);
                }
                break;
            case R.id.tvSpeed:
                //????????????
                showFDialog("Speed");
                break;
            case R.id.tvDistance:
                //????????????
                showFDialog("Distance");
                break;
            case R.id.tvCHTime:
                //????????????
                showFDialog("Time");
                break;
            case R.id.tvCEControl:
                CEControl(tvCEControl);
                //????????????
                break;
            case R.id.tvLightSelect:
                LightSelect(tvLightSelect);
                //????????????
                break;
            case R.id.tvSearchlightControl:
                SearchlightControl(tvSearchlightControl);
                //???  ???  ???
                break;
            case R.id.tvCHControl:
                //????????????
                break;
            case R.id.btnCEControl:
                //????????????
                CEControl(btnCEControl);
                break;
            case R.id.btnCHControl:
                break;
            case R.id.btnSearchlightControl:
                //???  ???  ???
                SearchlightControl(btnSearchlightControl);
                break;
            case R.id.btnLightSelect:
                //????????????
                LightSelect(btnLightSelect);
                break;
        }
    }

    /**
     * ????????????
     */
    private void LightSelect(View view) {
//        new MainUI().showPopupMenuLight(view, this, new ModbusCallBack() {
//            @Override
//            public void success(String s) {
//                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_THERE);
//            }
//
//            @Override
//            public void fail(String s) {
//                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_ONE);
//            }
//        });
    }

    /**
     * ???  ???  ???
     */
    private void SearchlightControl(View view) {
        new MainUI().showPopupMenu(view, "SearchlightControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                    isConfigure = false;
                    socketForModbusTCP.sendData(TZD_CONTROL + TAG_ONE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(TZD_CONTROL + TAG_ONE)), socketForModbusTCP);
                }
            }

            @Override
            public void fail(String s) {
                if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                    isConfigure = false;
                    socketForModbusTCP.sendData(TZD_CONTROL + TAG_THERE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(TZD_CONTROL + TAG_THERE)), socketForModbusTCP);
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void CEControl(View view) {
        new MainUI().showPopupMenu(view, "CEControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                    isConfigure = false;
                    socketForModbusTCP.sendData(CE_CONTROL + TAG_THERE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(CE_CONTROL + TAG_THERE)), socketForModbusTCP);
                }
            }

            @Override
            public void fail(String s) {
                isConfigure = false;
                if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                    socketForModbusTCP.sendData(CE_CONTROL + TAG_ONE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(CE_CONTROL + TAG_ONE)), socketForModbusTCP);
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    private void showFDialog(String tag) {
        String hint = "";
        if (tag.equals("Speed")) {
            hint = "?????????????????????";
        } else if (tag.equals("Distance")) {
            hint = "?????????????????????";
        } else if (tag.equals("Time")) {
            hint = "?????????????????????";
        }
        new AlertDialogUtil(this).showWriteDialog(hint, new ModbusCallBack() {
            @Override
            public void success(String backData) {
                if (backData != null && !backData.trim().equals("")) {
                    float distanceData = Float.parseFloat(backData);
                    //Float.floatToRawIntBits??????????????????????????????????????????????????????
                    String changeData = Integer.toHexString(Float.floatToRawIntBits(distanceData));
                    String sendData = changeData.substring(4, 8) + changeData.substring(0, 4);
                    if (tag.equals("Speed")) {
                        if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(MOB_SPEED + sendData + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(MOB_SPEED + sendData)), socketForModbusTCP);
                        }
                    }
                    if (tag.equals("Distance")) {
                        isConfigure = false;
                        if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                            socketForModbusTCP.sendData(MOB_DISTANCE + sendData + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(MOB_DISTANCE + sendData)), socketForModbusTCP);
                        }
                    }
                    if (tag.equals("Time")) {
                        isConfigure = false;
                        if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                            socketForModbusTCP.sendData(MOB_TIME + sendData + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(MOB_TIME + sendData)), socketForModbusTCP);
                        }
                    }
                }
            }

            @Override
            public void fail(String s) {

            }
        });
    }

    //???????????????,????????????Handler???????????????,???????????????Handler????????????????????????(handleMessage())
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG_ONE:
                    Toast.makeText(RobotActivity.this, R.string.connect_success, Toast.LENGTH_SHORT).show();
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(RobotActivity.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    break;
                case Constant.TAG_THERE:
                    Toast.makeText(RobotActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
}