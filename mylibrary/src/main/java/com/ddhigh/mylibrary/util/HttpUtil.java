package com.ddhigh.mylibrary.util;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
        client.post(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(),"utf-8"), "application/json", handler);
    }

    public static void get(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.get(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(),"utf-8"), "application/json", handler);
    }

    public static void delete(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.delete(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(),"utf-8"), "application/json", handler);
    }

    public static void put(Context context, String uri, JSONObject jsonObject, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        encryptRequest();
        client.put(context, getAbsoluteUrl(uri), new StringEntity(jsonObject.toString(),"utf-8"), "application/json", handler);
    }

    private static String getAbsoluteUrl(String uri) {
        if (uri.startsWith("http")) {
            return uri;
        }
        return api + uri;
    }

    protected static void encryptRequest() {
    }

}
