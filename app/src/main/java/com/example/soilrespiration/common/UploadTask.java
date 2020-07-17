package com.example.soilrespiration.common;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadTask extends AsyncTask<String, String, String> {

    // 请求类对象
    private CommonRequest request;

    // BaseActivity中基础问题的处理
    private Handler mHandler;

    // 返回信息处理回调接口
    private ResponseHandler rHandler;

    public UploadTask(CommonRequest request, Handler mHandler, ResponseHandler rHandler){
        this.request = request;
        this.mHandler = mHandler;
        this.rHandler = rHandler;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder resultBuf = new StringBuilder();
        try {
            URL url = new URL(strings[0]);

            // 第一步：使用URL打开一个HttpURLConnection连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 第二步：设置HttpURLConnection连接相关属性
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestMethod("POST");  // 设置请求方法，"POST或GET"
            connection.setConnectTimeout(8000);  // 设置连接建立的超时时间
            connection.setReadTimeout(50000);  // 设置网络报文收发超时时间
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // POST方法需要在获取输入流之前向连接写入POST参数
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //out.writeBytes(request.getJsonStr());
            out.write(request.getJsonStr().getBytes());
            out.flush();

            // 第三步：打开连接输入流读取返回报文
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                // 通过连接的输入流获取下发报文，然后就是Java的流处理
                InputStream in = connection.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = read.readLine()) != null){
                    resultBuf.append(line);
                }
                return resultBuf.toString();
            }else {
                // 异常情况，如404/500...
                mHandler.obtainMessage(Constants.HANDLER_HTTP_RECEIVE_FAIL, "[" + responseCode + "]" + connection.getResponseMessage()).sendToTarget();
            }
        }catch (IOException e){
            // 网络请求过程中发生IO异常
            mHandler.obtainMessage(Constants.HANDLER_HTTP_SEND_FAIL,e.getClass().getName() + " : " + e.getMessage()).sendToTarget();
        }
        return resultBuf.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (rHandler != null){
            if (!"".equals(s)){
                CommonResponse response = new CommonResponse(s);
                // 这里response.getResCode()为多少表示业务完成也是和服务器约定好的
                if ("100".equals(response.getResCode())){  // 正确
                    rHandler.success(response);
                }else {
                    rHandler.fail(response.getResCode(), response.getResMsg());
                }
            }
        }
    }
}
