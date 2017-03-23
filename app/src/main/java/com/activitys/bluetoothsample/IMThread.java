package com.activitys.bluetoothsample;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class IMThread extends Thread implements IMWriteCallBack{

    private boolean isRun = true;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private IMReadCallBack readCallBack;

    public IMThread(@NonNull BluetoothSocket socket,@NonNull IMReadCallBack readCallBack) {

        this.socket = socket;
        this.readCallBack = readCallBack;
        initStream();

    }

    public IMThread(@NonNull BluetoothSocket socket) {
        this.socket = socket;
        initStream();
    }

    private void initStream() {
        if (socket == null || !socket.isConnected()){
            readCallBack.onErro(new IllegalAccessException("蓝牙断开连接"));

            return;
        }

        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            readCallBack.onErro(e);
        }
    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];

        while (isRun && socket != null && socket.isConnected()){

            try {

                int read = -1;
                read = inputStream.read(buffer);
                if (read == -1) continue;
                readCallBack.read(new String(buffer,0,read,"UTF-8"));
                inputStream.reset();

            } catch (IOException e) {
                e.printStackTrace();
                readCallBack.onErro(e);
            }
        }


    }


    @Override
    public void write(String msg){

        try {

            outputStream.write(msg.getBytes("UTF-8"));
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            readCallBack.onErro(e);
        }

    }

    @Override
    public void setReadCallBack(IMReadCallBack callBack) {
        readCallBack  = callBack;
    }

    public void free(){

        try {

            if (socket != null && socket.isConnected()){
                socket.close();
            }
            socket = null;

            outputStream.flush();
            outputStream.close();
            outputStream = null;

            inputStream.reset();
            inputStream.close();

            inputStream = null;

        } catch (IOException e) {
            e.printStackTrace();
            readCallBack.onErro(e);
        }

        readCallBack = null;

    }
}
