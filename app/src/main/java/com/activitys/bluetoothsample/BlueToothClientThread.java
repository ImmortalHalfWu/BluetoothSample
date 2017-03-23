package com.activitys.bluetoothsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;

import static android.R.string.no;

/**
 * Created by Administrator on 2017/2/13.
 */

public class BlueToothClientThread extends Thread {

    private  BluetoothDevice serverDevice;
    private  BluetoothAdapter bluetoothAdapter;
    private  BlueToothClientCallback mCallBack;
    private  BluetoothSocket socket = null;

    public BlueToothClientThread(@NonNull BluetoothDevice serverDevice, @NonNull BluetoothAdapter bluetoothAdapter,@NonNull BlueToothClientCallback callback ) {

        this.serverDevice = serverDevice;
        this.bluetoothAdapter = bluetoothAdapter;
        mCallBack = callback;

        try {
            socket = serverDevice.createRfcommSocketToServiceRecord(BlueToothServerThread.SERVER_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            mCallBack.onErro(e);
        }


    }

    @Override
    public void run() {

        bluetoothAdapter.cancelDiscovery();

        if (!socket.isConnected()){

            try {

                socket.connect();

            } catch (IOException e) {
                e.printStackTrace();
                mCallBack.onErro(e);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    mCallBack.onErro(e1);
                }
            }

        }

        if (socket.isConnected()){
            mCallBack.onSuccess(socket);
        }

    }


    public void free(){
        try {

            if (socket != null && socket.isConnected()){
                socket.close();
                socket = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            mCallBack.onErro(e);
        }

        serverDevice = null;
        bluetoothAdapter = null;
        mCallBack = null;

    }

    public interface BlueToothClientCallback{

        void onErro(Exception e);
        void onSuccess(BluetoothSocket socket);

    }

}
