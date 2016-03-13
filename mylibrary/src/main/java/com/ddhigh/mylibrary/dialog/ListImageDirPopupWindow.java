package com.ddhigh.mylibrary.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ddhigh.mylibrary.R;
import com.ddhigh.mylibrary.bean.FolderBean;
import com.ddhigh.mylibrary.util.LocalImageLoader;

import java.util.List;

public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;

    private List<FolderBean> mDatas;

    private OnDirSelectListener mListener;

    public void setOnDirSelectedListener(OnDirSelectListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDirSelectListener {
        void onSelected(FolderBean bean);
    }

    public ListImageDirPopupWindow(Context context, List<FolderBean> mDatas) {
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.imp_popup_main, null);
        this.mDatas = mDatas;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews(context);
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onSelected(mDatas.get(position));
                }
            }
        });
    }

    private void initViews(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context, mDatas));
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean> {
        private LayoutInflater mInflater;
        private List<FolderBean> mDatas;


        public ListDirAdapter(Context context, List<FolderBean> objects) {
            super(context, 0, objects);
            mInflater = LayoutInflater.from(context);
            mDatas = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.imp_item_popup_main, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FolderBean bean = mDatas.get(position);
            //重置
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);
            LocalImageLoader.getInstance().loadImage(bean.getFirstImagePath(), viewHolder.mImg);
            viewHolder.mDirName.setText(bean.getName());
            viewHolder.mDirCount.setText(bean.getCount() + "");
            return convertView;
        }

        private class ViewHolder {
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
        }
    }

    /**
     * 计算popup宽度高度
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.7);
    }
}
