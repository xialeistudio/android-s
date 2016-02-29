package com.ddhigh.dodo.util;

import android.util.Log;

import com.ddhigh.dodo.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Http工具类
 *
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static String api;

    static {
        client.setTimeout(2000);
    }

    public static void setApi(String api) {
        HttpUtil.api = api;
    }

    /**
     * Get请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param handler 回调
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        url = url.startsWith("http") ? url : (api + url);
        client.get(url, params, handler);
        Log.d(MyApplication.TAG, "http get: " + url);
    }

    /**
     * Post请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param handler 回调
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        url = url.startsWith("http") ? url : (api + url);
        client.post(url, params, handler);
        Log.d(MyApplication.TAG, "http post: " + url);
    }
}
