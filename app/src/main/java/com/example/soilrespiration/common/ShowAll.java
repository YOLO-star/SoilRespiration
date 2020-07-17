package com.example.soilrespiration.common;

public class ShowAll {

    private int id;
    private String time;
    private int address;
    private double temperature;
    private double humidity;
    private double carbon;
    private double pressure;

    public ShowAll(int id,String time, int address, double temperature, double humidity, double carbon, double pressure){
        this.id = id;
        this.time = time;
        this.address = address;
        this.temperature = temperature;
        this.humidity = humidity;
        this.carbon = carbon;
        this.pressure = pressure;
    }

    public int getId(){
        return id;
    }

    public String getTime(){
        return time;
    }

    public int getAddress(){
        return address;
    }

    public double getTemperature(){
        return temperature;
    }

    public double getHumidity(){
        return humidity;
    }

    public double getCarbon(){
        return carbon;
    }

    public double getPressure(){
        return pressure;
    }
}
