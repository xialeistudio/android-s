package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Role;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/11 0011
 */
@ContentView(R.layout.activity_overtime_cu)
public abstract class OvertimeFormBaseActivity extends BaseActivity implements Spinner.OnItemSelectedListener {
    @ViewInject(R.id.txtBeginAt)
    EditText txtBeginAt;
    @ViewInject(R.id.txtEndAt)
    EditText txtEndAt;
    @ViewInject(R.id.txtContent)
    EditText txtContent;
    @ViewInject(R.id.spinnerDirector)
    Spinner spinnerDirector;
    @ViewInject(R.id.checkRealInfo)
    CheckBox checkRealInfo;
    @ViewInject(R.id.btnSubmit)
    Button btnSubmit;

    Date beginAt;
    Date endAt;

    String begin_at;
    String end_at;
    String content;
    int director_id;
    boolean isRealInfoChecked;

    List<String> directors = new ArrayList<>();
    List<User> users = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        showActionBar();
        txtBeginAt.setKeyListener(null);
        txtEndAt.setKeyListener(null);

        init(getIntent());
        //加载主管列表
        loadDirectors();
    }


    private void loadDirectors() {
        RequestParams params = new RequestParams();
        params.put("id", Role.ROLE_DIRECTOR);
        HttpUtil.get("/role/users", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        User u = new User();
                        u.decode(jsonObject);
                        users.add(u);
                        directors.add(u.getRealname());
                    } catch (JSONException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (users.size() == 0) {
                    Toast.makeText(OvertimeFormBaseActivity.this, "暂时没有主管，无法发起申请", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(false);
                    return;
                }

                adapter = new ArrayAdapter<>(OvertimeFormBaseActivity.this, android.R.layout.simple_spinner_item, directors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDirector.setAdapter(adapter);
                spinnerDirector.setOnItemSelectedListener(OvertimeFormBaseActivity.this);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(OvertimeFormBaseActivity.this, false);
                    finish();
                    return;
                }
                Log.e("overtime-form", "load directors fail", throwable);
                Toast.makeText(OvertimeFormBaseActivity.this, R.string.load_directors_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 有数据时初始化
     * @param intent 数据
     */
   abstract protected void init(Intent intent);


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
            case R.id.menuHelp:
                showHelpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getString(R.string.help))
                //TODO:帮助内容
                .setMessage("帮助内容")
                .setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        b.create().show();
    }

    int txtBeginAtFlag = 0;
    int txtEndAtFlag = 0;

    @Event(value = R.id.txtBeginAt, type = View.OnTouchListener.class)
    private boolean onBeginAtClicked(View view, MotionEvent event) {
        txtBeginAtFlag++;
        if (txtBeginAtFlag == 2) {
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(new SlideDateTimeListener() {
                        @Override
                        public void onDateTimeSet(Date date) {
                            String d = DateUtil.format(date, "yyyy-MM-dd HH:mm");
                            txtBeginAt.setText(d);
                            beginAt = date;
                        }
                    })
                    .setIs24HourTime(true)
                    .build()
                    .show();
            txtBeginAtFlag = 0;
        }
        return false;
    }

    @Event(value = R.id.txtEndAt, type = View.OnTouchListener.class)
    private boolean onEndAtClicked(View view, MotionEvent event) {
        txtEndAtFlag++;
        if (txtEndAtFlag == 2) {
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(new SlideDateTimeListener() {
                        @Override
                        public void onDateTimeSet(Date date) {
                            String d = DateUtil.format(date, "yyyy-MM-dd HH:mm");
                            txtEndAt.setText(d);
                            endAt = date;
                        }
                    })
                    .setIs24HourTime(true)
                    .build()
                    .show();
            txtEndAtFlag = 0;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        User u = users.get(position);
        Log.d("overtime-form", "select director: " + u);
        director_id = u.getUser_id();
    }

    /**
     * 检测表单是否合法
     */
    protected boolean isFormValidated() {
        begin_at = txtBeginAt.getText().toString().trim();
        end_at = txtEndAt.getText().toString().trim();
        content = txtContent.getText().toString().trim();
        isRealInfoChecked = checkRealInfo.isChecked();
        if (TextUtils.isEmpty(begin_at)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "开始时间"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(end_at)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "结束时间"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (beginAt.getTime() >= endAt.getTime()) {
            Toast.makeText(this, "开始时间必须小于结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.cannot_empty), "加班内容"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isRealInfoChecked) {
            Toast.makeText(this, "请确认所填信息真实无误", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
