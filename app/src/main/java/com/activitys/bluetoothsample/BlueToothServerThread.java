package com.activitys.bluetoothsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/13.
 */

public class BlueToothServerThread extends Thread {
    public static final UUID SERVER_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private boolean isRun = true;
//    private final BluetoothAdapter mAdapter;
    private BluetoothServerSocket mBluetoothServerSocket;
    private ServerThreadCallback mCallback;

    public BlueToothServerThread(@NonNull BluetoothAdapter adapter, @NonNull ServerThreadCallback Callback){


        mCallback = Callback;

        if (!adapter.isEnabled()){
            mCallback.onErro(new IllegalAccessException("蓝牙未开启"));
            return;
        }


        try {
            mBluetoothServerSocket = adapter.listenUsingRfcommWithServiceRecord(adapter.getName(),SERVER_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            mBluetoothServerSocket = null;
            mCallback.onErro(e);
        }

    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket;
        while (isRun && mBluetoothServerSocket != null){

            try {

                bluetoothSocket = mBluetoothServerSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
                mCallback.onErro(e);
                break;
            }

            if (bluetoothSocket != null && mCallback!= null){
                if (mCallback.acceptCallback(bluetoothSocket)){
                    free();
                }
            }else{
                mCallback.onErro(new NullPointerException("连接失败"));
            }

        }

    }


    public void free(){

        try {
            isRun = false;
            mBluetoothServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface ServerThreadCallback{

        void onErro(Exception e);
        boolean acceptCallback(BluetoothSocket socket);

    }

}
