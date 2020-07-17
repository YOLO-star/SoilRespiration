package com.example.soilrespiration.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.database.Sensor;
import com.example.soilrespiration.database.Task;

import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.lang.Math.pow;

public class Utility {

    private static int delta;

    private static int id = 1;

    public static int getDelta() {
        return delta;
    }

    public static void setDelta(int delta) {
        Utility.delta = delta;
    }

    /**
     * 解析和处理socket返回的传感器数据,并保存到数据库
     */
    public static boolean handleSensor(String response){  //response是从socket收集到的字符串
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

    public static void handleTask(){
        //1. 从Sensor中取出数据 pressure temperature Carbon 和sensor对象
        int idLast = DataSupport.findLast(Sensor.class).getId();
        int range = idLast - delta;

        List<Sensor> diffusionList = DataSupport.select("pressure","temperature").where("address = ?", "1")
                .order("id asc").offset(delta).limit(range/5).find(Sensor.class);
        List<Sensor> gradList4 = DataSupport.select("carbon").where("address = ? ", "4")
                .order("id asc").offset(delta).limit(range/5).find(Sensor.class);
        List<Sensor> gradList3 = DataSupport.select("carbon").where("address = ?", "3")
                .order("id asc").offset(delta).limit(range/5).find(Sensor.class);
        List<Sensor> linkList = DataSupport.offset(delta).limit(range).find(Sensor.class);
        delta += linkList.size();

        //2.1 计算平均扩散系数
        int i = 0;
        double diffSum = 0;
        double averageDiff = 0;
        double[] diffArray = new double[diffusionList.size()];
        for (Sensor sensor : diffusionList){
            diffArray[i++] = (437.5*pow(sensor.getTemperature()+273.15,1.5)*0.2392)/(sensor.getPressure()*100*40.2386*10000);
        }
        for (i = 0; i < diffArray.length; i++){
            diffSum += diffArray[i];
        }
        averageDiff = diffSum / diffusionList.size();

        //2.2 计算平均浓度梯度
        double carbon4;
        double carbon3;
        double gradEach = 0;
        double gradSum = 0;
        double averageGrad = 0;
        for (int j = 0; j < gradList4.size(); j++){
            carbon4 = gradList4.get(j).getCarbon();
            carbon3 = gradList3.get(j).getCarbon();
            gradEach = carbon4 - carbon3;
            gradSum += gradEach;
        }
        averageGrad = gradSum / gradList4.size();
        gradList4.clear();
        gradList3.clear();

        //2.3 计算通量
        double flux;
        flux = (averageDiff*averageGrad*1000)/(22.4*0.095);

        //3.存储数据到Task数据表
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        Task task = new Task();
        task.setFlux(flux);
        task.setTaskTime(simpleDateFormat.format(date));
        for (Sensor sensor : linkList){
            task.getSensorList().add(sensor);
        }
        task.setSensorCount(task.getSensorList().size());
        task.save();
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
        ShowAll temp = new ShowAll(id++, time, address, temperature, humidity, carbon, pressure);
        return temp;
    }
}
