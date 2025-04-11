package com.share.device.emqx.handler.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.share.common.core.constant.DeviceConstants;
import com.share.device.domain.Cabinet;
import com.share.device.domain.CabinetSlot;
import com.share.device.domain.PowerBank;
import com.share.device.emqx.annotation.GuiguEmqx;
import com.share.device.emqx.constant.EmqxConstants;
import com.share.device.emqx.handler.MassageHandler;
import com.share.device.service.ICabinetService;
import com.share.device.service.ICabinetSlotService;
import com.share.device.service.IPowerBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@GuiguEmqx(topic = EmqxConstants.TOPIC_PROPERTY_POST)
public class PropertyPostHandler implements MassageHandler {

    @Autowired
    private ICabinetService cabinetService;

    @Autowired
    private IPowerBankService powerBankService;

    @Autowired
    private ICabinetSlotService cabinetSlotService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 处理消息:
     * @param message
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleMessage(JSONObject message) {
        log.info("handleMessage: {}", message.toJSONString());
        //消息编号
        String messageNo = message.getString("mNo");
        //防止重复请求
        String key = "property:post:" + messageNo;
        boolean isExist = redisTemplate.opsForValue().setIfAbsent(key, messageNo, 1, TimeUnit.HOURS);
        if (!isExist) {
            log.info("重复请求: {}", message.toJSONString());
            return;
        }

        //柜机编号
        String cabinetNo = message.getString("cNo");
        //充电宝编号
        String powerBankNo = message.getString("pNo");
        //插槽编号
        String slotNo = message.getString("sNo");
        //当前电量
        BigDecimal electricity = message.getBigDecimal("ety");
        if (StringUtils.isEmpty(cabinetNo)
                || StringUtils.isEmpty(powerBankNo)
                || StringUtils.isEmpty(slotNo)
                || null == electricity) {
            log.info("参数为空: {}", message.toJSONString());
            return;
        }
        //获取柜机
        Cabinet cabinet = cabinetService.getBtCabinetNo(cabinetNo);
        // 获取充电宝
        PowerBank powerBank =powerBankService.getByPowerBankNo(powerBankNo);

        //更新充电宝状态
        // 状态（0:未投放 1：可用 2：已租用 3：充电中 4：故障）
        //更新充电宝电量与状态
        powerBank.setElectricity(electricity);
        //电量大于可用最低值
        // 状态（0:未投放 1：可用 2：已租用 3：充电中 4：故障）
        if(electricity.subtract(DeviceConstants.ELECTRICITY_MIN).doubleValue() > 0) {
            //可以借用
            powerBank.setStatus("1");
        } else {
            //充电中
            powerBank.setStatus("3");
        }
        powerBankService.updateById(powerBank);

        //更新柜机可用充电宝数量
        // 获取柜机插槽列表
        List<CabinetSlot> cabinetSlotList = cabinetSlotService.list(new LambdaQueryWrapper<CabinetSlot>()
                .eq(CabinetSlot::getStatus, "1")
                .eq(CabinetSlot::getCabinetId, cabinet.getId())
                .select(CabinetSlot::getPowerBankId)
        );
        // 获取可用充电宝id列表
        List<Long> powerBankIdList =cabinetSlotList.stream().map(CabinetSlot::getPowerBankId).collect(Collectors.toList());
        // 获取可用充电宝数量
        Long availableNum = powerBankService.count(new LambdaQueryWrapper<PowerBank>().in(PowerBank::getId, powerBankIdList).eq(PowerBank::getStatus, "1"));
        cabinet.setAvailableNum(availableNum.intValue());
        cabinet.setUpdateTime(new Date(0));
        cabinetService.updateById(cabinet);
    }
}

