package com.share.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.device.domain.Cabinet;
import com.share.device.domain.CabinetSlot;
import com.share.device.domain.PowerBank;
import com.share.device.mapper.CabinetMapper;
import com.share.device.mapper.CabinetSlotMapper;
import com.share.device.service.ICabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CabinetServiceImpl extends ServiceImpl<CabinetMapper, Cabinet>
        implements ICabinetService {

    @Autowired
    private CabinetMapper cabinetMapper;

    @Autowired
    private PowerBankServiceImpl powerBankService;

    @Autowired
    private CabinetSlotMapper cabinetSlotMapper;
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

    //根据编号查询
    @Override
    public Cabinet getBtCabinetNo(String cabinetNo) {
        return cabinetMapper.selectOne(new LambdaQueryWrapper<Cabinet>().eq(Cabinet::getCabinetNo, cabinetNo));
    }

    @Override
    public Map<String, Object> getAllInfo(Long id) {
        // 查询柜机信息
        Cabinet cabinet = this.getById(id);

        // 查询插槽信息
        List<CabinetSlot> cabinetSlotList = cabinetSlotMapper.selectList(new LambdaQueryWrapper<CabinetSlot>().eq(CabinetSlot::getCabinetId, cabinet.getId()));
        // 获取可用充电宝id列表
        List<Long> powerBankIdList = cabinetSlotList.stream().filter(item -> null != item.getPowerBankId()).map(CabinetSlot::getPowerBankId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(powerBankIdList)) {
            List<PowerBank> powerBankList = powerBankService.listByIds(powerBankIdList);
            Map<Long,PowerBank> powerBankIdToPowerBankMap = powerBankList.stream().collect(Collectors.toMap(PowerBank::getId, PowerBank -> PowerBank));
            cabinetSlotList.forEach(item -> item.setPowerBank(powerBankIdToPowerBankMap.get(item.getPowerBankId())));
        }

        Map<String, Object> result = Map.of("cabinet", cabinet, "cabinetSlotList", cabinetSlotList);
        return result;
    }
}
