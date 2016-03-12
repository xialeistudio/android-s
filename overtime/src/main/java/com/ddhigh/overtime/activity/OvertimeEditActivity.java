package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.mylibrary.util.DateUtil;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;

import java.text.ParseException;
import java.util.Date;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/12 0012
 */
public class OvertimeEditActivity extends OvertimeFormBaseActivity {
    private int id;
    Overtime overtime;

    @Override
    protected void init(Intent intent) {
        btnSubmit.setEnabled(false);
        if (intent.hasExtra("id")) {
            id = intent.getIntExtra("id", 0);
            if (id > 0) {
                Log.d("overtime-form", "edit init with: " + id);
                try {
                    overtime = dbManager.findById(Overtime.class, id);
                    onOvertimeLoaded();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (overtime == null) {
                    loadFromRemote();
                }
            }
        }
    }

    private void onOvertimeLoaded() {
        try {
            beginAt = DateUtil.parse(overtime.getBegin_at());
        } catch (ParseException e) {
            e.printStackTrace();
            beginAt = new Date();
        }
        try {
            endAt = DateUtil.parse(overtime.getEnd_at());
        } catch (ParseException e) {
            e.printStackTrace();
            endAt = new Date();
        }
        txtBeginAt.setText(overtime.getBegin_at());
        txtEndAt.setText(overtime.getEnd_at());
        txtContent.setText(overtime.getContent());
        btnSubmit.setEnabled(true);
    }

    private void loadFromRemote() {
        HttpUtil.get("/overtime/view?id=" + id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                overtime = new Overtime();
                try {
                    overtime.decode(response);
                    dbManager.saveOrUpdate(overtime);
                    onOvertimeLoaded();
                } catch (JSONException | IllegalAccessException e) {
                    onFailure(statusCode, headers, e, response);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(OvertimeEditActivity.this, false);
                    finish();
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    Log.e("overtime-form", e.getMessage(), throwable);
                    Toast.makeText(OvertimeEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Event(R.id.btnSubmit)
    private void onSubmit(View view) {
        Log.d("overtime-form", "btnSubmit clicked");
        if (isFormValidated()) {
            final LoadingDialog dialog = new LoadingDialog(this);
            dialog.setMessage(getString(R.string.applying));
            dialog.setCancelable(false);
            //组装数据
            RequestParams params = new RequestParams();
            params.put("begin_at", begin_at);
            params.put("end_at", end_at);
            params.put("content", content);
            params.put("director_id", director_id);

            HttpUtil.post("/overtime/edit?id=" + id, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("overtime-form", "edit: " + response.toString());
                    try {
                        overtime.decode(response);
                        dbManager.saveOrUpdate(overtime);
                        setResult(RESULT_OK);
                        Toast.makeText(OvertimeEditActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException | IllegalAccessException e) {
                        onFailure(statusCode, headers, e, response);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (statusCode == 401) {
                        User.loginRequired(OvertimeEditActivity.this, true);
                        return;
                    }
                    try {
                        HttpUtil.handleError(errorResponse);
                    } catch (AppBaseException e) {
                        Log.e("overtime-form", e.getMessage(), throwable);
                        Toast.makeText(OvertimeEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStart() {
                    dialog.show();
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.LOGIN:
                if (resultCode == RESULT_OK) {
                    init(getIntent());
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
