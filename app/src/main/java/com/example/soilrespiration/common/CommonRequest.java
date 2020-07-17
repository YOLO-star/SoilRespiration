package com.example.soilrespiration.common;

import com.example.soilrespiration.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  基本请求体封装类，将原始数据封装成JSON字符串
 */
public class CommonRequest {

    // 选中的task的id存放处
    private ArrayList<Integer> taskid;
    // 请求上传参数
    private ArrayList<HashMap<String, String>> requestTask;
    private ArrayList<HashMap<String, String>> requestSensor;

    public CommonRequest(){
        taskid = new ArrayList<>();
        requestTask = new ArrayList<>();
        requestSensor = new ArrayList<>();
    }

    public void addTaskid(int id){
        taskid.add(id);
    }

    public ArrayList getIdList(){
        return taskid;
    }

    /**
     * 为请求报文设置参数
     */
    public void addRequestTask(HashMap<String, String> taskmap){
        requestTask.add(taskmap);
    }

    public void addRequestSensor(HashMap<String, String> sensormap){
        requestSensor.add(sensormap);
    }

    /**
     *  将请求报文体组装成json形式的字符串，以便进行网络发送
     * @return 请求报文的json字符串
     */
    public String getJsonStr(){
        JSONObject object = new JSONObject();
        JSONArray taskArray = new JSONArray();
        JSONArray sensorArray = new JSONArray();
        int i = 0;
        int j = 0;
        try {
            // "requestTask"和"requestSensor"是和服务器约定好的请求体字段名称
            for (HashMap<String, String> subT : requestTask){
                JSONObject TObject = new JSONObject();
                TObject.put("flux", subT.get("flux"));
                TObject.put("time", subT.get("time"));
                TObject.put("SensorCount", subT.get("SensorCount"));
                taskArray.put(i++, TObject);
            }
            for (HashMap<String, String> subS : requestSensor){
                JSONObject SObject = new JSONObject();
                SObject.put("time", subS.get("time"));
                SObject.put("address", subS.get("address"));
                SObject.put("temperature", subS.get("temperature"));
                SObject.put("humidity", subS.get("humidity"));
                SObject.put("carbon", subS.get("carbon"));
                SObject.put("pressure", subS.get("pressure"));
                //SObject.put("task_id", subS.get("task_id"));
                sensorArray.put(j++, SObject);
            }
            object.put("requestTask", taskArray);
            object.put("requestSensor", sensorArray);
        }catch (JSONException e){
            LogUtil.logErr("请求报文组装异常：" + e.getMessage());
        }
        // 打印原始请求报文
        LogUtil.logRequest(object.toString());
        return object.toString();
    }
}
