package com.activitys.bluetoothsample.providers;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.activitys.bluetoothsample.R;
import com.activitys.bluetoothsample.multitypebean.IMMultitypeBeanOther;
import com.activitys.bluetoothsample.viewholdes.IMViewHolder;

import MultiType.ItemViewProvider;

/**
 * Created by Administrator on 2017/2/13.
 */

public class IMOtherProvider extends ItemViewProvider<IMMultitypeBeanOther,IMViewHolder> {
    @NonNull
    @Override
    protected IMViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new IMViewHolder(inflater.inflate(R.layout.im_other,parent,false));
    }

    @Override
    protected void onBindViewHolder(@NonNull IMViewHolder holder, @NonNull IMMultitypeBeanOther s) {
        holder.contentTextView.setText(s.getMsg());
    }
}