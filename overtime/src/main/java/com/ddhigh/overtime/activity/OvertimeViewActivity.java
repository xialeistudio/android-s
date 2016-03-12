package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Department;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class OvertimeViewActivity extends BaseActivity {
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
        TextView textView = new TextView(this);
        textView.setText(overtime.toString());
        setContentView(textView);
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
            onOvertimeLoaded();
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
}
