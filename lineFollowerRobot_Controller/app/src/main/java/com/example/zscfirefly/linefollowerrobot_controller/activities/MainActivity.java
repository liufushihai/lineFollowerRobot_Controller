package com.example.zscfirefly.linefollowerrobot_controller.activities;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zscfirefly.linefollowerrobot_controller.R;
import com.example.zscfirefly.linefollowerrobot_controller.ble.BLEDevice;
import com.example.zscfirefly.linefollowerrobot_controller.ble.BleManager;
import com.example.zscfirefly.linefollowerrobot_controller.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btn_search;
    private Button btn_connect;
    private Button btn_control;
    private ListView lv_devies;
    private TextView tv_addr;
    private TextView tv_rssi;
    private TextView tv_name;

    private ArrayList<String> preDatas = new ArrayList<String>();// 存放分段发送的消息
    private ArrayList<BluetoothGattService> services = new ArrayList<BluetoothGattService>();
    private Iterator<String> dataIterator;// 消息的迭代器

    private BluetoothGatt mBluetoothGatt;

    protected static String uuidQppService = "0000ff92-0000-1000-8000-00805f9b34fb";// 读写服务
    protected static String uuidQppCharWrite = "00009600-0000-1000-8000-00805f9b34fb"; // 写特征

    protected static String uuidATService = "0000cc03-0000-1000-8000-00805f9b34fb";// AT命令
    protected static String uuidATCharWrite = "0000ec00-0000-1000-8000-00805f9b34fb";// 写特征

   // private static final String MY_DEV_NAME = "LineFolRobot";
    private static final String MY_DEV_NAME = "FireBLE";
    private int my_dev_index = -1;              //记录选中的蓝牙设备在蓝牙列表中的下标
    public static boolean isConnected = false;
    public static String cur_Dev_Name = "";


    private static final int HANDLER_ON_CONNECTION_CHANGED = 0;
    private static final int HANDLER_ON_CHARACTERISTIC_READ = 1;
    private static final int HANDLER_ON_CHARACTERISTIC_WRITE = 2;
    private static final int HANDLER_ON_CHARACTERISTIC_CHANGED = 3;
    private static final int HANDLER_ON_SERVICE_DISCOVERED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        BleManager.getInstance().init(MainActivity.this);
        //startScanAndBind();

       lv_devies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               tv_name.setText("设备名称："+ BleManager.getInstance().getmLeDeviceListAdapter().getItem(i).getDeviceName());
               tv_addr.setText("蓝牙地址："+ BleManager.getInstance().getmLeDeviceListAdapter().getItem(i).getDeviceAddress());
               tv_rssi.setText("信号强度："+ BleManager.getInstance().getmLeDeviceListAdapter().getItem(i).getRssi() + "db");

               cur_Dev_Name = BleManager.getInstance().getmLeDeviceListAdapter().getItem(i).getDeviceName();

               my_dev_index = i;

              // Toast.makeText(MainActivity.this, i+"",Toast.LENGTH_SHORT).show();
           }
       });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //close();

