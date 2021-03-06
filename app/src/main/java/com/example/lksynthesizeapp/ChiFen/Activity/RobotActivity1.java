package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.itemName;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.mItemImgs;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MODBUS_CODE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_EIGHT;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FIVE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FOUR;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_SEVEN;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_TWO;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusContion;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusFloatCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.SocketForModbusTCP;
import com.example.lksynthesizeapp.ChiFen.Modbus.SocketForModbusTCPIsNull;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotActivity1 extends BaseActivity {

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
    @BindView(R.id.linBtn)
    LinearLayout linBtn;
    private CircleMenuAdapter circleMenuAdapternew;
    private boolean isConnect = false;
    private int TIME = 2000;
    ModbusContion modbusContion = new ModbusContion();
    Handler handlerConfigure = new Handler();
    public static String TAG = "RobotActivitytest";
    private Handler handlerData = new Handler();


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
        modbusConnect();
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //todo what you want
            modbusContion.readInputRegistersClickEvent(MODBUS_CODE, 3, TAG_FIVE, new ModbusCallBack() {
                @Override
                public void success(String s) {
                    message = new Message();
                    message.what = TAG_THERE;
                    message.obj = s;
                    handler.sendMessage(message);

                    modbusContion.readHoldingRegistersClickEvent(MODBUS_CODE, 16, TAG_EIGHT, new ModbusFloatCallBack() {
                        @Override
                        public void success(short[] s) {
                            message = new Message();
                            message.what = TAG_FOUR;
                            message.obj = s;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void fail(String s) {
                            message = new Message();
                            message.what = TAG_FIVE;
                            message.obj = s;
                            handler.sendMessage(message);
                        }
                    });
                }

                @Override
                public void fail(String s) {
                    message = new Message();
                    message.what = TAG_FIVE;
                    message.obj = s;
                    handler.sendMessage(message);
                }
            });
            handlerData.postDelayed(runnable, 2000);
        }
    };


    /**
     * ??????
     */
    private void modbusConnect() {
        ModbusReq.getInstance().setParam(new ModbusParam()
//                .setHost("172.16.16.68")
                .setHost(address)
                .setPort(502)
                .setEncapsulated(true)
                .setKeepAlive(true)
                .setTimeout(2000)
                .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        isConnect = true;
                        handlerData.postDelayed(runnable, TIME);//???????????????
                        Log.e(TAG, "onSuccess " + s);
                        message = new Message();
                        message.what = TAG_ONE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e(TAG, "onFailed " + msg);
                        message = new Message();
                        message.what = TAG_TWO;
                        handler.sendMessage(message);
                    }
                });
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
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_ONE);
                    }
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_THERE);
                    }
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_FOUR);
                    }
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_TWO);
                    }
                } else {
                    message = new Message();
                    message.what = TAG_TWO;
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
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_SEVEN);
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
                CHControl(tvCHControl);
                //????????????
                break;
            case R.id.btnCEControl:
                //????????????
                CEControl(btnCEControl);
                break;
            case R.id.btnCHControl:
                CHControl(btnCHControl);
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
        new MainUI().showPopupMenuLight(view, this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_THERE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_ONE);
            }
        });
    }

    /**
     * ???  ???  ???
     */
    private void SearchlightControl(View view) {
        new MainUI().showPopupMenu(view, "SearchlightControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 7, TAG_ONE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 7, TAG_THERE);
            }
        });
    }

    /**
     * ????????????
     */
    private void CHControl(View view) {
        new MainUI().showPopupMenu(view, "CHControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 3, TAG_ONE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 3, TAG_THERE);
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
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 5, TAG_THERE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 5, TAG_ONE);
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
                    if (tag.equals("Speed")) {
                        new ModbusContion().writeRegistersClickEvent(MODBUS_CODE, 18, byte2ShortArray(float2byte(distanceData)));
                    }
                    if (tag.equals("Distance")) {
                        if (new SocketForModbusTCPIsNull(RobotActivity1.this).socketForModbusTCPIsNull(socketForModbusTCP)) {
                            new ModbusContion().writeRegistersClickEvent(MODBUS_CODE, 20, byte2ShortArray(float2byte(distanceData)));
                        }
                    }
                    if (tag.equals("Time")) {
                        modbusContion.writeRegistersClickEvent(MODBUS_CODE, 22, byte2ShortArray(float2byte(distanceData)));
                    }
                }
            }

            @Override
            public void fail(String s) {

            }
        });
    }

    public static byte[] float2byte(float f) {
        // ???float?????????byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // ????????????
        int len = b.length;
        // ???????????????????????????????????????????????????
        byte[] dest = new byte[len];
        // ????????????????????????????????????????????????????????????
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // ????????????i???????????????i?????????
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    // byte?????????short??????
    public static short[] byte2ShortArray(byte[] data) {
        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modbusContion.destory();
        handlerData.removeCallbacksAndMessages(null);
    }

    //???????????????,????????????Handler???????????????,???????????????Handler????????????????????????(handleMessage())
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG_ONE:
                    Toast.makeText(RobotActivity1.this, R.string.connect_success, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_TWO:
                    Toast.makeText(RobotActivity1.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_THERE:
                    String data = msg.obj.toString();
                    set16Data(substringData(data));
                    Toast.makeText(RobotActivity1.this, data, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_FOUR:
                    short[] dataArray = (short[]) msg.obj;
                    String[] typeData = new String[dataArray.length*2];
                    for (int i = 0; i < dataArray.length; i++) {
                        String strHex2  = String.format("%04x", dataArray[i]).toUpperCase();//?????????0
                        typeData[i] = strHex2;
                    }
                    setFloatData(typeData);
                    break;
                case TAG_FIVE:
                    String message = msg.obj.toString();
                    Toast.makeText(RobotActivity1.this, message, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };

//    //??????16????????????
//    private void setFloatData(byte[] strs) {
//        byte[] byteActualDistance = new byte[4];
//        byteActualDistance[0] = strs[0];
//        byteActualDistance[1] = strs[1];
//        byteActualDistance[2] = strs[2];
//        byteActualDistance[3] = strs[3];
//        String actualDistanceHex = new BytesHexChange().ByteArrToHex(byteActualDistance);
//        actualDistanceHex = actualDistanceHex.substring(4, 8) + actualDistanceHex.substring(0, 4);
//        int ieee754ActualDistanceHex = Integer.parseInt(actualDistanceHex, 16);
//        float realActualDistanceHex = Float.intBitsToFloat(ieee754ActualDistanceHex);
//        tvActualDistance.setText(realActualDistanceHex + "mm");
//
//        byte[] byteSpeed = new byte[4];
//        byteSpeed[0] = strs[4];
//        byteSpeed[1] = strs[5];
//        byteSpeed[2] = strs[6];
//        byteSpeed[3] = strs[7];
//        String speenHex = new BytesHexChange().ByteArrToHex(byteSpeed);
//        speenHex = speenHex.substring(4, 8) + speenHex.substring(0, 4);
//        int ieee754Speed = Integer.parseInt(speenHex, 16);
//        float realApeed = Float.intBitsToFloat(ieee754Speed);
//        tvSpeed.setText(realApeed + "mm/min");
//
//        byte[] byteDistance = new byte[4];
//        byteDistance[0] = strs[8];
//        byteDistance[1] = strs[9];
//        byteDistance[2] = strs[10];
//        byteDistance[3] = strs[11];
//        String distanceHex = new BytesHexChange().ByteArrToHex(byteDistance);
//        distanceHex = distanceHex.substring(4, 8) + distanceHex.substring(0, 4);
//        int ieee754Distance = Integer.parseInt(distanceHex, 16);
//        float realDistance = Float.intBitsToFloat(ieee754Distance);
//        tvDistance.setText(realDistance + "mm");
//
//        byte[] byteCHTime = new byte[4];
//        byteCHTime[0] = strs[12];
//        byteCHTime[1] = strs[13];
//        byteCHTime[2] = strs[14];
//        byteCHTime[3] = strs[15];
//        String cHTimeHex = new BytesHexChange().ByteArrToHex(byteCHTime);
//        cHTimeHex = cHTimeHex.substring(4, 8) + cHTimeHex.substring(0, 4);
//        int ieee754CHTime = Integer.parseInt(cHTimeHex, 16);
//        float realCHTime = Float.intBitsToFloat(ieee754CHTime);
//        tvCHTime.setText(realCHTime + "s");
//    }


    //??????16????????????
    private void set16Data(String[] strs) {
        //??????
        if (strs[0] != null && strs[0].trim().equals("1")) {
            tvCHControl.setText(R.string.ch_control_open);
            btnCHControl.setText(R.string.ch_control_open);
            linBtn.setVisibility(View.VISIBLE);
        } else if (strs[0] != null && strs[0].trim().equals("3")) {
            tvCHControl.setText(R.string.ch_control_close);
            btnCHControl.setText(R.string.ch_control_close);
            linBtn.setVisibility(View.VISIBLE);
        }

        //??????
        if (strs[2] != null && strs[2].trim().equals("1")) {
            tvCEControl.setText(R.string.ce_up);
            btnCEControl.setText(R.string.ce_up);
            linBtn.setVisibility(View.VISIBLE);
        } else if (strs[2] != null && strs[2].trim().equals("3")) {
            tvCEControl.setText(R.string.ce_down);
            btnCEControl.setText(R.string.ce_down);
            linBtn.setVisibility(View.VISIBLE);
        }

        //????????????
        if (strs[3] != null && strs[3].trim().equals("1")) {
            tvLightSelect.setText(R.string.light_black);
            btnLightSelect.setText(R.string.light_black);
        } else if (strs[3] != null && strs[3].trim().equals("3")) {
            tvLightSelect.setText(R.string.light_wither);
            btnLightSelect.setText(R.string.light_wither);
        }

        //?????????
        if (strs[4] != null && strs[4].trim().equals("1")) {
            tvSearchlightControl.setText(R.string.search_light_open);
            btnSearchlightControl.setText(R.string.search_light_open);
        } else if (strs[4] != null && strs[4].trim().equals("3")) {
            tvSearchlightControl.setText(R.string.search_light_close);
            btnSearchlightControl.setText(R.string.search_light_close);
        }

//        //??????
//        if (strs[4] != null && strs[4].trim().equals("1")) {
//            tvCHControl.setText(R.string.ch_control_open);
//            btnCHControl.setText(R.string.ch_control_open);
//        } else if (strs[4] != null && strs[4].trim().equals("3")) {
//            tvCHControl.setText(R.string.ch_control_close);
//            btnCHControl.setText(R.string.ch_control_close);
//        }
    }
    //??????Float??????
    private void setFloatData(String[] typeData) {
        if (typeData[0] != null&&typeData[1] != null) {
            String actualDistanceHex = typeData[1]+typeData[0];
            int ieee754ActualDistanceHex = Integer.parseInt(actualDistanceHex, 16);
            float realActualDistanceHex = Float.intBitsToFloat(ieee754ActualDistanceHex);
            tvActualDistance.setText(realActualDistanceHex + "mm");
        }
        if (typeData[2] != null&&typeData[3] != null) {
            String speenHex = typeData[3]+typeData[2];
            int ieee754Speed = Integer.parseInt(speenHex, 16);
            float realApeed = Float.intBitsToFloat(ieee754Speed);
            tvSpeed.setText(realApeed + "mm/min");
        }
        if (typeData[4] != null&&typeData[5] != null) {
            String distanceHex = typeData[5]+typeData[4];
            int ieee754Distance = Integer.parseInt(distanceHex, 16);
            float realDistance = Float.intBitsToFloat(ieee754Distance);
            tvDistance.setText(realDistance + "mm");
        }
        if (typeData[6] != null&&typeData[7] != null) {
            String  cHTimeHex = typeData[7]+typeData[6];
            int ieee754CHTime = Integer.parseInt(cHTimeHex, 16);
            float realCHTime = Float.intBitsToFloat(ieee754CHTime);
            tvCHTime.setText(realCHTime + "s");
        }
    }

    //????????????
    private String[] substringData(String data) {
        String data1 = data.substring(1, data.length());
        String data2 = data1.substring(0, data1.length() - 1);
        String[] strs = data2.split(",");
        return strs;
    }
}