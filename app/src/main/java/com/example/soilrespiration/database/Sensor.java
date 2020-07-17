package com.example.soilrespiration.database;

import org.litepal.crud.DataSupport;

public class Sensor extends DataSupport {

    private int id;
    private String time;
    private int address;
    private double temperature;
    private double humidity;
    private double carbon;
    private double pressure;
    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public int getAddress(){
        return address;
    }

    public void setAddress(int address){
        this.address = address;
    }

    public double getTemperature(){
        return temperature;
    }

    public void setTemperature(double temperature){
        this.temperature = temperature;
    }

    public double getHumidity(){
        return humidity;
    }

    public void setHumidity(double humidity){
        this.humidity = humidity;
    }

    public double getCarbon(){
        return carbon;
    }

    public void setCarbon(double carbon){
        this.carbon = carbon;
    }

    public double getPressure(){
        return pressure;
    }

    public void setPressure(double pressure){
        this.pressure = pressure;
    }
}
