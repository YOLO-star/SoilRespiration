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
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.EventMsg;
import com.example.soilrespiration.common.EventTransfer;
import com.example.soilrespiration.common.QueryAdapter;
import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.common.TimerEvent;
import com.example.soilrespiration.service.SocketService;
import com.example.soilrespiration.util.ConvertUtil;
import com.example.soilrespiration.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.soilrespiration.util.ConvertUtil.convertToInt;

public class CollectActivity extends AppCompatActivity {
    /*@BindView(R.id.tvOut)
    TextView tvOut;  */
    @BindView(R.id.rise_Btn)
    Button riseBtn;
    @BindView(R.id.down_Btn)
    Button downBtn;
    @BindView(R.id.disconnect_Btn)
    Button disconnectBtn;
    @BindView(R.id.auto_Btn)
    Button autoBtn;
    @BindView(R.id.start_Btn)
    Button startBtn;
    @BindView(R.id.stop_Btn)
    Button stopBtn;
    @BindView(R.id.cycle_settings)
    EditText cycleSettings;
    @BindView(R.id.time_show)
    TextView timeShow;
    @BindView(R.id.cycle_count)
    TextView cycleCount;
    private ServiceConnection sc;
    public SocketService socketService;
    private List<ShowAll> showAllList = new ArrayList<>();
    private QueryAdapter adapter;
    private boolean autooptions = true;   // true 代表 start，false 代表 stop
    public static final String TAG = "CollectActivity";
    private int cycleTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        //注册EventBus
        EventBus.getDefault().register(this);
        bindSocketService();
        Toolbar collectTool = (Toolbar) findViewById(R.id.collect_tool);
        setSupportActionBar(collectTool);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.collect_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QueryAdapter(showAllList);
        recyclerView.setAdapter(adapter);
        autoBtn.setText("自动模式");
        cycleSettings.setText(String.valueOf(60));
        Log.d(TAG, "onCreate");
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
                    public void onDataChange(long data) {
                        Message msg = new Message();
                        msg.obj = data;
                        handler.sendMessage(msg);
                    }
                });  */
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            /*
             private Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                    String time = dateFormat.format(new Date(msg.obj.toString()));
                    timeShow.setText(time);
                    //tvOut.setText(msg.obj.toString());
                }
            };  */

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timerEvent(TimerEvent timer){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String time = dateFormat.format(new Date(timer.getTimer()));
        timeShow.setText(time);
        int seconds = (int) (timer.getTimer() / 1000);
        if (seconds % cycleTimer == 0){
            int show = seconds / cycleTimer;
            if (show > 0){
                cycleCount.setText("cycle :"+show);
            }
        }
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
    public void setAutoBtn(){
        if (autooptions){
            autooptions = false;
            String cmd = "startauto";
            socketService.sendOrder(cmd);
            autoBtn.setText("停止");
        }else {
            autooptions = true;
            String cmd = "stopauto";
            socketService.sendOrder(cmd);
            autoBtn.setText("自动模式");
        }
    }

    @OnClick(R.id.start_Btn)
    public void setStartBtn(){
        String cmd = "startmanual";
        String cycle = cycleSettings.getText().toString().trim();
        socketService.sendOrder(cmd);
        cycleTimer = Integer.parseInt(cycle);
        socketService.startTimer(cycleTimer);
    }

    @OnClick(R.id.stop_Btn)
    public void setStopBtn(){
        String cmd = "stopmanual";
        socketService.sendOrder(cmd);
        socketService.pauseTimer();
    }

    @OnClick(R.id.disconnect_Btn)
    public void setDisconnectBtn(){
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        Intent intent1 = new Intent(CollectActivity.this, ConnectActivity.class);
        startActivity(intent1);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CollectActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
