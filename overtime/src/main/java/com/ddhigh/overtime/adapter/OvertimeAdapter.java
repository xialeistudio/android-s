package com.ddhigh.overtime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.util.AppUtil;

import java.util.Date;
import java.util.List;

public class OvertimeAdapter extends BaseAdapter {
    private Context mContext;
    private List<Overtime> overtimes;

    public OvertimeAdapter(Context mContext, List<Overtime> overtimes) {
        this.mContext = mContext;
        this.overtimes = overtimes;
    }

    @Override
    public int getCount() {
        return overtimes.size();
    }

    @Override
    public Object getItem(int position) {
        return overtimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return overtimes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_overtime, null);
            viewHolder = new ViewHolder();
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
            viewHolder.txtCreatedAt = (TextView) convertView.findViewById(R.id.txtCreatedAt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Overtime overtime = overtimes.get(position);
        Date dd = new Date((long) overtime.getCreated_at() * 1000);
        String d = DateUtil.format(dd, "MM-dd HH:mm");
        viewHolder.txtCreatedAt.setText(d);
        viewHolder.txtStatus.setText(AppUtil.getStatusText(overtime.getStatus()));
        viewHolder.txtStatus.setBackground(AppUtil.getStatusBackground(mContext,overtime.getStatus()));
        viewHolder.txtContent.setText(overtime.getContent());
        return convertView;
    }


    private static class ViewHolder {
        TextView txtCreatedAt;
        TextView txtStatus;
        TextView txtContent;

    }
}
