package com.example.soilrespiration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.soilrespiration.common.EventMsg;
import com.example.soilrespiration.common.Constants;
import com.example.soilrespiration.common.EventTransfer;
import com.example.soilrespiration.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    /*socket*/
    private Socket socket;
    /*连接线程*/
    private Thread connectThread;
    private Timer timer = new Timer();  //A facility for threads to schedule tasks for future execution in a background thread.
    private OutputStream outputStreamBeat;
    private DataOutputStream outputStream;
    private InputStream inputStream = null;
    //private Callback callback;

    private SocketBinder socketBinder = new SocketBinder();
    private String ip;
    private String port;
    private TimerTask task;  //A task that can be scheduled for one-time or repeated execution by a Timer.

    /*默认重连*/
    private boolean isReConnect = true;

    private Handler handler = new Handler(Looper.getMainLooper());  //Returns the application's main looper, which lives in the main thread of the application.

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return socketBinder;
    }

    public class SocketBinder extends Binder {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService*/
        public SocketService getService(){
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*拿到传递过来的ip和端口号*/
        ip = intent.getStringExtra(Constants.INTENT_IP);
        port = intent.getStringExtra(Constants.INTENT_PORT);

        /*初始化socket*/
        initSocket();

        return super.onStartCommand(intent, flags, startId);
    }

    /*初始化socket*/
    private void initSocket(){
        if (socket == null && connectThread == null){
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    socket = new Socket();  //Creates an unconnected socket, with the system-default type of SocketImpl.
                    try {
                        /*超时时间位2秒*/
                        socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)), 2000);
                        /*连接成功的话,接收数据*/
                        if (socket.isConnected()){
                            /*因为Toast是要运行在主线程的 这里是子线程 所以需要到主线程那里去显示toast*/
                            toastMsg("socket已连接");

                            /*发送连接成功的消息*/
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNET_SUCCESS);
                            EventBus.getDefault().post(msg);
                            /*接收数据*/
                            receiveData();
                            /*发送心跳数据*/
                            sendBeatData();
                      }
                    }catch (IOException e){
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException){
                            toastMsg("连接超时，正在重连");
                            releaseSocket();
                        }else if (e instanceof NoRouteToHostException){
                            toastMsg("该地址不存在，请检查");
                            stopSelf();
                        }else if (e instanceof ConnectException){
                            toastMsg("连接异常或被拒绝，请检查");
                            stopSelf();
                        }
                    }
                }
            });
            /*启动连接线程*/
            connectThread.start();
        }
    }

    /*因为Toast是要运行在主线程的，所以需要到主线程那里去显示toast*/
    private void toastMsg(final String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*public void setCallback(Callback callback){
        this.callback = callback;
    }

    public static interface Callback{
        void onDataChange(String data);
    }  */

    /*接收数据*/
    private void receiveData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //final StringBuilder sb = new StringBuilder();
                    inputStream = socket.getInputStream();
                    //byte[] buffer = new byte[1024];
                    DataInputStream input = new DataInputStream(inputStream);
                    while (socket.isConnected()){
                        //int readSize = input.read(buffer);
                        /* The process of unpacking*/
                        int totalLen = input.readInt();
                        int flag = input.readInt();
                        if (flag == 66666){
                            byte[] data = new byte[totalLen - 4 - 4];
                            input.readFully(data);
                            String msg = new String(data);
                            EventBus.getDefault().post(new EventTransfer(msg));
                            Utility.handleDatabase(msg);
                        }
                        //if (readSize > 0){
                            //String value = new String(buffer,0,readSize);
                            //Log.d("SocketService: ", );
                            //String clientInputStr = input.readUTF();
                            //EventBus.getDefault().post(new EventTransfer(clientInputStr));
                        //}

                        /* int readSize = inputStream.read(buffer);
                        if (readSize > 0)
                        {
                            sb.append(new String(buffer, 0, readSize));
                            callback.onDataChange(sb.toString());
                        }  */
                    }
                }catch (IOException e){
                    /*接收失败,说明socket断开了或者出现了其他错误*/
                    toastMsg("连接断开，正在重连");
                    /*重连*/
                    releaseSocket();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*发送数据*/
    public void sendOrder(final String order){
        if (socket != null && socket.isConnected()){
            /*发送指令*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //outputStream = socket.getOutputStream();
                        outputStream  = new DataOutputStream(socket.getOutputStream());
                        if (outputStream != null){
                            //outputStream.write((order).getBytes("UTF-8"));
                            outputStream.writeUTF(order);
                            outputStream.flush();  //Flushes this output stream and forces any buffered output bytes to be written out.
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            toastMsg("socket连接错误，请重试");
        }
    }

    /*定时发送数据*/
    private void sendBeatData(){
        if (timer == null){
            timer = new Timer();
        }

        if (task == null){
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStreamBeat = socket.getOutputStream();

                        //这里的编码方式根据你的需求去改
                        outputStreamBeat.write(("test").getBytes("gbk"));
                        outputStreamBeat.flush();
                    }catch (Exception e){
                        //发送失败说明socket断开了或者出现了其他错误
                        toastMsg("连接断开，正在重连");
                        //重连
                        releaseSocket();
                        e.printStackTrace();
                    }
                }
            };
        }
        timer.schedule(task, 0, 2000);
    }

    /*释放资源*/
    private void releaseSocket(){
        if (task != null){
            task.cancel();
            task = null;
        }
        if (timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (outputStream != null){
            try {
                outputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null){
            try {
                socket.close();
            }catch (IOException e){

            }
            socket = null;
        }

        if (connectThread != null){
            connectThread = null;
        }

        /*重新初始化socket*/
        if (isReConnect){
            initSocket();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SocketService", "onDestroy");
        isReConnect = false;
        releaseSocket();
    }
}
