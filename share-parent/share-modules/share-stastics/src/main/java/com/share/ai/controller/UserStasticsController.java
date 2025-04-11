package com.share.ai.controller;

import com.share.ai.service.OrderStasticsService;
import com.share.ai.service.UserStasticsService;
import com.share.common.core.domain.R;
import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.user.api.RemoteUserService;
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
public class UserStasticsController extends BaseController {

    @Autowired
    private RemoteUserService remoteUserService;

    @GetMapping("/userCount")
    public AjaxResult userCount() {
        R<Map<String,Object>> result = remoteUserService.getUserCount();
        Map<String, Object> map = result.getData();
        return success(map);
    }
}
