package com.share.device.service;

import com.share.device.domain.ScanChargeVo;
import com.share.device.domain.StationVo;

import java.util.List;


public interface IDeviceService
{

    //获取附近站点信息列表
    List<StationVo> nearbyStation(String latitude, String longitude);

    // 根据id获取站点信息
    StationVo getStation(Long id, String latitude, String longitude);

    ////扫码充电接口
    ScanChargeVo scanCharge(String cabinetNo);
}