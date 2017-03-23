package com.activitys.bluetoothsample.providers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.activitys.bluetoothsample.R;
import com.activitys.bluetoothsample.multitypebean.IMMultitypeBeanMe;
import com.activitys.bluetoothsample.multitypebean.IMMultitypeBeanOther;
import com.activitys.bluetoothsample.viewholdes.IMViewHolder;

import MultiType.ItemViewProvider;

/**
 * Created by Administrator on 2017/2/13.
 */

public class IMMeProvider extends ItemViewProvider<IMMultitypeBeanMe,IMViewHolder> {
    @NonNull
    @Override
    protected IMViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new IMViewHolder(inflater.inflate(R.layout.im_me,parent,false));
    }

    @Override
    protected void onBindViewHolder(@NonNull IMViewHolder holder, @NonNull IMMultitypeBeanMe s) {
        holder.contentTextView.setText(s.getMsg());
    }
}
