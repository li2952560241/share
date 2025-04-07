package com.share.device.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.share.common.core.exception.ServiceException;
import com.share.device.service.IMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 修正导入路径
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapServiceImpl implements IMapService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tencent.map.key}") // 使用正确的注解
    private String key;

    @Override
    public JSONObject calculateLatLng(String keyword) {
        String url = "https://apis.map.qq.com/ws/geocoder/v1/?address={address}&key={key}";

        Map<String, String> map = new HashMap<>();
        map.put("address", keyword);
        map.put("key", key);

        JSONObject response = restTemplate.getForObject(url, JSONObject.class, map);
        if (response == null || response.getIntValue("status") != 0) {
            throw new ServiceException("地图解析异常");
        }

        // 返回第一条最佳线路
        JSONObject result = response.getJSONObject("result");
        log.info("地图解析结果: {}", result.toJSONString()); // 使用日志代替System.out.println
        return result.getJSONObject("location");
    }
}
