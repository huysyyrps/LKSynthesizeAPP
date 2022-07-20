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
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        ButterKnife.bind(this);
        try {
            address = new getIp().getConnectIp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.setBackgroundColor(Color.BLACK);
        WebSettings WebSet = webView.getSettings();    //获取webview设置
        WebSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //自适应屏幕
        //禁止上下左右滚动(不显示滚动条)
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        if (address != null) {
            webView.loadUrl("http://" + address + ":8080");
        } else {
            Toast.makeText(this, "IP为空", Toast.LENGTH_SHORT).show();
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
     * 获取配置信息
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
//            handlerConfigure.postDelayed(runnable, TIME); // 在初始化方法里.
//        }
        socketForModbusTCP.sendData(READ_FLOAT + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(READ_FLOAT)), socketForModbusTCP);
    }

    /**
     * modbus建立连接
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
                    //todo 根据业务解析数据
                    if (readBuffer[1] == 3) {
                        //因为返回是16进制  所以10即为10进制16
                        if (readBuffer[2] == 10) {
                            //实际距离
                            String actualDistance = new String(readBuffer,3,6);
                            Message message1 = new Message();
                            message1.what = TAG_THERE;
                            message1.obj = actualDistance;
                            handler.sendMessage(message1);
                            actualDistance = actualDistance.substring(4, 8) + actualDistance.substring(0, 4);
                            int intActualDistance = Integer.parseInt(actualDistance, 16);
                            //返回对应于给定的位表示的float值。该参数被认为是根据IEEE754浮点“单一格式”位布局是一个浮点值的表示
                            float floatActualDistance = Float.intBitsToFloat(intActualDistance);
                            tvActualDistance.setText(floatActualDistance + "mm");

                            //速度
                            String speen = new String(readBuffer,7,10);
                            speen = speen.substring(4, 8) + speen.substring(0, 4);
                            int intSpeen = Integer.parseInt(speen, 16);
                            //返回对应于给定的位表示的float值。该参数被认为是根据IEEE754浮点“单一格式”位布局是一个浮点值的表示
                            float floatSpeen = Float.intBitsToFloat(intSpeen);
                            tvSpeed.setText(floatSpeen + "mm");

                            //行走距离
                            String distance = new String(readBuffer,11,14);
                            distance = distance.substring(4, 8) + distance.substring(0, 4);
                            int intDistance = Integer.parseInt(distance, 16);
                            //返回对应于给定的位表示的float值。该参数被认为是根据IEEE754浮点“单一格式”位布局是一个浮点值的表示
                            float floatDistance = Float.intBitsToFloat(intDistance);
                            tvDistance.setText(floatDistance + "mm");

                            //行走距离
                            String time = new String(readBuffer,15,18);
                            time = time.substring(4, 8) + time.substring(0, 4);
                            int intTime = Integer.parseInt(time, 16);
                            //返回对应于给定的位表示的float值。该参数被认为是根据IEEE754浮点“单一格式”位布局是一个浮点值的表示
                            float floatTime = Float.intBitsToFloat(intTime);
                            tvCHTime.setText(floatTime + "mm");

                            if (new SocketForModbusTCPIsNull(RobotActivity.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                                socketForModbusTCP.sendData(READ_HEX + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(READ_HEX)), socketForModbusTCP);
                            }

                        }
                    } else if (readBuffer[1] == 4) {
                        if (readBuffer[2] == 6) {
                            //磁轭控制
                            String ceControl = new String(readBuffer,3,4);
                            Message message1 = new Message();
                            message1.what = TAG_THERE;
                            message1.obj = ceControl;
                            handler.sendMessage(message1);
                            if (ceControl.equals("0001")) {
                                tvCEControl.setText("磁轭抬起");
                                btnCEControl.setText("磁轭抬起");
                            } else if (ceControl.equals("0003")) {
                                tvCEControl.setText("磁轭落下");
                                btnCEControl.setText("磁轭落下");
                            }

                            //磁轭控制
                            String lightSelect = new String(readBuffer,5,6);
                            if (lightSelect.equals("0001")) {
                                tvLightSelect.setText("黑光");
                                btnLightSelect.setText("黑光");
                            } else if (lightSelect.equals("0003")) {
                                tvLightSelect.setText("白光");
                                btnLightSelect.setText("黑光");
                            } else {
                                tvLightSelect.setText("关闭");
                                btnLightSelect.setText("关闭");
                            }

                            //磁轭控制
                            String light = new String(readBuffer,7,8);
                            if (light.equals("0001")) {
                                tvSearchlightControl.setText("探照灯开启");
                                btnSearchlightControl.setText("探照灯开启");
                            } else if (light.equals("0003")) {
                                tvSearchlightControl.setText("探照灯关闭");
                                btnSearchlightControl.setText("探照灯关闭");
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
     * 方向键
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
                        if (itemName[position].equals("上")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + TAG_ONE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + TAG_ONE)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("左")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_THERE + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_THERE)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("右")) {
                            isConfigure = false;
                            socketForModbusTCP.sendData(Constant.MOB_DIRECTION + Constant.TAG_FOUR + new Crc16Util().getTableCRC(new BytesHexChange().hexStringToBytes(Constant.MOB_DIRECTION + Constant.TAG_FOUR)), socketForModbusTCP);
                        }
                        if (itemName[position].equals("下")) {
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
                //爬行速度
                showFDialog("Speed");
                break;
            case R.id.tvDistance:
                //行走距离
                showFDialog("Distance");
                break;
            case R.id.tvCHTime:
                //磁化时间
                showFDialog("Time");
                break;
            case R.id.tvCEControl:
                CEControl(tvCEControl);
                //磁轭控制
                break;
            case R.id.tvLightSelect:
                LightSelect(tvLightSelect);
                //黑白选择
                break;
            case R.id.tvSearchlightControl:
                SearchlightControl(tvSearchlightControl);
                //探  照  灯
                break;
            case R.id.tvCHControl:
                //磁化控制
                break;
            case R.id.btnCEControl:
                //磁轭控制
                CEControl(btnCEControl);
                break;
            case R.id.btnCHControl:
                break;
            case R.id.btnSearchlightControl:
                //探  照  灯
                SearchlightControl(btnSearchlightControl);
                break;
            case R.id.btnLightSelect:
                //黑白选择
                LightSelect(btnLightSelect);
                break;
        }
    }

    /**
     * 黑白选择
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
     * 探  照  灯
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
     * 磁轭控制
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
     * 弹窗数据设置
     */
    private void showFDialog(String tag) {
        String hint = "";
        if (tag.equals("Speed")) {
            hint = "请输入爬行速度";
        } else if (tag.equals("Distance")) {
            hint = "请输入行走距离";
        } else if (tag.equals("Time")) {
            hint = "请输入磁化时间";
        }
        new AlertDialogUtil(this).showWriteDialog(hint, new ModbusCallBack() {
            @Override
            public void success(String backData) {
                if (backData != null && !backData.trim().equals("")) {
                    float distanceData = Float.parseFloat(backData);
                    //Float.floatToRawIntBits返回的就是该数值的浮点数的十进制数字
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

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
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