package com.ddhigh.overtime.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ddhigh.mylibrary.widget.CircleProgressBar;
import com.ddhigh.overtime.R;
import com.ddhigh.overtime.util.AppUtil;
import com.ddhigh.overtime.util.HttpUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
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
    public static final String ACTION = "com.ddhigh.overtime.action.CHECK_UPDATE";

    @ViewInject(R.id.progressBar)
    private
    CircleProgressBar circleProgressBar;
    @ViewInject(R.id.txtCurrentVersion)
    private
    TextView txtCurrentVersion;
    @ViewInject(R.id.txtUpdateLog)
    private
    TextView txtUpdateLog;

    private int currentVersionCode;

    private boolean isActive = false;

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

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
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

        File url = new File(link);
        File file = new File(application.getApplicationPath(), url.getName());
        HttpUtil.get(link, null, new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.e("checkUpdate", "fail", throwable);
                Toast.makeText(CheckUpdateActivity.this, "下载失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                circleProgressBar.setText("下载失败(" + statusCode + ")");
                circleProgressBar.setProgress(circleProgressBar.getMaxProgress());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                installApp(file);
                circleProgressBar.setText("下载完成");
                circleProgressBar.setProgress(circleProgressBar.getMaxProgress());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                int p = (int) (bytesWritten * 100 / totalSize);
                circleProgressBar.setText("下载中(" + p + "%)");
                circleProgressBar.setProgress(p);
            }
        });
    }


    private void installApp(File file) {
        Log.d("checkUpdate", "install " + file.getAbsolutePath());
        if (!isActive) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(i);
    }
}
