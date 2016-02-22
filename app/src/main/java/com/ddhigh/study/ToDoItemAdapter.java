package com.ddhigh.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @project Study
 * @package com.ddhigh.study
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {
    int resource;
    public ToDoItemAdapter(Context context, int resource, List<ToDoItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout todoView;
        ToDoItem item = getItem(position);
        String taskString = item.getTask();
        Date createdDate = item.getCreated();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        String dateStr = simpleDateFormat.format(createdDate);

        if(convertView == null){
            todoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource,todoView,true);
        }else{
            todoView = (LinearLayout)convertView;
        }
        TextView dateView = (TextView)todoView.findViewById(R.id.rowDate);
        TextView taskView=(TextView)todoView.findViewById(R.id.row);

        dateView.setText(dateStr);
        taskView.setText(taskString);
        return todoView;
    }
}
