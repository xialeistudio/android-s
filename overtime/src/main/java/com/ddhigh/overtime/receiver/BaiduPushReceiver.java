package com.ddhigh.overtime.receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.ddhigh.overtime.model.User;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class BaiduPushReceiver extends PushMessageReceiver {
    public final static String TAG = "baidu-push";

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        int userId = User.getUserIdFromLocal(context.getApplicationContext());
        if (userId > 0 && !TextUtils.isEmpty(s2)) {
            Log.d(TAG, "onBind channel_id: " + s2 + ", " + userId);
            RequestParams params = new RequestParams();
            params.put("channel_id", s2);
            params.put("type", 3);
            HttpUtil.post("/push/setchannel", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "set push success: " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, errorResponse.toString(), throwable);
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

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
