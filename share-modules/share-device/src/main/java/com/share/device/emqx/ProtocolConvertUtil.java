package com.share.device.emqx;

import com.alibaba.fastjson2.JSONObject;

public class ProtocolConvertUtil {

//    public static void main(String[] args) {
//
//    }

    /**
     * 将字符串转换为 JSONObject
     * @param message
     * @return
     */
    public static JSONObject convertJson(String message) {
        // 创建一个 JSONObject 对象
        JSONObject jsonObject = new JSONObject();

        // 按照 "|" 分割字符串
        String[] pairs = message.replaceAll("\r", "").replaceAll("\n", "").split("\\|");

        // 遍历每个键值对
        for (String pair : pairs) {
            // 按照 "=" 分割键和值
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                // 将键值对添加到 JSONObject 中
                jsonObject.put(key, value);
            }
        }

        // 输出 JSON 字符串
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    /**
     * 将 JSONObject 转换为字符串
     * @param jsonMessage
     */
    public static String convertString(JSONObject jsonMessage) {
        // 构建目标字符串
        StringBuilder result = new StringBuilder();

        // 获取所有的键
        for (String key : jsonMessage.keySet()) {
            String value = jsonMessage.getString(key);
            if (result.length() > 0) {
                result.append("|");
            }
            result.append(key).append("=").append(value);
        }

        // 输出结果字符串
        System.out.println(result.toString());
        return result.toString();
    }
}
