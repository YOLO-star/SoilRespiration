package com.example.soilrespiration.wheelview;

import java.text.SimpleDateFormat;

public class JudgeDate {

    public static boolean isDate(String str_input, String rDateFormat){
        if (!isNull(str_input)){
            SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
            formatter.setLenient(false);
            try {
                formatter.format(formatter.parse(str_input));  //Formats the given Date into a date/time string and appends the result to the given
            }catch (Exception e){
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isNull(String str){
        if (str == null)
            return true;
        else
            return false;
    }
}
