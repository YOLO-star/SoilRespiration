package com.example.soilrespiration.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soilrespiration.R;
import com.example.soilrespiration.common.CommonRequest;
import com.example.soilrespiration.common.CommonResponse;
import com.example.soilrespiration.common.ResponseHandler;
import com.example.soilrespiration.database.Sensor;
import com.example.soilrespiration.database.Task;
import com.example.soilrespiration.util.DialogUtil;
import com.example.soilrespiration.util.LoadingDialogUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.soilrespiration.util.ConvertUtil.getDoubleNum;

public class UploadActivity extends BaseActivity {

    private CheckListViewAdapter mAdapter;
    private List<Task> uploadItem = new ArrayList<>();
    private Button uploadBtn;
    private ArrayList<Integer> taskList = new ArrayList<>();
    //private ArrayList<String> sensorList = new ArrayList<>();
    private String URL_UPLOAD = "http://192.168.1.106:8080/MyService/cloudServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ListView listView = (ListView) findViewById(R.id.upload_list);
        mAdapter = new CheckListViewAdapter(this, R.layout.item_with_check_box, uploadItem);
        listView.setAdapter(mAdapter);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener(listView));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UploadActivity.this, "请长按", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        uploadItem.addAll(getUploadItem());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uploadItem.clear();
    }

    private List<Task> getUploadItem(){
        List<Task> uploadItems = new ArrayList<>();
        for (int i = 1; i <= DataSupport.count(Task.class); i++){
            uploadItems.add(DataSupport.find(Task.class, i));
        }
        return uploadItems;
    }

    private class MultiChoiceModeListener implements AbsListView.MultiChoiceModeListener{

        private ListView mListView;
        private TextView mTitleTextView;
        private List<Task> mSelectedItems = new ArrayList<>();

        private MultiChoiceModeListener(ListView listView){
            mListView = listView;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mSelectedItems.add(mAdapter.getItem(position));
            mTitleTextView.setText("已选择"+mListView.getCheckedItemCount() + "项");
            //Log.d("UploadActivity: ", "已选择"+mListView.getCheckedItemCount() + "项");
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.check_task_priority, menu);
            @SuppressLint("InflateParams")
                    View multiSelectActionBarView = LayoutInflater.from(UploadActivity.this).inflate(R.layout.action_mode_bar, null);
            mode.setCustomView(multiSelectActionBarView);
            mTitleTextView = (TextView) multiSelectActionBarView.findViewById(R.id.upload_title);
            mTitleTextView.setText("已选择0项");

            mAdapter.setCheckable(true);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.cancel:
                    break;
                case R.id.upload:
                    upload(mSelectedItems);
                    break;
                    default:
                        break;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSelectedItems.clear();
            mAdapter.setCheckable(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void upload(List<Task> list){
        final CommonRequest request = new CommonRequest();
        for (Task task : list){
            taskList.add(task.getId());
            HashMap<String, String> data = new HashMap<>();
            data.put("flux", Double.toString(task.getFlux()));
            data.put("time", task.getTaskTime());
            data.put("SensorCount", Integer.toString(task.getSensorCount()));
            request.addRequestTask(data);
            Task taskRich  = DataSupport.find(Task.class, task.getId(), true);
            for (Sensor sensor : taskRich.getSensorList()){
                HashMap<String, String> deta = new HashMap<>();
                deta.put("time", sensor.getTime());
                deta.put("address", Integer.toString(sensor.getAddress()));
                deta.put("temperature", Double.toString(sensor.getTemperature()));
                deta.put("humidity", Double.toString(sensor.getHumidity()));
                deta.put("carbon", Double.toString(sensor.getCarbon()));
                deta.put("pressure", Double.toString(sensor.getPressure()));
                //deta.put("task_id", Integer.toString(sensor.getTask().getId()));
                request.addRequestSensor(deta);
            }
        }
        sendHttpPostRequest(URL_UPLOAD, request, new ResponseHandler() {
            @Override
            public void success(CommonResponse response) {
                // 删除Task表和Sensor表的对应数据
                for (int i = 0; i < taskList.size(); i++){
                    DataSupport.delete(Task.class, taskList.get(i));
                }
                taskList.clear();
                // 重置Task表和Sensor表的主键值
                if (0 == DataSupport.count(Task.class)){
                    ContentValues values = new ContentValues();
                    values.put("seq", "0");
                    DataSupport.updateAll("sqlite_sequence", values, "name = ?", "task");  // 重置task表的主键值为0
                    DataSupport.updateAll("sqlite_sequence", values, "name = ?", "sensor"); // 重置sensor表的主键值为0
                }else {
                    int taskid = DataSupport.findLast(Task.class).getId();
                    ContentValues tvalues = new ContentValues();
                    tvalues.put("seq", Integer.toString(taskid));
                    DataSupport.updateAll("sqlite_sequence", tvalues, "name = ?", "task");  // 重置task表的主键值
                    List<Sensor> listSensor = DataSupport.find(Task.class, taskid).getSensors(); // 该task对应的sensor链表
                    Sensor idEnd = listSensor.get(listSensor.size()-1); // sensor链表中最后一个元素
                    int idSensor =idEnd.getId(); // 该最后一个元素在Sensor表中对应的主键id
                    ContentValues svalues = new ContentValues();
                    svalues.put("seq", Integer.toString(idSensor));
                    DataSupport.updateAll("sqlite_sequence", svalues, "name = ?", "sensor"); // 重置sensor表的主键值
                }
                LoadingDialogUtil.cancelLoading();
                DialogUtil.showHintDialog(UploadActivity.this, response.getResCode() + ":" + response.getResMsg(), false);
            }

            @Override
            public void fail(String failCode, String failMsg) {
                DialogUtil.showHintDialog(UploadActivity.this, true, "数据上传失败", failCode + " : " + failMsg, "关闭对话框", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadingDialogUtil.cancelLoading();
                        DialogUtil.dismissDialog();
                    }
                });
            }
        }, true);
    }

    private class CheckListViewAdapter extends ArrayAdapter<Task>{

        private boolean mCheckable;

        private CheckListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects){
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = getItem(position);  //获取当前项的Task实例
            ViewHolder holder;
            if (null == convertView){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_with_check_box, parent, false);
                holder = new ViewHolder();
                holder.uploadItem_id = (TextView) convertView.findViewById(R.id.upload_task);
                holder.uploadItem_flux = (TextView) convertView.findViewById(R.id.upload_flux);
                holder.uploadItem_time = (TextView) convertView.findViewById(R.id.upload_time);
                holder.uploadItem_sensors = (TextView) convertView.findViewById(R.id.upload_sensor);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.selected_check_box);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (task != null){
                holder.uploadItem_id.setText(Integer.toString(task.getId()));
                holder.uploadItem_flux.setText(getDoubleNum(task.getFlux()));
                holder.uploadItem_time.setText(task.getTaskTime());
                holder.uploadItem_sensors.setText(Integer.toString(task.getSensorCount()));
            }

            //可见性和选中状态
            if (mCheckable){
                holder.checkBox.setVisibility(View.VISIBLE);
            }else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
            holder.checkBox.setChecked(((ListView)parent).isItemChecked(position));

            return convertView;
        }

        private void setCheckable(boolean checkable){
            mCheckable = checkable;
        }

        private class ViewHolder{
            private TextView uploadItem_id;
            private TextView uploadItem_flux;
            private TextView uploadItem_time;
            private TextView uploadItem_sensors;
            private CheckBox checkBox;
        }
    }
}
