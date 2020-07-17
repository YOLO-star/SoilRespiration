package com.example.soilrespiration.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.soilrespiration.common.CommonRequest;
import com.example.soilrespiration.common.Constants;
import com.example.soilrespiration.common.ResponseHandler;
import com.example.soilrespiration.common.UploadTask;
import com.example.soilrespiration.util.DialogUtil;
import com.example.soilrespiration.util.LoadingDialogUtil;
import com.example.soilrespiration.util.LogUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void sendHttpPostRequest(String url, CommonRequest request, ResponseHandler responseHandler, boolean showLoadingDialog){
        new UploadTask(request, mHandler, responseHandler).execute(url);
        if (showLoadingDialog){
            LoadingDialogUtil.showLoadingDialog(BaseActivity.this);
        }
    }

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == Constants.HANDLER_HTTP_SEND_FAIL){
                LogUtil.logErr(msg.obj.toString());

                LoadingDialogUtil.cancelLoading();
                DialogUtil.showHintDialog(BaseActivity.this, "请求发送失败，请重试", true);
            }else if (msg.what == Constants.HANDLER_HTTP_RECEIVE_FAIL){
                LogUtil.logErr(msg.obj.toString());

                LoadingDialogUtil.cancelLoading();
                DialogUtil.showHintDialog(BaseActivity.this, "请求接收失败，请重试",true);
            }
        }
    };
}
