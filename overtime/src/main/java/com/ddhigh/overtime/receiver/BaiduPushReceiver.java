package com.ddhigh.overtime.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.ddhigh.overtime.activity.MainActivity;
import com.ddhigh.overtime.activity.OvertimeAuditActivity;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BaiduPushReceiver extends PushMessageReceiver {
    public final static String TAG = "baidu-push";

    private static final int ACTION_NEW_OVERTIME = 1;
    private static final int ACTION_AUDIT_OVERTIME = 2;
    private static final int ACTION_EDIT_OVERTIME = 3;

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        int userId = User.getUserIdFromLocal(context.getApplicationContext());
        if (userId > 0 && !TextUtils.isEmpty(s2)) {
            Log.d(TAG, "onBind channel_id: " + s2 + ", " + userId);
            RequestParams params = new RequestParams();
            params.put("channel_id", s2);
            params.put("type", 3);
            HttpUtil.post("/app/push", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "set push success: " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "set push fail", throwable);
                }
            });
        }
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        if (!TextUtils.isEmpty(s2)) {
            try {
                JSONObject jsonObject = new JSONObject(s2);
                int action = 0;
                if (jsonObject.has("action")) {
                    action = jsonObject.getInt("action");
                }
                handleAction(action, jsonObject, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                handleAction(0, null, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Log.d(TAG, "new message: " + s2);
    }

    private void handleAction(int action, JSONObject jsonObject, Context context) throws JSONException {
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (action) {
            case ACTION_NEW_OVERTIME:
            case ACTION_EDIT_OVERTIME:
                i.setClass(context, OvertimeAuditActivity.class);
                int id = jsonObject.getInt("overtime_id");
                i.putExtra("id", id);
                i.putExtra("isPush", true);
                break;
            default:
                i.setClass(context, MainActivity.class);
                i.putExtra("isPush", true);
                break;
        }
        context.startActivity(i);
    }
}
