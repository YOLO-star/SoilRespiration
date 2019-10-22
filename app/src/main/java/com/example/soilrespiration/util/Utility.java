package com.example.soilrespiration.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.database.Sensor;

public class Utility {
    /**
     * 解析和处理socket返回的传感器数据,并保存到数据库
     */
    public static boolean handleDatabase(String response){  //response是从socket收集到的字符串
        if (!TextUtils.isEmpty(response)){
            Sensor sensor = new Sensor();
            String process[] = response.split(",");
            sensor.setTime(process[0]);
            sensor.setAddress(ConvertUtil.convertToInt(process[1], 0));
            sensor.setTemperature(ConvertUtil.convertToDouble(process[2], 0));
            sensor.setHumidity(ConvertUtil.convertToDouble(process[3], 0));
            sensor.setCarbon(ConvertUtil.convertToDouble(process[4], 0));
            sensor.setPressure(ConvertUtil.convertToDouble(process[5], 0));
            sensor.save();
            return true;
        }
        return false;
    }

    /**
     * 对socket收集到的原始数据进行封装，给RecyclerView使用
     * @param response 从socket收集到的字符串
     * @return
     */
    public static ShowAll handleCollection(String response){
        String process[] = response.split(",");
        String time = process[0];
        int address = ConvertUtil.convertToInt(process[1], 0);
        Double temperature = ConvertUtil.convertToDouble(process[2], 0);
        Double humidity = ConvertUtil.convertToDouble(process[3], 0);
        Double carbon = ConvertUtil.convertToDouble(process[4], 0);
        Double pressure = ConvertUtil.convertToDouble(process[5], 0);
        ShowAll temp = new ShowAll(time, address, temperature, humidity, carbon, pressure);
        return temp;
    }
}
