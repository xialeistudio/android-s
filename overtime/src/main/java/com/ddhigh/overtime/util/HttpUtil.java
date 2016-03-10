package com.ddhigh.overtime.util;

import com.ddhigh.overtime.exception.AppBaseException;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtil extends com.ddhigh.mylibrary.util.HttpUtil {
    static {
        setApi("https://overtime.ddhigh.com/v1");
    }

    /**
     * 处理错误
     *
     * @param response Http回调
     * @throws AppBaseException
     */
    public static void handleError(JSONObject response) throws AppBaseException {
        try {
            int errcode = 0;
            String errmsg = "";
            if (response.has("errcode")) {
                errcode = response.getInt("errcode");
            }
            if (errcode > 0 && response.has("errmsg")) {
                errmsg = response.getString("errmsg");
                throw new AppBaseException(errmsg, errcode);
            }
        } catch (JSONException e) {
            throw new AppBaseException(e.getMessage(), AppBaseException.COMMON_ERROR_UNDEFINED);
        }
    }
}
