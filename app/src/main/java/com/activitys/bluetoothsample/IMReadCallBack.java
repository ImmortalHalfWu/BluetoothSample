package com.activitys.bluetoothsample;

/**
 * Created by Administrator on 2017/2/13.
 */

public interface IMReadCallBack {

    void onErro(Exception e);
    void read(String msg);
    void setWriteCallBack(IMWriteCallBack callBack);

}
