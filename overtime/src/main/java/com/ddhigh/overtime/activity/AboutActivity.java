package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.Config;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.util.AppUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @ViewInject(R.id.txtAppInfo)
    TextView txtAppInfo;
    @ViewInject(R.id.viewRedPointer)
    View redPointer;
    @ViewInject(R.id.txtVersion)
    TextView txtVersion;
    String appInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);


        PackageInfo packageInfo = AppUtil.getAppInfo(this);
        appInfo = getResources().getString(R.string.app_name) + " " + packageInfo.versionName;
        txtAppInfo.setText(appInfo);
        showActionBar();

        Intent i = getIntent();
        if (i.hasExtra("newVersion")) {
            String v = i.getStringExtra("newVersion");
            redPointer.setVisibility(View.VISIBLE);
            txtVersion.setText(v);
            txtVersion.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("list", "clicked " + position);
    }

    @Event(R.id.btnCheckUpdate)
    private void onUpdate(View v) {
        startActivity(new Intent(CheckUpdateActivity.ACTION));
    }

    @Event(R.id.btnFeedback)
    private void onFeedback(View v) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:" + Config.FEEEDBACK_EMAIL));
        i.putExtra(Intent.EXTRA_SUBJECT, appInfo + "反馈");
        String body = "###请勿删除 " + AppUtil.getSystemInfo() + " 请勿删除###";
        i.putExtra(Intent.EXTRA_TEXT, body);
        startActivityForResult(i, RequestCode.SEND_FEEDBACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.SEND_FEEDBACK:
                Log.d("feedback","result: "+resultCode);
                Toast.makeText(this, "谢谢您的反馈", Toast.LENGTH_SHORT).show();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
