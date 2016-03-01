package com.ddhigh.dodo.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ddhigh.dodo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.user
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
