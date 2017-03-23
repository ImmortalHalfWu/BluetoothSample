package com.activitys.bluetoothsample;

/**
 * Created by Administrator on 2017/2/13.
 */

public interface IMWriteCallBack {

    void write(String msg);
    void setReadCallBack(IMReadCallBack callBack);

}
