package com.example.lksynthesizeapp.ChiFen.Modbus;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;

public class SocketForModbusTCPIsNull {
    /**
     * 判断socketForModbusTCP是否为null
     * @param socketForModbusTCP
     */
    Context context;

    public SocketForModbusTCPIsNull(Context robotActivity) {
        this.context = robotActivity;
    }

    public boolean socketForModbusTCPIsNull(SocketForModbusTCP socketForModbusTCP){
        if (socketForModbusTCP==null){
            Message message = new Message();
            message.what = Constant.TAG_ONE;
            handler.sendMessage(message);
            return false;
        }else {
            return true;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context, R.string.connect_faile, Toast.LENGTH_SHORT).show();
        }
    };
}
