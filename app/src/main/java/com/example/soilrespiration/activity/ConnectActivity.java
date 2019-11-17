package com.example.soilrespiration.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soilrespiration.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.example.soilrespiration.common.Constants;
import com.example.soilrespiration.common.EventMsg;
import com.example.soilrespiration.service.SocketService;

import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    @BindView(R.id.ipTv)
    EditText ipTv;
    @BindView(R.id.portTv)
    EditText portTv;
    @BindView(R.id.connectBtn)
    Button connectBtn;
    @BindView(R.id.rememberConn)
    CheckBox rememberConn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private boolean isConnectSuccess = false;
    public static final String TAG = "ConnectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_connection", false);
        if (isRemember){
            //将主机IP和端口都设置到文本框中
            String hostip = pref.getString("HostIP", "");
            String port = pref.getString("Port", "");
            ipTv.setText(hostip);
            portTv.setText(port);
            rememberConn.setChecked(true);
        }

        /*register EventBus*/
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        Log.d(TAG, "onCreate");
    }

    @OnClick(R.id.connectBtn)
    public void onViewClicked(){
        String ip = ipTv.getText().toString().trim();
        String port = portTv.getText().toString().trim();

        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){   //Returns true if the string is null or 0-length.
            Toast.makeText(this, "ip和端口号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        editor = pref.edit();
        if (rememberConn.isChecked()){
            editor.putBoolean("remember_connection", true);
            editor.putString("HostIP", ip);
            editor.putString("Port", port);
        }else {
            editor.clear();
        }
        editor.apply();

        /*先判断Service是否正在运行，如果正在运行，给出提示，防止启动多个service
        if (isServiceRunning("com.example.soilrespiration.service.SocketService")){
            Toast.makeText(this, "连接服务已运行",Toast.LENGTH_SHORT).show();
            return;
        }  */

        /*启动service*/
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra(Constants.INTENT_IP, ip);  // 第一个参数是键
        intent.putExtra(Constants.INTENT_PORT, port);
        startService(intent);
    }

    /*连接成功的话，跳转到收集界面*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void skipToCollectActivity(EventMsg msg){
        if (msg.getTag().equals(Constants.CONNET_SUCCESS)){
            /*接收到这个消息说明连接成功*/
            isConnectSuccess = true;
            Intent intent = new Intent(this, CollectActivity.class);
            startActivity(intent);

            this.finish();
        }
    }

    /**
     * 判断服务是否运行
     */
    private boolean isServiceRunning(final String className){
        //ActivityManager:This class gives information about, and interacts with, activities, services, and the containing process.
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);  //For backwards compatibility, it will still return the caller's own services.
        if (info == null || info.size() == 0)
            return false;
        for (ActivityManager.RunningServiceInfo aInfo : info){
            if (className.equals(aInfo.service.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceRunning("com.example.soilrespiration.service.SocketService")){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ConnectActivity.this);
            dialog.setTitle("有一个任务正在进行中");
            dialog.setMessage("点击进入");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ConnectActivity.this, CollectActivity.class);
                    startActivity(intent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            dialog.show();
        }
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
        /*unregister EventBus*/
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }

        /*如果没有连接成功，则退出的时候停止服务
        if (!isConnectSuccess){
            Intent intent = new Intent(this, SocketService.class);
            stopService(intent);
        }  */
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

}
