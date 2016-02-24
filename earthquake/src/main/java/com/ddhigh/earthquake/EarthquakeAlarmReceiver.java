package com.ddhigh.earthquake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/24 0024
 */
public class EarthquakeAlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_REFRESH_EARTHQUAKE_ALARM = "com.ddhigh.earthquake.ACTION_REFRESH_EARTHQUAKE_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, EarthquakeUpdateService.class);
        context.startService(intent1);
    }
}
