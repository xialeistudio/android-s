package com.ddhigh.downloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "DOWNLOADMANAGER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnDownload) {
            String service = Context.DOWNLOAD_SERVICE;
            final DownloadManager downloadManager = (DownloadManager) getSystemService(service);

            Uri uri = Uri.parse("http://sqdd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("QQ.apk");
            request.setDescription("手机QQ");
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,"mobileqq_android.apk");
            final long referenceID = downloadManager.enqueue(request);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            Log.d(TAG, "reference: " + referenceID);

            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long referenceID2 = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (referenceID == referenceID2) {
                        //下载完成
                        Log.d(TAG, "下载QQ完成");
                    }
                }
            };
            registerReceiver(receiver, intentFilter);

            IntentFilter intentFilter1 = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            BroadcastReceiver receiver1 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String extraID = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                    long[] references = intent.getLongArrayExtra(extraID);

                    for(long reference:references){
                        if(reference == referenceID){
                            Log.d(TAG,"下载QQ完成，点击通知栏");
                        }
                    }
                }
            };

            registerReceiver(receiver1,intentFilter1);
        }
    }
}
