package com.example.soilrespiration.wheelview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";

    public static String currentMonth(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  // 格式化对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return sdf.format(calendar.getTime());
    }

    public static String formateStringH(String dateStr, String pattren) {
        Date date = parseDate(dateStr, pattren);
        try {
            String str = dateToString(date, DateUtils.yyyyMMddHHmm);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    public static Date parseDate(String dateStr, String type) {
        SimpleDateFormat df = new SimpleDateFormat(type);
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String pattern)
            throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }
}