//        BleManager.getInstance().onBLEDeviceConnectionChange(BleManager.getInstance().getmBLEDevice()
//                ,BleManager.STATE_CONNECTED,BleManager.STATE_DISCONNECTED);
        unRegisterReciver();
        BleManager.getInstance().clearBLEDevice();
        BleManager.getInstance().clearData();
        BleManager.getInstance().disconnect();
        //BleManager.getInstance().closeBluetooth();

    }

    /**
     * 初始化视图
     */
    private void initView() {
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_control = (Button) findViewById(R.id.btn_control);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        lv_devies = (ListView) findViewById(R.id.lv_devices);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_rssi = (TextView) findViewById(R.id.tv_rssi);
    }

    /**
     * 点击事件处理
     * @param view
     */
    public void clickHandler(View view){
        switch (view.getId()){
            case R.id.btn_search:
              //  Toast.makeText(MainActivity.this,"btn_search",Toast.LENGTH_SHORT).show();

                //BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
                startScanAndBind();    //扫描蓝牙

//                setNotification(uuidQppService, uuidQppCharWrite);
//                searchServices();

                lv_devies.setAdapter(BleManager.getInstance().getmLeDeviceListAdapter());
              //  new BleTasks().execute();   //后台任务类连接蓝牙设备

                break;

            case R.id.btn_control:
//                setNotification(uuidQppService, uuidQppCharWrite);
//                searchServices();

              //  Toast.makeText(MainActivity.this,"btn_control",Toast.LENGTH_SHORT).show();
               // sendData("hello lineFollowerRobot_Controller!");
             //   BleManager.getInstance().write("hello car!");

//                setNotification(uuidQppService, uuidQppCharWrite);
//                searchServices();

                if(isConnected == true) {
                    Intent intent = new Intent(MainActivity.this, RockerActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"设备尚未连接！",Toast.LENGTH_SHORT).show();
                }
//
//                Intent intent = new Intent(MainActivity.this, RockerActivity.class);
//                startActivity(intent);

                break;

            case R.id.btn_connect:
                Log.d("cur_Dev_Name",cur_Dev_Name);

//                setNotification(uuidQppService, uuidQppCharWrite);
//                searchServices();

//                if(cur_Dev_Name.equals(MY_DEV_NAME)){

//                    BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
//                    BleManager.getInstance().setDevice(my_dev_index);
//                    BleManager.getInstance().connect();
//
//                    BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
//                    BleManager.getInstance().setDevice(my_dev_index);
//                    BleManager.getInstance().connect();

//                    for(int i = 0; i < 10 ; i++)
//                    {
//                        BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
//                        BleManager.getInstance().setDevice(my_dev_index);
//                        BleManager.getInstance().connect();
//                    }
                BleManager.getInstance().setDevice(my_dev_index);
                BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());

                BleManager.getInstance().connect();

                /*
                if(BleManager.getInstance().connect() == true){
                    isConnected = true;
                    Toast.makeText(MainActivity.this,"设备连接成功！",Toast.LENGTH_SHORT).show();
                    Log.d("BLE_STATE********","BleManager.STATE_CONNECTED\n");
                }else{
                    BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
                    BleManager.getInstance().setDevice(my_dev_index);
                    BleManager.getInstance().connect();
                    if(BleManager.getInstance().connect() == false){
                        Toast.makeText(MainActivity.this,"设备连接失败！",Toast.LENGTH_SHORT).show();
                        Log.d("BLE_STATE********","BleManager.STATE_DISCONNECTED\n");
                    }else{
                        isConnected = true;
                        Toast.makeText(MainActivity.this,"设备连接成功！",Toast.LENGTH_SHORT).show();
                        Log.d("BLE_STATE********","BleManager.STATE_CONNECTED\n");
                    }
                }*/



//                if(BleManager.getInstance().getConnectionState() == BleManager.STATE_CONNECTING){
//                    Log.d("BLE_STATE********","BleManager.STATE_CONNECTING\n");
//                }
//                else if(BleManager.getInstance().getConnectionState()==BleManager.STATE_CONNECTED)
//                {
//                    isConnected = true;
//                    Toast.makeText(MainActivity.this,"设备连接成功！",Toast.LENGTH_SHORT).show();
//                    Log.d("BLE_STATE********","BleManager.STATE_CONNECTED\n");
//                }else{
//                    BleManager.getInstance().prepareBLEDevice(uuidQppService,BleManager.getInstance());
//                    BleManager.getInstance().setDevice(my_dev_index);
//                    BleManager.getInstance().connect();
//
//                    if(BleManager.getInstance().getConnectionState() == BleManager.STATE_DISCONNECTED){
//                        isConnected = false;
//                        Toast.makeText(MainActivity.this,"设备连接失败！",Toast.LENGTH_SHORT).show();
//                        Log.d("BLE_STATE********","BleManager.STATE_DISCONNECT\n");
//                    }
//                }


//                if(true == BleManager.getInstance().connect()){
//                    isConnected = true;
//                    Toast.makeText(MainActivity.this,"设备连接成功！",Toast.LENGTH_SHORT).show();
//                }else{
//                    isConnected = false;
//                    Toast.makeText(MainActivity.this,"设备连接失败！",Toast.LENGTH_SHORT).show();
//                }



//                }
//
//        else{
//                    Toast.makeText(MainActivity.this,"此设备无法连接！",Toast.LENGTH_SHORT).show();
//                }




//                if(BleManager.getInstance().getConnectionState() == BleManager.STATE_CONNECTED){
//                    Toast.makeText(MainActivity.this,"蓝牙设备连接成功！",Toast.LENGTH_SHORT).show();
//                }

//                if(BleManager.STATE_CONNECTED == BleManager.getInstance().getmBLEDevice().getConnectionState()){
//                    Toast.makeText(MainActivity.this,"蓝牙设备连接成功！",Toast.LENGTH_SHORT).show();
//                }

                //Toast.makeText(MainActivity.this,"btn_connect",Toast.LENGTH_SHORT).show();

//                if (my_dev_index != (-1) &&
//                        BleManager.getInstance().getmLeDeviceListAdapter().getItem(my_dev_index).getDeviceName().matches(MY_DEV_NAME) == true){
//                    for (int j = 0; j < 10; j++) {
//                        BleManager.getInstance().prepareBLEDevice(uuidQppService, BleManager.getInstance());
//                        BleManager.getInstance().setDevice(my_dev_index);
//                        BleManager.getInstance().connect();
//                    }
//                }


//                if(BleManager.getInstance().getDeviceCount() > 0){
//                   // new BleTasks().execute();   //后台任务类连接蓝牙设备
//                }

                break;

            default:break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String value = (String) msg.obj;
            Toast.makeText(MainActivity.this, value,
                    Toast.LENGTH_SHORT).show();
        };
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String) msg.obj;
            switch (msg.what) {
                case HANDLER_ON_CONNECTION_CHANGED:
                    int newState = msg.arg1;
                    if (newState == BLEDevice.STATE_DISCONNECTED) {
                        Toast.makeText(MainActivity.this,
                                "连接已断开!", Toast.LENGTH_SHORT)
                                .show();
                        isConnected = false;
                        Log.d("MainActivity Handler:", "STATE_DISCONNECTED!");

                    } else if (newState == BLEDevice.STATE_CONNECTED) {
                        Toast.makeText(MainActivity.this,
                                "已连接!", Toast.LENGTH_SHORT).show();
                        isConnected = true;
                    }
                    break;
                case HANDLER_ON_CHARACTERISTIC_WRITE:
                    break;
                case HANDLER_ON_CHARACTERISTIC_CHANGED:

                    break;
                case HANDLER_ON_SERVICE_DISCOVERED:

                    break;
                default:
                    // LogUtil.e(DeviceListActivity.this, "Unknown message!");
                    break;
            }
        }
    };

    private boolean mScanAndBindStarted = false;
    public static final long SCAN_PERIOD = 4 * 1000;

    /**
     * 开始扫描设备
     */
    public void startScanAndBind() {
        if (BleManager.getInstance() == null || mScanAndBindStarted == true) {
            return;
        }
        mScanAndBindStarted = true;
        BleManager.getInstance().clearData();
        BleManager.getInstance().scanLeDevice(true);
        //Toast.makeText(this,BleManager.getInstance().getDeviceCount()+"",Toast.LENGTH_SHORT).show();
        registerReciver();
    }

    /**
     * 停止扫描设备
     */
    public void stopScanForBind() {
        if (BleManager.getInstance() == null)
            return;
        BleManager.getInstance().scanLeDevice(false);
        mScanAndBindStarted = false;
    }

    /**
     * 发送数据
     */
    private Object sendDataLock = new Object();
    private final static int DATALENGTH = 20;//蓝牙ble一次传输20个字节长的数据

    private boolean sendData(String value) {
        // 判断连接状态
        synchronized (sendDataLock) {
            if (BleManager.getInstance().getConnectionState() != BLEDevice.STATE_CONNECTED) {
//                Toast.makeText(MainAty.this,
//                        R.string.connecting_hint, Toast.LENGTH_SHORT).show();
            }
            // 判断value是否为空
            if (value == null || value.equals(""))
                return false;

            // 判断value长度
            if ( value.length() > DATALENGTH) {
                preDatas = StringUtils.spilt(value, DATALENGTH);
                dataIterator = preDatas.iterator();
               // sendBroadcast(new Intent(SEND_COMPLETED));
                return false;
            }
            //System.out.println("sendvalue=" + value);
            // 发送

            if (BleManager
                    .getInstance()
                    .getmBLEDevice()
                    .writeCharacteristic(UUID.fromString(uuidATService),
                            UUID.fromString(uuidATCharWrite), value) == false) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
//                        Toast.makeText(MainAty.this,
//                                R.string.write_text_send_err,
//                                Toast.LENGTH_SHORT).show();

                    }
                });
                return false;
            } else {
                return true;
            }
        }

    }

    /**
     * 设置notify
     * @param serviceUuid 服务的uuid字符串
     * @param charUuid	特征值的uuid字符串
     */
    private void setNotification(String serviceUuid, String charUuid) {
        final UUID tsUuid = UUID.fromString(serviceUuid);
        final UUID tcUuid = UUID.fromString(charUuid);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                BleManager.getInstance().getmBLEDevice()
                        .setCharacteristicNotification(tsUuid, tcUuid, true);

            }
        }, 500);

    }

    // 广播
    public static final String BLE_ACTION_CONNECTION_CHANGE = "com.tchip.tchipblehelper.action_CONNECTION_CHANGE";
    public static final String BLE_ACTION_CHARACTERISTIC_CHANGE = "com.tchip.tchipblehelper.action_CHARACTERISTIC_CHANGE";
    public static final String BLE_ACTION_CHARACTERISTIC_READ = "com.tchip.tchipblehelper.action_CHARACTERISTIC_READ";
    public static final String BLE_ACTION_CHARACTERISTIC_WRITE_STATE = "com.tchip.tchipblehelper.action_CHARACTERISTIC_WRITE_STATE";
    public static final String BLE_ACTION_SERVICES_DISCOVERED = "com.tchip.tchipblehelper.action_SERVICES_DISCOVERED";
    public static final String SEND_COMPLETED = "SEND_COMPLETED";
    public static final String BLE_FINISHU="com.tchip.finishu";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UUID uuid;
            String data;
            switch (intent.getAction()) {
                case BLE_ACTION_CONNECTION_CHANGE:
                    int laststate = intent.getIntExtra("laststate", 0);
                    int newstate = intent.getIntExtra("newstate", 0);
                    onBLEDeviceConnectionChange(laststate, newstate);
                    break;
                case BLE_ACTION_CHARACTERISTIC_CHANGE:
                    uuid = UUID.fromString(intent.getStringExtra("uuid"));
                    data = intent.getStringExtra("data");
                    onCharacteristicChanged(uuid, data);
                    // Toast.makeText(MainAty.this,"BLE_ACTION_CHARACTERISTIC_CHANGE",Toast.LENGTH_SHORT).show();
                    break;
                case BLE_ACTION_CHARACTERISTIC_READ:
                    uuid = UUID.fromString(intent.getStringExtra("uuid"));
                    data = intent.getStringExtra("data");
                    onCharacteristicRead(uuid, data);
                    // Toast.makeText(MainAty.this,"BLE_ACTION_CHARACTERISTIC_READ",Toast.LENGTH_SHORT).show();
                    break;
                case BLE_ACTION_CHARACTERISTIC_WRITE_STATE:
                    uuid = UUID.fromString(intent.getStringExtra("uuid"));
                    int state = intent.getIntExtra("state", 0);
                    onCharacteristicWriteState(uuid, state);
                    // Toast.makeText(MainAty.this,"BLE_ACTION_CHARACTERISTIC_WRITE_STATE",Toast.LENGTH_SHORT).show();
                    break;
                case BLE_ACTION_SERVICES_DISCOVERED:
                    onServicesDiscovered();
                    // Toast.makeText(MainAty.this,"BLE_ACTION_SERVICES_DISCOVERED",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_COMPLETED:
                    if (dataIterator != null) {
                        if (dataIterator.hasNext()) {
                            sendData(dataIterator.next());
                        } else {
                            dataIterator = null;
                        }
                    }
                    break;
                case BLE_FINISHU:
                    unRegisterReciver();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        registerReciver();
    }

    /**
     * 更新设备消息
     */
    private void updateDevMsg() {
//        tv_ry01_devname.setText(BleManager.getInstance().getDeviceName());
//        tv_ry01_devmac.setText(BleManager.getInstance().getDeviceAddress());
//        // TODO
//        int constate = BleManager.getInstance().getConnectionState();
//        switch (constate) {
//            case BLEDevice.STATE_CONNECTED:
//                tv_ry01_devstate.setText("connected");
//                break;
//            case BLEDevice.STATE_DISCONNECTED:
//                tv_ry01_devstate.setText("disconnected");
//                break;
//            case BLEDevice.STATE_CONNECTING:
//                tv_ry01_devstate.setText("disconnected");
//                break;
//
//        }
    }

    /**
     * 注册广播监听
     */
    public void registerReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLE_ACTION_CHARACTERISTIC_CHANGE);
        filter.addAction(BLE_ACTION_CHARACTERISTIC_READ);
        filter.addAction(BLE_ACTION_CHARACTERISTIC_WRITE_STATE);
        filter.addAction(BLE_ACTION_CONNECTION_CHANGE);
        filter.addAction(BLE_ACTION_SERVICES_DISCOVERED);
        filter.addAction(SEND_COMPLETED);
        filter.addAction(BLE_FINISHU);
        this.registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 取消广播监听
     */
    public void unRegisterReciver() {
        this.unregisterReceiver(broadcastReceiver);
    }

    public void onBLEDeviceConnectionChange(int laststate, int newstate) {
        // TODO Auto-generated method stub

        if (mHandler == null)
            return;
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_ON_CONNECTION_CHANGED;
        msg.arg1 = newstate;
        mHandler.sendMessage(msg);
    }

    public void onCharacteristicRead(UUID uuid, String data) {
        // TODO Auto-generated method stub
        System.out.println("Dlist onCharacteristicRead data=" + data);

    }

    public void onCharacteristicChanged(UUID uuid, String data) {
        // TODO Auto-generated method stub
        System.out.println("Dlist onCharacteristicChanged data=" + data);
    }

    public void onServicesDiscovered() {
        // TODO Auto-generated method stub
        System.out.println("Dlist onServicesDiscovered");
    }

    public void onCharacteristicWriteState(UUID uuid, int state) {
        // TODO Auto-generated method stub
        System.out.println("Dlist onCharacteristicWriteState ");
    }

    /**
     * 搜索蓝牙服务
     * @return
     */
    private ArrayList<BluetoothGattService> searchServices() {
        int i = 0;
        BluetoothGatt gatt = BleManager.getInstance().getmBLEDevice()
                .getBluetoothGatt();
        ArrayList<BluetoothGattService> services;
        if (gatt != null) {
            services = (ArrayList<BluetoothGattService>) gatt.getServices();
        } else {
            return null;
        }
        for (BluetoothGattService serivce : services) {
            // System.out.println("Service[" + i + "]"
            // + serivce.getUuid().toString());
            i++;
        }
        return services;
    }

    /**
     * 后台任务类
     * 用于连接蓝牙设备
     * 用BleTask连接效果最好，但是连接成功后点击按钮发数据一直不能成功
     */
    public class BleTasks extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                while (isConnected == false) {

                    //已匹配蓝牙设备
                    if (my_dev_index != (-1) && BleManager.getInstance().getmLeDeviceListAdapter().getItem(my_dev_index).getDeviceName().matches(MY_DEV_NAME) == true) {

                        //连接设备
                        for (int j = 0; j < 20; j++) {
                            BleManager.getInstance().prepareBLEDevice(uuidQppService, BleManager.getInstance());
                            BleManager.getInstance().setDevice(my_dev_index);
                            BleManager.getInstance().connect();

                            if(BleManager.getInstance().getConnectionState() == BleManager.STATE_CONNECTING){
                                BleManager.getInstance().onBLEDeviceConnectionChange(BleManager.getInstance().getmBLEDevice(),BleManager.STATE_CONNECTING,BleManager.STATE_CONNECTED);
                            }

//                            if (BleManager.getInstance().getConnectionState() == BleManager.STATE_CONNECTED) {
//                                return true;
//                            }
                        }

                        isConnected = true;

//
                    }
                    if(BleManager.getInstance().getConnectionState() == BleManager.STATE_CONNECTED){
                            BleManager.getInstance().onBLEDeviceConnectionChange(BleManager.getInstance().getmBLEDevice(),BleManager.STATE_CONNECTING,BleManager.STATE_CONNECTED);
                            return true;
                        }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }


        @Override
        protected void onProgressUpdate(Integer... values){
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }


    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 串口透传：发送数据
     */
    private void sendData() {

        if (BleManager.getInstance().getConnectionState() != BLEDevice.STATE_CONNECTED) {
            Toast.makeText(MainActivity.this,
                    "重新连接中,稍等。。。", Toast.LENGTH_SHORT).show();
            BleManager.getInstance().disconnect();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    BleManager.getInstance().connect();

                }
            }, 3000);

            return;
        }

