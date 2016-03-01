package com.ddhigh.dodo.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.util.BitmapUtil;
import com.ddhigh.dodo.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

    @Event(R.id.btnSave)
    private void onBtnSaveClicked(View view) {
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
            application.user.async(new Callback.CommonCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (result.has("error")) {
                        try {
                            JSONObject error = result.getJSONObject("error");
                            showToast(error.getString("message"));
                        } catch (JSONException e) {
                            onError(e, true);
                        }
                    } else {
                        try {
                            application.user.parse(result);
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sp.edit();
                            //写入本地存储
                            editor.putString(User.PREF_USER, result.toString());
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                private void showToast(String message) {
                    Toast.makeText(ModifyNicknameActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                    showToast("保存失败");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    loadingDialog.dismiss();
                    finish();
                }
            });
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
