package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.constants.RequestCode;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;

public class OvertimeAuditActivity extends OvertimeViewActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageCall.setVisibility(View.VISIBLE);
        imageSms.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onOvertimeLoaded() {
        super.onOvertimeLoaded();
        if (overtime.getStatus() == 0) {
            btnAccept.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
        } else {
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
        }
    }

    @Event(R.id.btnAccept)
    private void onAccept(View view) {
        RequestParams params = new RequestParams();
        params.add("status", "1");

        sendHttpRequest(params);
    }

    private void sendHttpRequest(RequestParams params) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setMessage("操作中");
        dialog.setCancelable(false);
        HttpUtil.post("/overtime/audit?id=" + overtime.getId(), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    overtime.decode(response);
                    onOvertimeLoaded();
                    Toast.makeText(OvertimeAuditActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
                    dbManager.saveOrUpdate(overtime);
                } catch (JSONException | IllegalAccessException e) {
                    onFailure(statusCode, headers, e, response);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) {
                    User.loginRequired(OvertimeAuditActivity.this, true);
                    return;
                }
                try {
                    HttpUtil.handleError(errorResponse);
                } catch (AppBaseException e) {
                    Log.e("overtime-audit", e.getMessage(), throwable);
                    Toast.makeText(OvertimeAuditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Event(R.id.btnReject)
    private void onReject(View v) {
        final RequestParams params = new RequestParams();
        params.add("status", "2");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请输入拒绝理由");
        final EditText editText = new EditText(this);
        editText.setHint("如时间不符");
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String t = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(t)) {
                    dialog.dismiss();
                    params.add("reason", t);
                    sendHttpRequest(params);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
