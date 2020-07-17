package com.example.soilrespiration.common;

import com.example.soilrespiration.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 常规返回报文格式化
 */
public class CommonResponse {

    // 任务执行状态码
    private String resCode = "";

    // 任务失败说明
    private String resMsg = "";

    /**
     *  通用报文返回构造函数
     * @param responseString Json格式的返回字符串
     */
    public CommonResponse(String responseString){

        // 日志输出原始应答报文
        LogUtil.logResponse(responseString);

        try {
            JSONObject root = new JSONObject(responseString);
            resCode = root.getString("resCode");
            resMsg = root.optString("resMsg");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getResCode(){
        return resCode;
    }

    public String getResMsg(){
        return resMsg;
    }
}
