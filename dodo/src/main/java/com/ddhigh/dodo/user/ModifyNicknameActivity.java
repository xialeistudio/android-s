package com.ddhigh.dodo.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.widget.LoadingDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * @project Study
 * @package com.ddhigh.dodo.user
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_modify_nickname)
public class ModifyNicknameActivity extends AppCompatActivity {
    @ViewInject(R.id.txtNickname)
    EditText txtNickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        final MyApplication application = (MyApplication) getApplication();
        txtNickname.setText(application.user.getNickname());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_form, menu);
        return true;
    }

    private void onBtnSaveClicked() {
        String nickname = txtNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            return;
        }
        final MyApplication application = (MyApplication) getApplication();
        if (application.user.getNickname().equals(nickname)) {
            finish();
        } else {
            //同步至远程
            application.user.setNickname(nickname);
            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.setTitle("保存中");
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            try {
                application.user.async(ModifyNicknameActivity.this,new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        User.saveToLocal(getApplicationContext(), ModifyNicknameActivity.this, response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        throwable.printStackTrace();
                        Log.d(MyApplication.TAG, "update nickname: " + errorResponse.toString());
                        Toast.makeText(ModifyNicknameActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        loadingDialog.dismiss();
                        finish();
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
                loadingDialog.dismiss();
                Toast.makeText(ModifyNicknameActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuSave:
                onBtnSaveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
