package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Department;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.AppUtil;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_overtime_view)
public class OvertimeViewActivity extends BaseActivity {

    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtRealname)
    TextView txtRealname;
    @ViewInject(R.id.txtPhone)
    TextView txtPhone;
    @ViewInject(R.id.layoutDepartments)
    LinearLayout layoutDepartments;

    @ViewInject(R.id.imageCall)
    ImageView imageCall;
    @ViewInject(R.id.imageSms)
    ImageView imageSms;


    @ViewInject(R.id.txtId)
    TextView txtId;
    @ViewInject(R.id.txtBeginAt)
    TextView txtBeginAt;
    @ViewInject(R.id.txtEndAt)
    TextView txtEndAt;
    @ViewInject(R.id.txtStatus)
    TextView txtStatus;
    @ViewInject(R.id.txtContent)
    TextView txtContent;


    Overtime overtime = new Overtime();
    User user = new User();
    List<Department> departments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        loadOverTime();
        showActionBar();
    }

    private void onOvertimeLoaded() {
        //overtime肯定有
        txtId.setText(String.valueOf(overtime.getId()));
        txtBeginAt.setText(overtime.getBegin_at());
        txtEndAt.setText(overtime.getEnd_at());
        txtStatus.setText(AppUtil.getStatusText(overtime.getStatus()));
        txtStatus.setTextColor(getResources().getColor(AppUtil.getStatusColor(overtime.getStatus())));
        txtContent.setText(overtime.getContent());
        //检测用户数据和部门数据
        if (user.getUser_id() != 0) {
            if (!TextUtils.isEmpty(user.getAvatar())) {
                ImageLoader.getInstance().displayImage(user.getAvatar() + "?imageView2/1/w/128", imageAvatar);
            }
            txtRealname.setText(user.getRealname());
            txtPhone.setText(user.getPhone());
        }
        if (departments.size() > 0) {
            for (Department department : departments) {
                TextView t = new TextView(this);
                t.setText(department.getName());
                t.setTextAppearance(this, R.style.Label);
                int p = getResources().getDimensionPixelSize(R.dimen.label_padding);
                int m = getResources().getDimensionPixelSize(R.dimen.label_margin);
                t.setPadding(p, p, p, p);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, m, 0);
                t.setLayoutParams(lp);
                t.setBackground(getResources().getDrawable(R.drawable.label_success));
                layoutDepartments.addView(t);
            }
        }
    }

    private void loadOverTime() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        if (id == 0) {
            finish();
            return;
        }
        //基础数据从本地加载
        try {
            overtime = dbManager.findById(Overtime.class, id);
            if (overtime != null) {
                onOvertimeLoaded();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        //加载远程数据
        loadDataFromRemote(id);
    }

    private void loadDataFromRemote(int id) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        HttpUtil.get("/overtime/view", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    overtime.decode(response);
                    dbManager.saveOrUpdate(overtime);
                    Log.d("overtime-view", "decode overtime: " + overtime);
                    if (response.has("user")) {
                        JSONObject jsonUser = response.getJSONObject("user");
                        user.decode(jsonUser);
                        Log.d("overtime-view", "decode user: " + user);
                        if (jsonUser.has("userDepartments")) {
                            JSONArray jaUserDepartments = jsonUser.getJSONArray("userDepartments");
                            for (int i = 0; i < jaUserDepartments.length(); i++) {
                                JSONObject jUserDepartment = jaUserDepartments.getJSONObject(i);
                                JSONObject department = jUserDepartment.getJSONObject("department");
                                Department d = new Department();
                                d.decode(department);
                                Log.d("overtime-view", "decode department: " + d);
                                departments.add(d);
                            }
                        }
                    }
                    onOvertimeLoaded();
                } catch (JSONException | IllegalAccessException | DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Event(R.id.imageCall)
    private void onCall(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        String msg = "是否拨打电话给" + user.getRealname() + "[" + user.getPhone() + "]？";
        b.setMessage(msg);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getPhone()));
                startActivity(i);
            }
        });
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.create().show();
    }

    @Event(R.id.imageSms)
    private void onSms(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        String msg = "是否发送短信给" + user.getRealname() + "[" + user.getPhone() + "]？";
        b.setMessage(msg);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + user.getPhone()));
                startActivity(i);
            }
        });
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.create().show();
    }
}
