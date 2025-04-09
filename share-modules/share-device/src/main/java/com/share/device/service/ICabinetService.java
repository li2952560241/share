package com.share.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.device.domain.Cabinet;

import java.util.List;
import java.util.Map;

public interface ICabinetService extends IService<Cabinet> {
    //分页查询
    List<Cabinet> selectListCabinet(Cabinet cabinet);

    //未使用柜机
    List<Cabinet> searchNoUseList(String keyword);

    //根据柜机编号查询
    Cabinet getBtCabinetNo(String cabinetNo);

    Map<String, Object> getAllInfo(Long id);
}
