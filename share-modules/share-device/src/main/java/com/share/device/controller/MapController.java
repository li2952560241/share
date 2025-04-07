package com.share.device.controller;

import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.device.service.IMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "地图接口管理")
@RestController
@RequestMapping("/map")
public class MapController extends BaseController
{
    @Autowired
    private IMapService mapService;

    @Operation(summary = "根据经纬度计算详细地址")
    @GetMapping("/calculateLatLng/{keyword}")
    public AjaxResult calculateLatLng(@PathVariable String keyword)
    {
        return success(mapService.calculateLatLng(keyword));
    }

}
