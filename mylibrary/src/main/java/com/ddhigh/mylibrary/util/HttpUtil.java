package com.ddhigh.mylibrary.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @project Study
 * @package com.ddhigh.mylibrary.util
 * @user xialeistudio
 * @date 2016/3/2 0002
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    private static String BASE_URL = "https://d.apicloud.com/mcm/api";

    public static void get(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.get(getAbsoluteUrl(uri), params, handler);
    }

    public static void post(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.post(getAbsoluteUrl(uri), params, handler);
    }


    private static String getAbsoluteUrl(String uri) {
        if (uri.startsWith("http")) {
            return uri;
        }
        return BASE_URL + uri;
    }

    private static void encryptRequest() {
        String appid = "A6990125149280";
        String appkey = "E448A921-A1A7-FD66-8B47-BDF15CF872CE";
        client.addHeader("X-APICloud-AppId", appid);
        long timestamp = System.currentTimeMillis();
        String key = EncryUtil.sha1(appid + "UZ" + appkey + "UZ" + timestamp) + "." + timestamp;
        client.addHeader("X-APICloud-AppKey", key);
    }

}
