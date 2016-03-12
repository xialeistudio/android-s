package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.Department;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.model.UserDepartment;
import com.ddhigh.overtime.util.AppUtil;
import com.ddhigh.overtime.util.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
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
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_overtime_view)
public class OvertimeViewActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    @ViewInject(R.id.scrollOvertimeView)
    private
    PullToRefreshScrollView scrollView;
    @ViewInject(R.id.imageAvatar)
    private
    ImageView imageAvatar;
    @ViewInject(R.id.txtRealname)
    private
    TextView txtRealname;
    @ViewInject(R.id.txtPhone)
    private
    TextView txtPhone;
    @ViewInject(R.id.layoutDepartments)
    private
    LinearLayout layoutDepartments;

    @ViewInject(R.id.imageCall)
    ImageView imageCall;
    @ViewInject(R.id.imageSms)
    ImageView imageSms;

    @ViewInject(R.id.txtBeginAt)
    private
    TextView txtBeginAt;
    @ViewInject(R.id.txtEndAt)
    private
    TextView txtEndAt;
    @ViewInject(R.id.txtStatus)
    private
    TextView txtStatus;
    @ViewInject(R.id.txtContent)
    private
    TextView txtContent;
    @ViewInject(R.id.txtCreatedAt)
    private
    TextView txtCreatedAt;

    @ViewInject(R.id.btnAccept)
    Button btnAccept;
    @ViewInject(R.id.btnReject)
    Button btnReject;

    Overtime overtime = new Overtime();
    private User user = new User();
    private final List<Department> departments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        loadOverTime();
        showActionBar();


        imageCall.setVisibility(View.GONE);
        imageSms.setVisibility(View.GONE);
        scrollView.setOnRefreshListener(this);

        btnAccept.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
    }

    /**
     * 加载结果回调
     */
    void onOvertimeLoaded() {
        if (overtime != null) {
            txtBeginAt.setText(overtime.getBegin_at());
            txtEndAt.setText(overtime.getEnd_at());
            txtStatus.setText(AppUtil.getStatusText(overtime.getStatus()));
            txtStatus.setBackgroundDrawable(AppUtil.getStatusBackground(this, overtime.getStatus()));
            txtContent.setText(overtime.getContent());
            txtCreatedAt.setText(DateUtil.format(new Date((long) overtime.getCreated_at() * 1000), "yyyy-MM-dd HH:mm"));
        }
        //检测用户数据和部门数据
        if (user.getUser_id() != 0) {
            if (!TextUtils.isEmpty(user.getAvatar())) {
                ImageLoader.getInstance().displayImage(user.getAvatar() + "?imageView2/1/w/128", imageAvatar);
            }
            txtRealname.setText(user.getRealname());
            txtPhone.setText(user.getPhone());
        }
        if (departments.size() > 0) {
            layoutDepartments.removeAllViews();
            for (Department department : departments) {
                addDepartment(department.getName());
            }
        }
        invalidateOptionsMenu();
    }

    /**
     * 添加部门
     *
     * @param name 部门名称
     */
    private void addDepartment(String name) {
        TextView t = new TextView(this);
        t.setText(name);
        t.setTextAppearance(this, R.style.Label);
        int p = getResources().getDimensionPixelSize(R.dimen.label_padding);
        int m = getResources().getDimensionPixelSize(R.dimen.label_margin);
        t.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, m, 0);
        t.setLayoutParams(lp);
        t.setTextAppearance(this, R.style.LabelSmall);
        t.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_success));
        layoutDepartments.addView(t);
    }

    /**
     * 加载详情
     */
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
            if (overtime == null) {
                loadDataFromRemote(id);
            } else {
                //加载用户
                user = dbManager.findById(User.class, overtime.getUser_id());
                if (user == null) {
                    user = new User();
                }
                //加载部门
                loadUserDepartment();
                onOvertimeLoaded();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        //加载远程数据
        loadDataFromRemote(id);
    }

    /**
     * 加载用户部门
     */
    private void loadUserDepartment() {
        try {
            List<UserDepartment> list = dbManager.selector(UserDepartment.class).where("user_id", "=", application.getAccessToken().getUser_id()).findAll();
            if (list != null && list.size() > 0) {
                departments.clear();
                for (UserDepartment ud : list) {
                    Log.d("overtime-view", "ud: " + ud);
                    Department d = dbManager.findById(Department.class, ud.getDepartment_id());
                    if (d != null) {
                        departments.add(d);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
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
                        dbManager.saveOrUpdate(user);
                        Log.d("overtime-view", "decode user: " + user);
                        if (jsonUser.has("userDepartments")) {
                            departments.clear();
                            JSONArray jaUserDepartments = jsonUser.getJSONArray("userDepartments");
                            for (int i = 0; i < jaUserDepartments.length(); i++) {
                                JSONObject jUserDepartment = jaUserDepartments.getJSONObject(i);
                                UserDepartment ud = new UserDepartment();
                                ud.decode(jUserDepartment);
                                dbManager.saveOrUpdate(ud);
                                JSONObject department = jUserDepartment.getJSONObject("department");
                                Department d = new Department();
                                d.decode(department);
                                Log.d("overtime-view", "decode department: " + d);
                                departments.add(d);
                                dbManager.saveOrUpdate(d);
                            }
                        }
                    }
                    onOvertimeLoaded();
                } catch (JSONException | IllegalAccessException | DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(OvertimeViewActivity.this, true);
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    Log.e("overtime-audit", e.getMessage(), throwable);
                    Toast.makeText(OvertimeViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
                scrollView.onRefreshComplete();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (user != null && overtime != null && user.getUser_id() == overtime.getUser_id() && overtime.getStatus() == 0) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goHome();
                return true;
            case R.id.menuEdit:
                Intent intent = new Intent(this, OvertimeEditActivity.class);
                intent.putExtra("id", overtime.getId());
                startActivityForResult(intent, RequestCode.EDIT_OVERTIME);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goHome() {
        Intent intent = getIntent();
        if (intent.hasExtra("isPush")) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        finish();
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadOverTime();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.EDIT_OVERTIME:
            case RequestCode.LOGIN:
                if (resultCode == RESULT_OK) {
                    loadOverTime();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
