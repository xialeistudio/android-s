package com.ddhigh.overtime.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
public class Model {
    /**
     * 解码
     *
     * @param jsonObject 远程数据
     */
    public void decode(JSONObject jsonObject) throws JSONException, IllegalAccessException {
        Iterator iterator = jsonObject.keys();
        Field[] fields = getClass().getDeclaredFields();
        Map<String, Field> map = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field);
        }
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (map.containsKey(key)) {
                map.get(key).set(this, jsonObject.get(key));
            }
        }
    }

    /**
     * 编码
     *
     * @return json
     */
    public JSONObject encode() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            jsonObject.put(field.getName(), field);
        }
        return jsonObject;
    }
}
