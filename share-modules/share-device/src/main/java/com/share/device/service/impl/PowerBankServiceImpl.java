package com.share.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.common.core.exception.ServiceException;
import com.share.device.domain.PowerBank;
import com.share.device.mapper.PowerBankMapper;
import com.share.device.service.IPowerBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerBankServiceImpl extends ServiceImpl<PowerBankMapper, PowerBank>
        implements IPowerBankService {

    @Autowired
    private PowerBankMapper powerBankMapper;

    //分页查询
    @Override
    public List<PowerBank> selectListPowerBank(PowerBank powerBank) {
        return powerBankMapper.selectListPowerBank(powerBank);
    }

    //添加
    @Override
    public int savePowerBank(PowerBank powerBank) {
        //1 判断powerBankNo是否存在，如果存在不进行添加
        String powerBankNo = powerBank.getPowerBankNo();
        //封装条件
        LambdaQueryWrapper<PowerBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PowerBank::getPowerBankNo,powerBankNo);
        Long count = powerBankMapper.selectCount(wrapper);
        //判断
        if(count > 0) {//如果存在不进行添加
            throw new ServiceException("充电宝编号已经存在");
        }
        //不存在做添加
        int rows = powerBankMapper.insert(powerBank);
        return rows;
    }

    //修改
    @Override
    public int updatePowerBank(PowerBank powerBank) {
        //1 判断状态是0才进行修改
        Long id = powerBank.getId();
        PowerBank oldPowerBank = powerBankMapper.selectById(id);
        if(oldPowerBank != null && "0".equals(oldPowerBank.getStatus())) {

            int rows = powerBankMapper.updateById(powerBank);
            return rows;
        }
        return 0;
    }

    @Override
    public PowerBank getByPowerBankNo(String powerBankNo) {
        return powerBankMapper.selectOne(new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getPowerBankNo, powerBankNo));
    }
}
