package com.example.soilrespiration.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.EventMsg;
import com.example.soilrespiration.common.EventTransfer;
import com.example.soilrespiration.common.QueryAdapter;
import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.service.SocketService;
import com.example.soilrespiration.util.ConvertUtil;
import com.example.soilrespiration.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectActivity extends AppCompatActivity {
    /*@BindView(R.id.tvOut)
    TextView tvOut;  */
    @BindView(R.id.rise_Btn)
    Button riseBtn;
    @BindView(R.id.down_Btn)
    Button downBtn;
    @BindView(R.id.auto_Btn)
    Button autoBtn;
    private ServiceConnection sc;
    public SocketService socketService;
    private List<ShowAll> showAllList = new ArrayList<>();
    private QueryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        //注册EventBus
        EventBus.getDefault().register(this);
        bindSocketService();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.collect_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QueryAdapter(showAllList);
        recyclerView.setAdapter(adapter);
    }

    private void bindSocketService(){

        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder)service;
                socketService = binder.getService();
                /*socketService.setCallback(new SocketService.Callback() {
                    @Override
                    public void onDataChange(String data) {
                        Message msg = new Message();
                        msg.obj = data;
                        handler.sendMessage(msg);
                    }
                });  */
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

             /*private Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    tvOut.setText(msg.obj.toString());
                }
            }; */

        };

        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventTransfer transfer){
        String done = transfer.getTransfer();
        ShowAll analyse = Utility.handleCollection(done);
        adapter.addData(analyse);
    }

    @OnClick(R.id.rise_Btn)
    public void onRiseClicked(){
        String cmd = "rise";
        socketService.sendOrder(cmd);
    }

    @OnClick(R.id.down_Btn)
    public void onDownClicked(){
        String cmd = "down";
        socketService.sendOrder(cmd);
    }

    @OnClick(R.id.auto_Btn)
    public void onAutoClicked(){
        String cmd = "auto";
        socketService.sendOrder(cmd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }
}
