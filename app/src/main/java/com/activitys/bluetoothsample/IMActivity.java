package com.activitys.bluetoothsample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.activitys.bluetoothsample.multitypebean.IMMultitypeBean;
import com.activitys.bluetoothsample.multitypebean.IMMultitypeBeanMe;
import com.activitys.bluetoothsample.multitypebean.IMMultitypeBeanOther;
import com.activitys.bluetoothsample.providers.IMMeProvider;
import com.activitys.bluetoothsample.providers.IMOtherProvider;

import java.util.ArrayList;
import java.util.List;

import MultiType.MultiTypeAdapter;

public class IMActivity extends AppCompatActivity implements IMReadCallBack{

    private RecyclerView recyclerView;
    private MultiTypeAdapter adapter;
    private List<IMMultitypeBean> msgList;
    private static String mTitle;
    private EditText editText;
    private Button button;
    private static IMWriteCallBack mWriteCallBack;

    public static void startActivity(Context context,IMWriteCallBack writeCallBack,String title){
        context.startActivity(new Intent(context,IMActivity.class));
        mWriteCallBack = writeCallBack;
        mTitle = title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);

        mWriteCallBack.setReadCallBack(this);

        getSupportActionBar().setTitle(mTitle);
        initView();
        initAdapter();
        
    }

    private void initAdapter() {
        msgList = new ArrayList<>();
        msgList.add(new IMMultitypeBeanOther("test"));
        adapter = new MultiTypeAdapter(msgList);
        adapter.register(IMMultitypeBeanMe.class, new IMMeProvider());
        adapter.register(IMMultitypeBeanOther.class, new IMOtherProvider());

        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

    }

    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recuclerView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText())){
                    mWriteCallBack.write(editText.getText().toString());
                    msgList.add(new IMMultitypeBeanMe(editText.getText().toString()));
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(msgList.size());
                    editText.setText("");
                }
            }
        });

    }

    @Override
    public void onErro(Exception e) {

    }

    @Override
    public void read(String msg) {
        msgList.add(new IMMultitypeBeanOther(msg));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(msgList.size());
            }
        });
    }

    @Override
    public void setWriteCallBack(IMWriteCallBack callBack) {
        mWriteCallBack = callBack;
    }
}
