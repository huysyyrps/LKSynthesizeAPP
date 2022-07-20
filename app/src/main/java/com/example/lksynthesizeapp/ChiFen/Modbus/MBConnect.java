package com.example.lksynthesizeapp.ChiFen.Modbus;

import android.util.Log;

import com.example.lksynthesizeapp.ChiFen.Activity.RobotActivity;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.Arrays;

public class MBConnect {
    public static final String TAG = "MBConnect";
    public void ModbusConnect(RobotActivity robotActivity, String address, int port, ModbusCallBack modbusCallBack){
        ModbusReq.getInstance().setParam(new ModbusParam()
                .setHost(address)
                .setPort(port)
                .setEncapsulated(false)
                .setKeepAlive(true)
                .setTimeout(2000)
                .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        modbusCallBack.success("");
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e("XXX",msg);
//                        modbusCallBack.fail(msg);
                    }
                });
    }





    //01读线圈
    public void ReadCoil(){
        ModbusReq.getInstance().readCoil(new OnRequestBack<boolean[]>() {
            @Override
            public void onSuccess(boolean[] booleen) {
                Log.d(TAG, "readCoil onSuccess " + Arrays.toString(booleen));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readCoil onFailed " + msg);
            }
        }, 1, 1, 2);
    }

    //02读直接寄存器
    public void ReadDiscreteInput(){
        ModbusReq.getInstance().readDiscreteInput(new OnRequestBack<boolean[]>() {
            @Override
            public void onSuccess(boolean[] booleen) {
                Log.d(TAG, "readDiscreteInput onSuccess " + Arrays.toString(booleen));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readDiscreteInput onFailed " + msg);
            }
        },1,1,5);
    }

    //03读保持寄存器
    public void ReadHoldingRegisters(){
        ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readHoldingRegisters onFailed " + msg);
            }
        }, 1, 2, 8);
    }

    //04读输⼊寄存器
    public void ReadInputRegisters(){
        ModbusReq.getInstance().readInputRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                Log.d(TAG, "readInputRegisters onSuccess " + Arrays.toString(data));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readInputRegisters onFailed " + msg);
            }
        }, 1, 2, 8);
    }

    //05写线圈
    public void WriteCoil(){
        ModbusReq.getInstance().writeCoil(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        },1,1,true);
    }

    //06写寄存器
    public void WriteRegister(){
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeRegister onFailed " + msg);
            }
        },1,1,234);
    }

    //07写多个寄存器

    /**
     *
     * @param slaveId 从站地址
     * @param start 开始位置
     * @param value 数据
     */
    public void WriteRegisters(int slaveId, int start, short[] value){
        ModbusReq.getInstance().writeRegisters(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegisters onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeRegisters onFailed " + msg);
            }
        },slaveId,start,value);
    }

    public void DestoryModbus(){
        ModbusReq.getInstance().destory();
    }

}
