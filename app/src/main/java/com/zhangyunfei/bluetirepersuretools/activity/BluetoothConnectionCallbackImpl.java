package com.zhangyunfei.bluetirepersuretools.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bluetoothlib.contract.ConnectionCallback;
import com.zhangyunfei.bluetirepersuretools.activity.printf.MyPrintInfo;
import com.zhangyunfei.bluetirepersuretools.activity.printf.PrintDataService;
import com.zhangyunfei.bluetirepersuretools.activity.printf.PrintUtil;
import com.zhangyunfei.bluetirepersuretools.activity.printf.TemplateFactory;
import com.zhangyunfei.bluetirepersuretools.activity.printf.Waybill4PrintInfo;
import com.zhangyunfei.bluetirepersuretools.activity.printf.WaybillPrintTemplate;


/**
 * Created by zhangyunfei on 16/9/17.
 */
public class BluetoothConnectionCallbackImpl implements ConnectionCallback {
    private static final String TAG = "BLUE";
    private android.os.Handler mHandler;

    public BluetoothConnectionCallbackImpl(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void onStateChange(int oldState, int newState) {
        Log.i("show", "## 消息状态发生改变: " + +oldState + " -> " + newState);
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothDemoActivity2.MESSAGE_STATE_CHANGE, newState, -1).sendToTarget();
    }

    MyPrintInfo bean;
    WaybillPrintTemplate template;
    PrintDataService printDataService ;
    @Override
    public void onConnected(String deviceName) {
        Log.i("show", "## 连接成功  地址: " + deviceName);
        printDataService = new PrintDataService( deviceName);
        bean = getDataBean();
        template = TemplateFactory.getTemplate(TemplateFactory.KEY_TEMPLATE_ID_my);


        //连接成功,开始打印
        doPrint();

        // Send the name of the connected device back to the UI Activity
        /*Message msg = mHandler.obtainMessage(BluetoothDemoActivity2.CONNECTED);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity2.DEVICE_NAME, deviceName);
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/
    }
    private void doPrint() {
        // // 设置当前设备名称
        // this.deviceName.setText(this.printDataService.getDeviceName());

        // 一上来就先连接蓝牙设备
        boolean flag = printDataService.connect();
        if (flag == false) {
            // 连接失败
            Log.i("show","连接打印机失败");

        } else {
            // 连接成功
            Log.i("show","连接打印机成功");
            //PrintUtil.create(printDataService).setTemplate(template).setData(bean).print();
        }
    }

    private MyPrintInfo getDataBean() {
        MyPrintInfo bean = new MyPrintInfo();
        bean.put(MyPrintInfo.NAME, "小薇薇");
        bean.put(MyPrintInfo.PHONE, "你知道吗");
        bean.put(MyPrintInfo.ADDRESS, "I Miss You Very Much!");

       /* bean.put(Waybill4PrintInfo.KEY_receiver_name, "张三丰");
        bean.put(Waybill4PrintInfo.KEY_receiver_phone, "13901010101");
        bean.put(Waybill4PrintInfo.KEY_receiver_address, "山东西湖路小巷18号");

        bean.put(Waybill4PrintInfo.KEY_weight, "100");

        bean.put(Waybill4PrintInfo.KEY_pay_way_cash, "√");
        bean.put(Waybill4PrintInfo.KEY_is_bao_jia, "√");

        bean.put(Waybill4PrintInfo.KEY_bao_e, "1000");
        bean.put(Waybill4PrintInfo.KEY_bao_jia, "1000");
        bean.put(Waybill4PrintInfo.KEY_yun_fei, "99");

        bean.put(Waybill4PrintInfo.KEY_total_price, "199.9");*/
        return bean;
    }



    @Override
    public void onConnectionFailed(String str) {
        Log.i("show", "## 连接失败 ");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDemoActivity2.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity2.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onConnectionLost() {
        Log.i("show","## 连接丢失 ");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDemoActivity2.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity2.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onReadMessage(byte[] tmp) {
        Log.i("show","## 读取到消息 len = " + tmp.length);
        // Send the obtained bytes to the UI Activity
        mHandler.obtainMessage(BluetoothDemoActivity2.MESSAGE_READ, tmp)
                .sendToTarget();
    }

    @Override
    public void onWriteMessage(byte[] buffer) {
        Log.i("show", "## 发送消息 len = " + buffer.length);
        // Share the sent message back to the UI Activity
        mHandler.obtainMessage(BluetoothDemoActivity2.MESSAGE_WRITE, -1, -1, buffer)
                .sendToTarget();
    }

    @Override
    public void onConnectStart(String mBluetoothDeviceAddress) {
        Log.i("show", "## onConnectStart ");

    }
}
