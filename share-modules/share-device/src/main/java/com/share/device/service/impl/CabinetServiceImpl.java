package com.share.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.device.domain.Cabinet;
import com.share.device.mapper.CabinetMapper;
import com.share.device.service.ICabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CabinetServiceImpl extends ServiceImpl<CabinetMapper, Cabinet>
        implements ICabinetService {

    @Autowired
    private CabinetMapper cabinetMapper;
    //分页查询
    @Override
    public List<Cabinet> selectListCabinet(Cabinet cabinet) {
        return cabinetMapper.selectListCabinet(cabinet);
    }

    //未使用柜机
    @Override
    public List<Cabinet> searchNoUseList(String keyword) {
        LambdaQueryWrapper<Cabinet> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Cabinet::getCabinetNo,keyword);
        wrapper.eq(Cabinet::getStatus,0);
        List<Cabinet> list = cabinetMapper.selectList(wrapper);
        return list;
    }

    @Override
    public Cabinet getBtCabinetNo(String cabinetNo) {
        return cabinetMapper.selectOne(new LambdaQueryWrapper<Cabinet>().eq(Cabinet::getCabinetNo, cabinetNo));
    }
}
