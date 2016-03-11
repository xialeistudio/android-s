package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ddhigh.mylibrary.dialog.LoadingDialog;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.exception.AppBaseException;
import com.ddhigh.overtime.model.Overtime;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/11 0011
 */
@ContentView(R.layout.activity_overtime_cu)
public class OvertimeCreateActivity extends OvertimeFormBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.new_apply));
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

            HttpUtil.post("/overtime/create", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("overtime-form", "create: " + response.toString());
                    Overtime overtime = new Overtime();
                    try {
                        overtime.decode(response);
                        Intent intent = new Intent(OvertimeCreateActivity.this, OvertimeViewActivity.class);
                        intent.putExtra("id", overtime.getId());
                        startActivity(intent);
                        finish();
                    } catch (JSONException | IllegalAccessException e) {
                        onFailure(statusCode, headers, e, response);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    try {
                        HttpUtil.handleError(errorResponse);
                    } catch (AppBaseException e) {
                        Log.e("overtime-form", e.getMessage(), throwable);
                        Toast.makeText(OvertimeCreateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
