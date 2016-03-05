package com.ddhigh.joke.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.joke.user
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
@ContentView(R.layout.activity_user_edit)
public class EditActivity extends AppCompatActivity {
    MyApplication application;
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    TextView txtNickname;
    @ViewInject(R.id.txtEmail)
    TextView txtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        application = (MyApplication) getApplication();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //显示用户信息
        if (!TextUtils.isEmpty(application.user.getAvatar())) {
            imageLoader.displayImage(application.user.getAvatar(), imageAvatar);
        }
        if (!TextUtils.isEmpty(application.user.getNickname())) {
            txtNickname.setText(application.user.getNickname());
        }
        if (!TextUtils.isEmpty(application.user.getEmail())) {
            txtEmail.setText(application.user.getEmail());
        }
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
