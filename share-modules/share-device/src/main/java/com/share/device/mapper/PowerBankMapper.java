package com.share.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.share.device.domain.PowerBank;

import java.util.List;

public interface PowerBankMapper extends BaseMapper<PowerBank> {

    //分页查询
    List<PowerBank> selectListPowerBank(PowerBank powerBank);
}
