package com.ddhigh.overtime.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                Field field = map.get(key);
                if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    field.set(this, jsonObject.get(key));
                }
            }

        }
    }

    Model() {
    }

    /**
     * 编码
     *
     * @return json
     */
    public JSONObject encode() throws JSONException, IllegalAccessException {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //不反射静态常量
            if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                jsonObject.put(field.getName(), field.get(this));
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        Field[] fields = getClass().getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!Modifier.isFinal(field.getModifiers())) {
                try {
                    builder.append(field.getName()).append(": ").append(field.get(this)).append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        boolean isInstanceOf = o instanceof Model;
        if (!isInstanceOf) {
            return false;
        }
        boolean isEqual = true;
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                isEqual = field.get(this).equals(field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return isEqual;
    }
}
