package com.example.soilrespiration.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.QueryAdapter;
import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.database.Sensor;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueryActivity extends Activity {

    private List<ShowAll> showAllList = new ArrayList<>();
    private List<Sensor> sensorList;
    QueryAdapter adapter;
    @BindView(R.id.queryBtn)
    Button queryAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.collect_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QueryAdapter(showAllList);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.queryBtn)
    public void onViewClicked(){
        querySaved();
    }

    private void querySaved(){
        sensorList = DataSupport.findAll(Sensor.class);
        for (Sensor sensor : sensorList){
            ShowAll temp = new ShowAll(sensor.getTime(), sensor.getAddress(), sensor.getTemperature(), sensor.getHumidity(), sensor.getCarbon(), sensor.getPressure());
            adapter.addData(temp);
        }
    }
}
