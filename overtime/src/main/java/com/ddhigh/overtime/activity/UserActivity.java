package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.model.Role;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.model.UserRole;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
@ContentView(R.layout.activity_user)
public class UserActivity extends BaseActivity {
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtRealname)
    TextView txtRealname;
    @ViewInject(R.id.txtPhone)
    TextView txtPhone;
    @ViewInject(R.id.txtTotalTime)
    TextView txtTotalTime;

    @ViewInject(R.id.txtRole)
    TextView txtRole;
    @ViewInject(R.id.txtCreatedAt)
    TextView txtCreatedAt;

    @ViewInject(R.id.btnSetting)
    RelativeLayout btnSetting;
    @ViewInject(R.id.btnAbout)
    RelativeLayout btnAbout;
    @ViewInject(R.id.btnLogout)
    RelativeLayout btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        showActionBar();

        init();
    }

    private void init() {
        if (!TextUtils.isEmpty(application.getUser().getAvatar())) {
            ImageLoader.getInstance().displayImage(application.getUser().getAvatar() + "?imageView2/1/w/128", imageAvatar);
        }
        txtRealname.setText(application.getUser().getRealname());
        txtPhone.setText(application.getUser().getPhone());
        txtTotalTime.setText(((float) application.getUser().getTotal_time() / 60) + "小时");
        txtCreatedAt.setText(DateUtil.format(new Date((long) application.getUser().getCreated_at() * 1000), "yyyy-MM-dd") + "加入");
        try {
            UserRole userRole = dbManager.selector(UserRole.class).where("user_id", "=", application.getUser().getUser_id()).findFirst();
            if (userRole != null) {
                txtRole.setVisibility(View.VISIBLE);
                txtRole.setText(userRole.getRole_id() == Role.ROLE_DIRECTOR ? "主管" : "员工");
            } else {
                txtRole.setVisibility(View.GONE);
            }
        } catch (DbException e) {
            e.printStackTrace();
            txtRole.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(R.id.btnLogout)
    private void onLogout(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("确定退出当前账号?");
        b.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                User.loginRequired(UserActivity.this, false);
                finish();
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
