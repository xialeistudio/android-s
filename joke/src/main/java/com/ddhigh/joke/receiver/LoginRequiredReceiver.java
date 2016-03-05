package com.ddhigh.joke.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ddhigh.joke.MyApplication;
import com.ddhigh.joke.auth.LoginActivity;

/**
 * @project Study
 * @package com.ddhigh.joke.receiver
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class LoginRequiredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MyApplication.TAG, "login required");
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
