package com.ddhigh.overtime.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddhigh.overtime.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
