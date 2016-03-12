package com.ddhigh.overtime.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.util.AppUtil;

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
            viewHolder.txtId = (TextView) convertView.findViewById(R.id.txtId);
            viewHolder.txtBeginAt = (TextView) convertView.findViewById(R.id.txtBeginAt);
            viewHolder.txtEndAt = (TextView) convertView.findViewById(R.id.txtEndAt);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Overtime overtime = overtimes.get(position);
        viewHolder.txtId.setText(String.valueOf(overtime.getId()));
        viewHolder.txtBeginAt.setText(overtime.getBegin_at());
        viewHolder.txtEndAt.setText(overtime.getEnd_at());
        viewHolder.txtStatus.setText(AppUtil.getStatusText(overtime.getStatus()));
        viewHolder.txtStatus.setTextColor(mContext.getResources().getColor(AppUtil.getStatusColor(overtime.getStatus())));
        viewHolder.txtContent.setText(overtime.getContent());
        return convertView;
    }


    private static class ViewHolder {
        TextView txtId;
        TextView txtBeginAt;
        TextView txtEndAt;
        TextView txtStatus;
        TextView txtContent;

    }
}
