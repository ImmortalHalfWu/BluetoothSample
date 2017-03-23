package com.activitys.bluetoothsample.viewholdes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.activitys.bluetoothsample.R;

/**
 * Created by Administrator on 2017/2/13.
 */

public class IMViewHolder extends RecyclerView.ViewHolder {

    public final TextView contentTextView;

    public IMViewHolder(View itemView) {
        super(itemView);
        contentTextView = (TextView) itemView.findViewById(R.id.textView1);
    }
}
