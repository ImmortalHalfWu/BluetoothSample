package com.activitys.bluetoothsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

public class MainActivity extends AppCompatActivity {

    private final int OPEN_BLUE_TOOTH_REQUEST_CODE = 0x110;
    private BluetoothAdapter bluetoothAdapter;
    private View contentView;
    private TextView diviceTextName;
    private ListView oldListView,nowListView;
    private ListViewAdapter oldListViewAdapter,nowListViewAdapter;
    private BlueToothServerThread mBlueToothServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initFab();
        initView();
        initReceiver();

        initBluetooth();
        initServerThread();

    }

    private void initServerThread() {

        mBlueToothServerThread = new BlueToothServerThread(bluetoothAdapter, new BlueToothServerThread.ServerThreadCallback() {
            @Override
            public void onErro(Exception e) {
                e.printStackTrace();
            }

            @Override
            public boolean acceptCallback(BluetoothSocket socket) {
                startImActivity(socket);
                return false;
            }
        });
        mBlueToothServerThread.start();

    }

    private void initReceiver() {

        BluetoothBroadCastReceiver.startReceiver(this, new BluetoothBroadCastReceiver.BluetoothCallBack() {
            @Override
            public void divice(BluetoothDevice device) {
                if (nowListViewAdapter == null){
                    nowListViewAdapter = new ListViewAdapter(new ArrayList<BluetoothDevice>());
                    nowListView.setAdapter(nowListViewAdapter);
                }
                nowListViewAdapter.addObject(device);
            }

            @Override
            public void over() {
                if (nowListViewAdapter != null){
                    nowListViewAdapter.notifyDataSetInvalidated();
                }
            }
        });

    }

    private void initView() {
        contentView = findViewById(R.id.content_main);
        diviceTextName = (TextView) findViewById(R.id.textView2);
        oldListView = (ListView) findViewById(R.id.listView1);
        nowListView = (ListView) findViewById(R.id.listView2);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((ListViewAdapter)adapterView.getAdapter()).getBlueTootjDevoce().get(i);
                new BlueToothClientThread(((ListViewAdapter) adapterView.getAdapter()).getBlueTootjDevoce().get(i), bluetoothAdapter, new BlueToothClientThread.BlueToothClientCallback() {
                    @Override
                    public void onErro(Exception e) {

                    }

                    @Override
                    public void onSuccess(BluetoothSocket socket) {
                        startImActivity(socket);
                    }
                }).start();
            }
        };

        nowListView.setOnItemClickListener(onItemClickListener);
        oldListView.setOnItemClickListener(onItemClickListener);

    }


    private void startImActivity(BluetoothSocket socket) {
        IMThread imThread = new IMThread(socket);
        imThread.start();
        IMActivity.startActivity(this,imThread,socket.getRemoteDevice().getName());
    }

    private void initBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            showSnackBar("本设备不支持蓝牙");
            return;
        }

        if (!bluetoothAdapter.isEnabled()){
            showSnackBar("蓝牙未开启","开启",new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBluetooth();
                }
            });
            return;
        }

        bluetoothOpenSuccess();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode ){

            case OPEN_BLUE_TOOTH_REQUEST_CODE:

                if (resultCode == RESULT_OK){
                    bluetoothOpenSuccess();
                }else {
                    bluetoothOpenFail();
                }

                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void bluetoothOpenSuccess() {

        if (bluetoothAdapter != null){

            diviceTextName.setText(bluetoothAdapter.getName());
            bluetoothAdapter.startDiscovery();

            List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
            bluetoothDevices.addAll(bluetoothAdapter.getBondedDevices());

            if (bluetoothDevices.size() == 0){
                showSnackBar("未找到已配对设备");
                return;
            }

            if (oldListViewAdapter == null){
                oldListViewAdapter = new ListViewAdapter(bluetoothDevices);
                oldListView.setAdapter(oldListViewAdapter);
            }else {
                oldListViewAdapter.setBlueTootjDevoce(bluetoothDevices);
            }

            oldListViewAdapter.notifyDataSetInvalidated();

        }

    }


    private void bluetoothOpenFail() {
        showSnackBar("蓝牙无法打开");
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()){
                    showSnackBar("蓝牙已开启");
                }else {
                    showSnackBar("蓝牙未开启","开启",new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openBluetooth();
                        }
                    });
                }

            }
        });
    }

    private void openBluetooth(){
        Intent openBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        openBluetoothIntent.putExtra(String.valueOf(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE),300);
        startActivityForResult(openBluetoothIntent,OPEN_BLUE_TOOTH_REQUEST_CODE);
    }

    private void showSnackBar(String msg){
        Snackbar.make(contentView,msg,Snackbar.LENGTH_LONG).show();
    }

    private void showSnackBar(String msg, String action, View.OnClickListener listener){
        Snackbar.make(contentView,msg,Snackbar.LENGTH_LONG).setAction(action,listener).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static final class ListViewAdapter extends BaseAdapter{

        private List<BluetoothDevice> blueTootjDevoce;

        public ListViewAdapter(List<BluetoothDevice> sources){
            blueTootjDevoce = sources;
        }

        @Override
        public int getCount() {
            return blueTootjDevoce == null ? 0  : blueTootjDevoce.size();
        }

        @Override
        public Object getItem(int i) {
            return blueTootjDevoce.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textview;
            if (view == null){
                textview = (TextView) LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
            }else {
                textview = (TextView) view;
            }

            textview.setText(blueTootjDevoce.get(i).getName());

            return textview;
        }

        public void addObject(@NonNull BluetoothDevice bluetoothDevice){
            this.blueTootjDevoce.add(bluetoothDevice);
        }

        public void setBlueTootjDevoce(List<BluetoothDevice> blueTootjDevoce) {
            this.blueTootjDevoce = blueTootjDevoce;
        }
        public List<BluetoothDevice> getBlueTootjDevoce() {
            return blueTootjDevoce;
        }
    }


    static final class BluetoothBroadCastReceiver extends BroadcastReceiver{


        public static final void startReceiver(@NonNull Context context, @NonNull BluetoothCallBack callback){
            BluetoothBroadCastReceiver mReceiver = new BluetoothBroadCastReceiver(callback);

            IntentFilter intentFilter = new IntentFilter(ACTION_FOUND);
            context.registerReceiver(mReceiver,intentFilter);

            intentFilter = new IntentFilter(ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(mReceiver,intentFilter);

        }

        private BluetoothCallBack mCallback;

        private BluetoothBroadCastReceiver(@NonNull BluetoothCallBack callback){
            mCallback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_FOUND.equals(intent.getAction())){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    mCallback.divice(device);
                }

                return;
            }
            if (ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){

                mCallback.over();

                return;
            }

        }

        public interface BluetoothCallBack{
            void divice(BluetoothDevice device);
            void over();
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlueToothServerThread.free();
    }
}
