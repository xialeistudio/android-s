package com.ddhigh.dodo.util;

import com.ddhigh.dodo.Config;

import org.xutils.http.RequestParams;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class HttpUtil {
    static String api;
    static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        HttpUtil.token = token;
    }

    public static void setApi(String api) {
        HttpUtil.api = api;
    }


    /**
     * 准备请求参数
     *
     * @param uri
     * @return
     */
    public static RequestParams prepare(String uri) {
        RequestParams params = new RequestParams(uri.startsWith("http") ? uri : (api + uri));
        //请求加密
        params.addHeader("X-APICloud-AppId", Config.ApiCloud.appid);
        long timestamp = System.currentTimeMillis();
        String key = EncryUtil.sha1(Config.ApiCloud.appid + "UZ" + Config.ApiCloud.appkey + "UZ" + timestamp) + "." + timestamp;
        params.addHeader("X-APICloud-AppKey", key);
        if (token != null) {
            params.addHeader("authorization", token);
        }
        return params;
    }

}
