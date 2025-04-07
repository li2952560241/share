package com.share.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.device.domain.CabinetType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ICabinetTypeService extends IService<CabinetType>
{

    /**
     * 查询柜机类型列表
     *
     * @param cabinetType 柜机类型
     * @return 柜机类型集合
     */
    public List<CabinetType> selectCabinetTypeList(CabinetType cabinetType);

}