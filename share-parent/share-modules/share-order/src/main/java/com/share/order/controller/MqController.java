package com.share.order.controller;

import com.github.pagehelper.PageHelper;
import com.share.common.core.context.SecurityContextHolder;
import com.share.common.core.domain.R;
import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.core.web.page.TableDataInfo;
import com.share.common.rabbit.constant.MqConst;
import com.share.common.rabbit.service.RabbitService;
import com.share.common.security.annotation.InnerAuth;
import com.share.common.security.annotation.RequiresLogin;
import com.share.common.security.utils.SecurityUtils;
import com.share.order.domain.OrderInfo;
import com.share.order.domain.OrderSqlVo;
import com.share.order.service.IOrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Mq接口管理")
@RestController
@RequestMapping("/mq")
public class MqController extends BaseController
{
    @Autowired
    private RabbitService rabbitService;

    @Operation(summary = "发送消息")
    @GetMapping("/sendMessage")
    public AjaxResult sendMessage()
    {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_TEST, "hello");
        return success();
    }

}