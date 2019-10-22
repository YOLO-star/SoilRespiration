package com.example.soilrespiration.wheelview;

import java.util.List;

public class WeekAdapter implements WheelAdapter {

    // The default max value
    public static final int DEFAULT_MAX_VALUE = 9;

    // The default min value
    private static final int DEFAULT_MIN_VALUE = 0;

    List<String>dateList;

    //Values
    private int minValue;
    private int maxValue;

    // format
    private String format;

    /**
     *  Constructor
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param dateList
     */
    public WeekAdapter(int minValue, int maxValue, List<String>dateList){
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.dateList = dateList;
    }

    @Override
    public String getItem(int index) {
        if (index >= 0 && index < getItemsCount()){
            String strMin = dateList.get(index);
            return strMin;
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue;
    }

    @Override
    public int getMaximumLength() {
        int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
        int maxLen = Integer.toString(max).length();
        if (minValue < 0){
            maxLen++;
        }
        return maxLen;
    }
}
