package com.ddhigh.overtime.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.ddhigh.mylibrary.widget.CircleProgressBar;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.util.AppUtil;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.activity_check_update)
public class CheckUpdateActivity extends BaseActivity {
    public static String ACTION = "com.ddhigh.overtime.action.CHECK_UPDATE";

    @ViewInject(R.id.progressBar)
    CircleProgressBar circleProgressBar;
    @ViewInject(R.id.txtCurrentVersion)
    TextView txtCurrentVersion;
    @ViewInject(R.id.txtUpdateLog)
    TextView txtUpdateLog;

    int currentVersionCode;

    DownloadManager downloadManager;
    DownloadChangeObserver downloadObServer;
    long lastDownloadId = 0;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        showActionBar();

        PackageInfo packageInfo = AppUtil.getAppInfo(this);
        currentVersionCode = packageInfo.versionCode;
        txtCurrentVersion.setText("当前版本：" + packageInfo.versionName);
        circleProgressBar.setMaxProgress(100);
        circleProgressBar.setProgress(0);
        circleProgressBar.setText("检查更新中");

        launchChecker(packageInfo);
    }

    private void launchChecker(PackageInfo packageInfo) {
        RequestParams params = new RequestParams();
        params.put("platform", "android");
        params.put("version", packageInfo.versionName);

        final int[] progress = {0};

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progress[0]++;
                circleProgressBar.setProgressNotInUIThread(progress[0]);
                circleProgressBar.setTextNotUIThread("连接服务器(" + progress[0] + "%)");
            }
        };
        final Timer timer = new Timer("checkUpdateTimer");
        HttpUtil.get("/app/update", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("checkUpdate", "check success: " + response.toString());
                circleProgressBar.setText("连接服务器成功");
                circleProgressBar.setProgress(100);
                checkVersion(response);
            }

            @Override
            public void onStart() {
                timer.schedule(timerTask, 0, 100);
            }

            @Override
            public void onFinish() {
                timer.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("checkUpdate", "check fail: " + statusCode, throwable);
                circleProgressBar.setText("连接服务器失败");
            }
        });
    }

    private void checkVersion(JSONObject response) {
        if (response.has("versionCode")) {
            try {
                int versionCode = response.getInt("versionCode");
                String updateLog = response.getString("updateLog");
                String link = response.getString("downloadUrl");
                String versionName = response.getString("versionName");
                if (currentVersionCode < versionCode) {
                    //新版本，读取更新日志和下载连接
                    txtUpdateLog.setText("新版本：" + versionName + "\n更新日志\n" + updateLog);
                    download(link);
                } else {
                    circleProgressBar.setText("已是最新版本");
                    circleProgressBar.setProgress(circleProgressBar.getMaxProgress());
                    txtUpdateLog.setText("更新日志\n" + updateLog);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                circleProgressBar.setText("解析响应失败");
            }
        }
    }

    private void download(String link) {
        circleProgressBar.setText("下载中(0%)");
        circleProgressBar.setProgress(0);
        Log.d("checkUpdate", "download apk from: " + link);

        initDownloadManager(link);
    }

    File f;

    private void initDownloadManager(String url) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        f = new File(url);
        lastDownloadId = downloadManager.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_MOBILE
                                | DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, f.getName()));
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadObServer = new DownloadChangeObserver(null);
        getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObServer);
    }

    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            Log.v("tag", "" + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            queryDownloadStatus();
        }
    };

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(lastDownloadId);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
            int fileSizeIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
//            String title = c.getString(titleIdx);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);

            int progress = bytesDL * 100 / fileSize;
            circleProgressBar.setTextNotUIThread("下载中(" + progress + "%)");
            circleProgressBar.setMaxProgress(fileSize);
            circleProgressBar.setProgressNotInUIThread(bytesDL);

            // Translate the pause reason to friendly text.
//            int reason = c.getInt(reasonIdx);


            // Display the status
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.v("tag", "STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    Log.v("tag", "STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                    Log.v("tag", "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //完成
                    Log.v("tag", "下载完成");
                    circleProgressBar.setTextNotUIThread("下载完成");
                    installApp(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), f.getName()));
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    Log.v("tag", "STATUS_FAILED");
                    downloadManager.remove(lastDownloadId);
                    break;
            }
        }
    }

    private void installApp(File file) {
        Log.d("checkUpdate", "install " + file.getAbsolutePath());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        getContentResolver().unregisterContentObserver(downloadObServer);
    }
}
