package com.example.soilrespiration.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.TaskAdapter;
import com.example.soilrespiration.database.Task;

import org.litepal.crud.DataSupport;

import java.util.List;


public class SuperQuery extends AppCompatActivity {

    private List<Task> taskList = DataSupport.findAll(Task.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_query);
        Toolbar superTool = (Toolbar) findViewById(R.id.super_tool);
        setSupportActionBar(superTool);
        TaskAdapter adapter = new TaskAdapter(SuperQuery.this, R.layout.task_item, taskList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskList.get(position);
                int identity = task.getId();
                Intent intent = new Intent(SuperQuery.this, QueryActivity.class);
                intent.putExtra("id_card", identity);
                startActivity(intent);
            }
        });
    }
}
