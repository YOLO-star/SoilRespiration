package com.example.soilrespiration.database;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Task extends DataSupport {

    private int id;
    private Double flux;
    private String taskTime;
    private int sensorCount;
    private List<Sensor> sensorList = new ArrayList<Sensor>();

    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public List<Sensor> getSensors(){
        return DataSupport.where("task_id = ?", String.valueOf(id)).find(Sensor.class);
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Double getFlux(){
        return flux;
    }

    public void setFlux(Double flux){
        this.flux = flux;
    }

    public String getTaskTime(){
        return taskTime;
    }

    public void setTaskTime(String taskTime){
        this.taskTime = taskTime;
    }
}
