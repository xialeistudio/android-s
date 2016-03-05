package com.ddhigh.joke.util;

import android.content.Context;
import android.text.TextUtils;

import com.ddhigh.joke.JokeException;
import com.ddhigh.joke.config.Config;
import com.ddhigh.mylibrary.util.EncryUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class HttpUtil {

    public static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    static String api;
    static String token;

    public static void setToken(String token) {
        HttpUtil.token = token;
    }

    public static void setApi(String api) {
        HttpUtil.api = api;
    }

    public static void get(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.get(getAbsoluteUrl(uri), params, handler);
    }

    public static void post(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.post(getAbsoluteUrl(uri), params, handler);
    }

    public static void delete(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.delete(getAbsoluteUrl(uri), params, handler);
    }

    public static void put(String uri, RequestParams params, AsyncHttpResponseHandler handler) {
        encryptRequest();
        client.put(getAbsoluteUrl(uri), params, handler);
    }

    public static void post(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.post(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(), "utf-8"), "application/json", handler);
    }

    public static void get(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.get(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(), "utf-8"), "application/json", handler);
    }

    public static void delete(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.delete(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(), "utf-8"), "application/json", handler);
    }

    public static void put(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.put(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(), "utf-8"), "application/json", handler);
    }

    private static String getAbsoluteUrl(String uri) {
        if (uri.startsWith("http")) {
            return uri;
        }
        return api + uri;
    }

    protected static void encryptRequest() {
        long timestamp = System.currentTimeMillis();
        String key = EncryUtil.sha1(Config.ApiCloud.AppId + "UZ" + Config.ApiCloud.AppKey + "UZ" + timestamp) + "." + timestamp;
        client.addHeader("X-APICloud-AppId", Config.ApiCloud.AppId);
        client.addHeader("X-APICloud-AppKey", key);
        if (token != null && !TextUtils.isEmpty(token)) {
            client.addHeader("authorization", token);
        }
    }

    /**
     * 处理错误
     *
     * @param object 原始请求
     * @throws JSONException
     * @throws JokeException
     */
    public static void handleError(String object) throws JSONException, JokeException {
        JSONObject jsonObject = new JSONObject(object);
        if (jsonObject.has("error")) {
            JSONObject error = jsonObject.getJSONObject("error");
            String msg = error.getString("message");
            if (errmap.containsKey(msg)) {
                msg = errmap.get(msg);
            }
            throw new JokeException(msg, error.getInt("statusCode"));
        }
    }


    /**
     * 错误消息翻译
     */
    private static Map<String, String> errmap = new HashMap<>();

    static {
        errmap.put("username:already exists", "用户名已存在");
    }
}
