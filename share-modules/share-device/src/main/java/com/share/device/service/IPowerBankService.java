package com.share.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.device.domain.PowerBank;

import java.util.List;

public interface IPowerBankService extends IService<PowerBank> {
    //分页查询
    List<PowerBank> selectListPowerBank(PowerBank powerBank);

    ////添加
    int savePowerBank(PowerBank powerBank);

    //修改
    int updatePowerBank(PowerBank powerBank);

    PowerBank getByPowerBankNo(String powerBankNo);
}
