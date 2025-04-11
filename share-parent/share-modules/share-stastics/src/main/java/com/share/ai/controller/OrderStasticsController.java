package com.share.ai.controller;

import com.share.ai.service.OrderStasticsService;
import com.share.common.core.domain.R;
import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.order.api.RemoteOrderInfoService;
import com.share.order.domain.OrderSqlVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Tag(name = "AI数据统计")
@RestController
@RequestMapping("/sta")
public class OrderStasticsController extends BaseController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    @GetMapping("/orderData")
    public AjaxResult getOrderData(@RequestParam(value = "message",defaultValue = "hello")
                                       String message) {
        // restTemplate调用ai模块根据需求得到生成sql语句
        String sql =
                restTemplate.getForObject("http://localhost:8899/ai/generateSql?message=" + message,
                String.class);
        //远程调用
        //根据sql获取报表数据
        OrderSqlVo orderSqlVo = new OrderSqlVo();
        orderSqlVo.setSql(sql);
        R<Map<String,Object>> result = remoteOrderInfoService.getOrderCount(orderSqlVo);
        Map<String, Object> map = result.getData();
        return success(map);
    }
}
