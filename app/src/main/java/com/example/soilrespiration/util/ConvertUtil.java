package com.example.soilrespiration.util;

import android.text.TextUtils;

import java.text.DecimalFormat;

public class ConvertUtil {
    //把String转化为double
    public static double convertToDouble(String number, double defaultValue){
        if (TextUtils.isEmpty(number)){
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        }catch (Exception e){
            return defaultValue;
        }
    }

    //把String转化为int
    public static int convertToInt(String number, int defaultValue){
        if (TextUtils.isEmpty(number)){
            return defaultValue;
        }
        try {
            return Integer.parseInt(number);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public static String getDoubleNum(double num){
        DecimalFormat format = new DecimalFormat("###.00");
        return format.format(num);
    }
}