//        String value = uartFragment.getEt_send_msg().getText().toString();
//        if (value == null || value.equals("")) {
//            return;
//        }

//        if (getSwitchStatus(R.id.hex_send_switch)) {
//            value = value.toUpperCase();
//            value = value.replace(" ", "");
//            value = StringUtils.hexStr2Str(value);
//
//        }

//        // 发送新行
//        if (getSwitchStatus(R.id.send_unewline_switch)) {
//            value = value + "\r\n";
//        }

       // System.out.println("send value str=" + value);
//        if (BleManager.getInstance().write(value) == false) {
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    Toast.makeText(SerialTransmissionActivity.this,
//                            R.string.write_text_send_err, Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
//        } else {
//
//            Message msg = handler.obtainMessage();
//            msg.obj = value;
//            handler.sendMessage(msg);
//
//        }

    }

    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String TAG = "------BLUE-------";
    public boolean setCharacteristicNotification(UUID gatservice_uuid, UUID char_uuid, boolean enable) {
        // synchronized(mGattLock){
        try {
            if (mBluetoothGatt == null)
                return false;
            BluetoothGattService bgs = mBluetoothGatt.getService(gatservice_uuid);
            if (bgs == null) {
                Log.e(TAG, "setCharacteristicNotification bgs==null");
                return false;
            }

            BluetoothGattCharacteristic bgc = bgs.getCharacteristic(char_uuid);
            if (bgc == null) {
                Log.e(TAG, "setCharacteristicNotification bgc==null");
                return false;
            }

            int properties = bgc.getProperties();
            if ((properties
                    & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                if (mBluetoothGatt.setCharacteristicNotification(bgc, enable)) {
                    Log.w(TAG, "setCharacteristicNotification succeeded");

                    BluetoothGattDescriptor descriptor = bgc
                            .getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Log.w(TAG, "setCharacteristicNotification succeeded");

                    return mBluetoothGatt.writeDescriptor(descriptor);
                } else {
                    Log.e(TAG, "setCharacteristicNotification failed");
                    return false;
                }
            } else {
                Log.e(TAG, gatservice_uuid + "->" + char_uuid + " can not notify !");
                return false;
            }
        } catch (Exception ex) {
            Log.e(TAG, "setCharacteristicNotification mBluetoothGatt dead .");
            return false;
        }
    }
    //接收数据
    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            if(newState == BluetoothProfile.STATE_CONNECTED)
            {
                mBluetoothGatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                close();
            }
        }
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data= characteristic.getValue();
            if(data != null)
            {
            }
        }
        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //Log.w(TAG, "BluetoothGattCallback onCharacteristicRead true");
            } else {
                //Log.e(TAG, "BluetoothGattCallback onCharacteristicRead failed");
            }
        }
    };

}
