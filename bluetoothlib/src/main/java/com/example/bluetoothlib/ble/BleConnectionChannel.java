package com.example.bluetoothlib.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.bluetoothlib.contract.BLueConnectionState;
import com.example.bluetoothlib.contract.ConnectionCallback;
import com.example.bluetoothlib.contract.ConnectionChannel;
import com.example.bluetoothlib.util.LogHelper;
import com.example.bluetoothlib.util.OutputStringUtil;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhangyunfei on 16/9/17.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleConnectionChannel extends ConnectionChannel {
    private static final String TAG = "BleConnectionChannel";
    private static final boolean DEBUG = true;
    //冯金强给的 UUID
//    private static UUID uuidServer = UUID.fromString("FFF0");
//    private static UUID uuidCharRead = UUID.fromString("FFF1");
//    private static UUID uuidCharWrite = UUID.fromString("FFF2");

    //从旧代码获得的UUID
    private static UUID uuidServer = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static UUID uuidCharRead = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static UUID uuidCharWrite = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static UUID uuidDescriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //2016-10-18适配于工给的盒子，需要特殊的UUUI，经测试可用
//    private static UUID uuidServer = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
//    private static UUID uuidCharRead = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
//    private static UUID uuidCharWrite = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
//    private static UUID uuidDescriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic characteristic4Read;
    private BluetoothGattCharacteristic characteristic4Write;
    private Handler handler;
    private String mBluetoothDeviceAddress;
    private RunnableTimeoutGATT runnableTimeoutGATT = new RunnableTimeoutGATT();


    public BleConnectionChannel(Context context, BluetoothAdapter bluetoothAdapter, ConnectionCallback bluetoothConnectionCallback) {
        super(context, bluetoothConnectionCallback);
        if (context == null)
            throw new NullPointerException();
        if (bluetoothAdapter == null)
            throw new NullPointerException();
        Log.i("show","## create BleConnectionChannel");
        mBluetoothAdapter = bluetoothAdapter;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void start() {

    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param deviceAddress The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public synchronized void connect(final String deviceAddress, final boolean autoConnect) {
        Log.i("show","## 准备连接到 " + deviceAddress);
        if (TextUtils.isEmpty(deviceAddress)) {
            Log.i("show","## deviceAddress is empty");
            if (runnableTimeoutGATT != null)
                runnableTimeoutGATT.clear();
            raiseOnFailure("deviceAddress is empty");
            return;
        }
        if (mBluetoothAdapter == null) {
             Log.i("show","## BluetoothAdapter not initialized or unspecified address.");
            raiseOnFailure("BluetoothAdapter not initialized or unspecified address.");
            return;
        }
        if (getState() == BLueConnectionState.CONNECTION_STATE_CONNECTING) {
             Log.i("show","## 蓝牙连接中，无需再次连接,中止连接");
            return;
        }
        if (getState() == BLueConnectionState.CONNECTION_STATE_CONNECTED) {
             Log.i("show","## 蓝牙已连接，无需再次连接");
            return;
        }
        raiseOnConnectStart(deviceAddress);
        if (!TextUtils.isEmpty(mBluetoothDeviceAddress) && mBluetoothDeviceAddress.equals(deviceAddress)
                && mBluetoothGatt != null) {
             Log.i("show","## 尝试使用一个已经存在的 mBluetoothGatt 进行连接.");
            if (mBluetoothGatt.connect()) {
                runnableTimeoutGATT.delayRetryConnectionAagin();
                 Log.i("show","## 尝试使用一个已经存在的 mBluetoothGatt 进行连接.执行完成");
                return;
            } else {
                 Log.i("show","## 尝试使用一个已经存在的 mBluetoothGatt 进行连接.执行失败");
            }
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
             Log.i("show","## 未找到指定的设备:" + deviceAddress);
            raiseOnFailure("未找到指定的设备:" + deviceAddress);
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
         Log.i("show","## 尝试创建一个新的 mBluetoothGatt 连接.");
        mBluetoothGatt = BleHelper.connectGatt(device, getContext(), false, mGattCallback);
        mBluetoothDeviceAddress = deviceAddress;
        runnableTimeoutGATT.clear();
        runnableTimeoutGATT.delayRetryConnectionAagin();
         Log.i("show","## 启动 gattTimoutTimer");
    }

    /**
     * 手机型号低于5.1时，connectGatt没有 4个参数的方法以传入 TRANPORT_LE参数
     * 为了应对某些双模的设备.当连接双模设备时，android默认第一次会使用EDR模式连接，会失败，
     * 第二次使用 TRANPORT_LE参数 连接，才能成功
     * 当发起一个连接后，等待超时，超时后要 使用同一个gatt连接执行connect.才能连接成功。
     * 当然，如果手机型号大于5.1可以直接指定 TRANPORT_LE参数，可以很容易连接成功。
     */
    private class RunnableTimeoutGATT implements Runnable {
        private int timeout_frequency = 1;
        public static final int MAX_RETRY = 5;
        public static final int INTENAL = 2000;

        @Override
        public synchronized void run() {
            handler.removeCallbacks(runnableTimeoutGATT);
            if (TextUtils.isEmpty(mBluetoothDeviceAddress)) return;
             Log.i("show",String.format("## 触发第%s次超时 gattTimoutTimer", timeout_frequency));
            if (timeout_frequency < MAX_RETRY) {
                if (mBluetoothGatt != null) {
                    raiseOnConnectStart(mBluetoothDeviceAddress);
                    mBluetoothGatt.connect();
                }
                timeout_frequency++;
                delayRetryConnectionAagin();
            } else {
                 Log.i("show",String.format("##       到达最大重试次数%s", MAX_RETRY));
                raiseOnFailure("连接超时");
                close();
                clear();
            }
        }

        public void clear() {
             Log.i("show","## 中止 gattTimoutTimer");
            timeout_frequency = 1;
            handler.removeCallbacks(runnableTimeoutGATT);
        }

        /**
         * 尝试重新连接
         */
        public void delayRetryConnectionAagin() {
            handler.postDelayed(runnableTimeoutGATT, INTENAL * timeout_frequency);
        }
    }

//    private static class MyHandler extends Handler{
//        WeakReference<BleConnectionChannel> blueToothConnectionBLEWeakReference;
//
//        public MyHandler(WeakReference<BleConnectionChannel> blueToothConnectionBLEWeakReference) {
//            this.blueToothConnectionBLEWeakReference = blueToothConnectionBLEWeakReference;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }


    private void raiseOnConnectStart(String address) {
        //正在连接
        if (getState() == BLueConnectionState.CONNECTION_STATE_CONNECTING)
            return;
        //重新连接
        if (getConnectionCallback() != null)
            getConnectionCallback().onConnectStart(address);
        setState(BLueConnectionState.CONNECTION_STATE_CONNECTING);
    }

    private void raiseOnConnected(String address) {
        runnableTimeoutGATT.clear();
        if (getState() == BLueConnectionState.CONNECTION_STATE_CONNECTED){
            Log.i("show","## 连接成功!!!");
            return;
        }
        if (getConnectionCallback() != null)
            getConnectionCallback().onConnected(address);
        setState(BLueConnectionState.CONNECTION_STATE_CONNECTED);
    }

    private void raiseOnFailure(String error) {
        if (getState() == BLueConnectionState.CONNECTION_STATE_DISCONNECTED)
            return;
        if (getConnectionCallback() != null)
            getConnectionCallback().onConnectionFailed(error);
        setState(BLueConnectionState.CONNECTION_STATE_DISCONNECTED);
    }


    private void raiseOnDisconnected() {
        LogHelper.d(TAG, "## raiseOnDisconnected state=" + getState());
        if (getState() == BLueConnectionState.CONNECTION_STATE_DISCONNECTED)
            return;
        if (getConnectionCallback() != null)
            getConnectionCallback().onConnectionLost();
        setState(BLueConnectionState.CONNECTION_STATE_DISCONNECTED);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
             Log.i("show",String.format("## onConnectionStateChange. status = %s, newState = %s", status, newState));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runnableTimeoutGATT.clear();
                    //Connected to GATT server !!!!
                    //开始打印
                    if (getConnectionCallback() != null){
                        Log.i("show","连接成功 设置回调%%%%%%%%%%%%");
                        getConnectionCallback().onConnected(mBluetoothDeviceAddress);
                        setState(BLueConnectionState.CONNECTION_STATE_CONNECTED);
                    }else {
                        Log.i("show","连接失败 设置回调%%%%%%%%%%%%");
                    }




                    // Attempts to discover services after successful connection.
                     Log.i("show","## Attempting to start service discovery:" +
                            mBluetoothGatt.discoverServices());

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                     Log.i("show","## Disconnected from GATT server.");
                    raiseOnDisconnected();
                }
            } else {
                runnableTimeoutGATT.delayRetryConnectionAagin();
            }

        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                 Log.i("show","## onServicesDiscovered received:  SUCCESS");
                initCharacteristic();
                //初始化服务后，要延迟，否则第一条消息会卡住(无响应)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        raiseOnConnected(mBluetoothDeviceAddress);

                    }
                }, 1200);
            } else {
                 Log.i("show","## onServicesDiscovered error falure " + status);
                raiseOnFailure("连接失败 status=" + status);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
             Log.i("show","## onCharacteristicWrite status: " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
             Log.i("show","## onDescriptorWrite status: " + status);
             Log.i("show","## 设定特征的通知 OK");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
             Log.i("show","## onDescriptorRead status: " + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
             Log.i("show","## onCharacteristicRead status: " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            Log.e(TAG, "## ============ threadid = " + Thread.currentThread().getName());
            byte[] bytes = readCharacteristic(characteristic);
             Log.i("show","## onCharacteristicChanged, len = " + (bytes == null ? 0 : bytes.length));
            if (bytes != null && getConnectionCallback() != null) {
                getConnectionCallback().onReadMessage(bytes);
            }

        }
    };


    /**
     * @param context
     * @return true 支持ble，false 不支持
     */
    public static boolean isSupportBLE(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void initCharacteristic() {
        if (mBluetoothGatt == null) throw new NullPointerException();
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        Log.e(TAG, services.toString());
        BluetoothGattService service = mBluetoothGatt.getService(uuidServer);
        characteristic4Read = service.getCharacteristic(uuidCharRead);
        characteristic4Write = service.getCharacteristic(uuidCharWrite);

        if (characteristic4Read == null) throw new NullPointerException();
        if (characteristic4Write == null) throw new NullPointerException();
        mBluetoothGatt.setCharacteristicNotification(characteristic4Read, true);
        BluetoothGattDescriptor descriptor = characteristic4Read.getDescriptor(uuidDescriptor);
        if (descriptor == null) throw new NullPointerException();
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

         Log.i("show","## characteristic4Read = " + characteristic4Read);
         Log.i("show","## characteristic4Write = " + characteristic4Write);
         Log.i("show","## descriptor = " + descriptor);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public byte[] readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
             Log.i("show","## BluetoothAdapter not initialized");
            return null;
        }
//        mBluetoothGatt.readCharacteristic(characteristic);
        return characteristic.getValue();
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
             Log.i("show","## BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (handler != null)
            handler.removeCallbacks(null);
         Log.i("show","## close gatt");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothDeviceAddress = null;
        runnableTimeoutGATT.clear();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        if (getState() != BLueConnectionState.CONNECTION_STATE_DISCONNECTED)
            setState(BLueConnectionState.CONNECTION_STATE_DISCONNECTED);
    }

    public void write(byte[] cmd) throws IOException {
        if (cmd == null || cmd.length == 0) return;
        Log.e(TAG, "## invoke write ============ threadid = " + Thread.currentThread().getName());
        if (getState() != BLueConnectionState.CONNECTION_STATE_CONNECTED)
            throw new IOException("未建立蓝牙连接");
         Log.i("show","## write:" + OutputStringUtil.transferForPrint(cmd));
       //synchronized (LOCK) {
        byte[] newCmd;
        if (cmd[cmd.length - 1] != 0x0D) {
             Log.i("show","## 结束符不以\\r结尾，自动补充\\r");
            newCmd = new byte[cmd.length + 1];
            System.arraycopy(cmd, 0, newCmd, 0, cmd.length);
            newCmd[newCmd.length - 1] = 0x0D;
        } else {
            newCmd = cmd;
        }
        characteristic4Write.setValue(newCmd);
        characteristic4Write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothGatt.writeCharacteristic(characteristic4Write);
//        }
        if (getConnectionCallback() != null)
            getConnectionCallback().onWriteMessage(cmd);
    }


    /**
     * 可被发现
     *
     * @param context context
     */
    public void ensureDiscoverable(Context context) {
        if (context == null)
            throw new NullPointerException();
        if (DEBUG)  Log.i("show","## ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);
        }
    }







}
