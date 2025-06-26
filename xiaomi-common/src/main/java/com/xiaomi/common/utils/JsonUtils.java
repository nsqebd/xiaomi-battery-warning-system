package com.xiaomi.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 */
@Slf4j
public class JsonUtils {

    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            log.error("对象转JSON字符串失败：{}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString) || clazz == null) {
            return null;
        }
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (JSONException e) {
            log.error("JSON字符串转对象失败：{}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString) || clazz == null) {
            return null;
        }
        try {
            return JSON.parseArray(jsonString, clazz);
        } catch (JSONException e) {
            log.error("JSON字符串转List失败：{}", e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMap(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return JSON.parseObject(jsonString, Map.class);
        } catch (JSONException e) {
            log.error("JSON字符串转Map失败：{}", e.getMessage(), e);
            return null;
        }
    }

    public static boolean isValidJson(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return false;
        }
        try {
            JSON.parse(jsonString);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}