package com.example.soilrespiration.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soilrespiration.R;

import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private List<ShowAll> mShowAllList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView showId;
        TextView showTime;
        TextView showAddress;
        TextView showTemperature;
        TextView showHumidity;
        TextView showCarbon;
        TextView showPressure;

        public ViewHolder(View view){
            super(view);
            showId = (TextView) view.findViewById(R.id.show_id);
            showTime = (TextView) view.findViewById(R.id.show_time);
            showAddress = (TextView) view.findViewById(R.id.show_address);
            showTemperature = (TextView) view.findViewById(R.id.show_temperature);
            showHumidity = (TextView) view.findViewById(R.id.show_humidity);
            showCarbon = (TextView) view.findViewById(R.id.show_carbon);
            showPressure = (TextView) view.findViewById(R.id.show_pressure);
        }
    }

    public QueryAdapter(List<ShowAll> showAllList){
        mShowAllList = showAllList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.showall_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ShowAll showAll = mShowAllList.get(i);
        viewHolder.showId.setText(Integer.toString(showAll.getId()));
        viewHolder.showTime.setText(showAll.getTime());
        viewHolder.showAddress.setText(Integer.toString(showAll.getAddress()));
        viewHolder.showTemperature.setText(Double.toString(showAll.getTemperature()));
        viewHolder.showHumidity.setText(Double.toString(showAll.getHumidity()));
        viewHolder.showCarbon.setText(Double.toString(showAll.getCarbon()));
        viewHolder.showPressure.setText(Double.toString(showAll.getPressure()));
    }

    public void addData(ShowAll showAll){
        //在list中添加数据，并通知条目加入一条
        mShowAllList.add(showAll);
        notifyItemInserted(mShowAllList.size());
    }

    @Override
    public int getItemCount() {
        return mShowAllList.size();
    }
}
