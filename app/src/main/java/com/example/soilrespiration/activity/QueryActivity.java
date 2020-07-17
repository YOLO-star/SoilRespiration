package com.example.soilrespiration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.QueryAdapter;
import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.database.Sensor;
import com.example.soilrespiration.database.Task;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class QueryActivity extends Activity {

    private List<ShowAll> showAllList = new ArrayList<>();
    private List<Sensor> sensorList;
    private int figure;
    QueryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.collect_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QueryAdapter(showAllList);
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        figure = intent.getIntExtra("id_card", 1);

        Task task  = DataSupport.find(Task.class, figure);
        sensorList = task.getSensors();

        for (Sensor sensor : sensorList){
            ShowAll temp = new ShowAll(sensor.getId(),sensor.getTime(), sensor.getAddress(), sensor.getTemperature(), sensor.getHumidity(), sensor.getCarbon(), sensor.getPressure());
            adapter.addData(temp);
        }
    }

}
