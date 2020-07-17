package com.example.soilrespiration.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.soilrespiration.R;
import com.example.soilrespiration.database.Task;

import java.util.List;

import static com.example.soilrespiration.util.ConvertUtil.getDoubleNum;

public class TaskAdapter extends ArrayAdapter<Task> {

    private int resourceId;

    public TaskAdapter(Context context, int textViewResourceId, List<Task> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);  //获取当前项的Task实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskItem_id = (TextView) view.findViewById(R.id.taskItem_id);
            viewHolder.taskItem_flux = (TextView) view.findViewById(R.id.taskItem_flux);
            viewHolder.taskItem_time = (TextView) view.findViewById(R.id.taskItem_time);
            viewHolder.sensorList_num = (TextView) view.findViewById(R.id.sensorList_num);
            view.setTag(viewHolder);  //将ViewHolder存储在View中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();  //重新获取ViewHolder
        }
        viewHolder.taskItem_id.setText(Integer.toString(task.getId()));
        viewHolder.taskItem_flux.setText(getDoubleNum(task.getFlux()));
        viewHolder.taskItem_time.setText(task.getTaskTime());
        viewHolder.sensorList_num.setText(Integer.toString(task.getSensorCount()));
        return view;
    }

    class ViewHolder{
        TextView taskItem_id;
        TextView taskItem_flux;
        TextView taskItem_time;
        TextView sensorList_num;
    }
}
