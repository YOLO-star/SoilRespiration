package com.example.soilrespiration.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.QueryAdapter;
import com.example.soilrespiration.common.ShowAll;
import com.example.soilrespiration.database.Sensor;
import com.example.soilrespiration.wheelview.DateUtils;
import com.example.soilrespiration.wheelview.JudgeDate;
import com.example.soilrespiration.wheelview.ScreenInfo;
import com.example.soilrespiration.wheelview.WheelWeekMain;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QueryActivity extends Activity implements View.OnClickListener {

    private List<ShowAll> showAllList = new ArrayList<>();
    private List<Sensor> sensorList;
    QueryAdapter adapter;
    private TextView tv_start_week_house_time;
    private TextView tv_end_week_house_time;
    private TextView tv_center;
    private Button query_all;
    private WheelWeekMain wheelWeekMainDate;
    private String beginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        initView();
        initEvent();
        /* ButterKnife.bind(this);
         RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QueryAdapter(showAllList);
        recyclerView.setAdapter(adapter); */
    }

    private void initView(){
        tv_start_week_house_time = (TextView)findViewById(R.id.tv_start_week_house_time);
        tv_end_week_house_time = (TextView)findViewById(R.id.tv_end_week_house_time);
        tv_center = (TextView)findViewById(R.id.tv_center);
        query_all = (Button)findViewById(R.id.query_all);
    }

    private void initEvent(){
        tv_start_week_house_time.setOnClickListener(this);
        tv_end_week_house_time.setOnClickListener(this);
        query_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start_week_house_time:
                showWeekBottoPopupWindow();
                break;
            case R.id.tv_end_week_house_time:
                showWeekBottoPopupWindow();
                break;
            case R.id.query_all:
                break;
        }
    }

    public void showWeekBottoPopupWindow(){
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_week_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int)(width*0.8), ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelWeekMainDate = new WheelWeekMain(menuView, true);
        wheelWeekMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")){
            try {
                calendar.setTime(new Date(time));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelWeekMainDate.initDateTimePicker(year, month, day, hours, minute);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(tv_center, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView)menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择设置时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginTime = wheelWeekMainDate.getTime().toString();
                tv_start_week_house_time.setText(DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm));
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    /* public void onViewClicked(){
        querySaved();
    }

       private void querySaved(){
        sensorList = DataSupport.findAll(Sensor.class);
        for (Sensor sensor : sensorList){
            ShowAll temp = new ShowAll(sensor.getTime(), sensor.getAddress(), sensor.getTemperature(), sensor.getHumidity(), sensor.getCarbon(), sensor.getPressure());
            adapter.addData(temp);
        }
    } */
}
