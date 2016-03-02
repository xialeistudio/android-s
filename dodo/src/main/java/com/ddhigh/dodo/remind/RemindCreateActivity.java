package com.ddhigh.dodo.remind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.remind
 * @user xialeistudio
 * @date 2016/3/2 0002
 */
@ContentView(R.layout.activity_create_remind)
public class RemindCreateActivity extends AppCompatActivity {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_form, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuSave:
                Log.d(MyApplication.TAG,"RemindCreateActivity save clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
