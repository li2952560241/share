package com.share.device.emqx.handler.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.share.common.core.constant.DeviceConstants;
import com.share.common.core.constant.SecurityConstants;
import com.share.common.core.utils.StringUtils;
import com.share.common.rabbit.constant.MqConst;
import com.share.common.rabbit.service.RabbitService;
import com.share.device.domain.Cabinet;
import com.share.device.domain.CabinetSlot;
import com.share.device.domain.PowerBank;
import com.share.device.domain.Station;
import com.share.device.emqx.annotation.GuiguEmqx;
import com.share.device.emqx.constant.EmqxConstants;
import com.share.device.emqx.handler.MassageHandler;
import com.share.device.service.ICabinetService;
import com.share.device.service.ICabinetSlotService;
import com.share.device.service.IPowerBankService;
import com.share.device.service.IStationService;
import com.share.order.api.RemoteOrderInfoService;
import com.share.order.domain.EndOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@GuiguEmqx(topic = EmqxConstants.TOPIC_POWERBANK_CONNECTED)
public class PowerBankConnectedHandler implements MassageHandler {

    @Autowired
    private ICabinetService cabinetService;

    @Autowired
    private IPowerBankService powerBankService;

    @Autowired
    private ICabinetSlotService cabinetSlotService;

    @Autowired
    private IStationService stationService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 处理消息:
     *  充电宝插入柜机，连接上线，有两种情况
     *      1， 无订单，初始化插入
     *      2， 有订单，归还充电宝
     * @param message
     * {"messageNo":"112233","cabinetNo":"xgxgxxxg","powerBankNo":"gg001",
     * "slotNo":"1", "electricity": 85}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleMessage(JSONObject message) {
        //1 获取messageNo，防止重复提交
        String messageNo = message.getString("mNo");
        String key = "powerBank:connected:" + messageNo;
        Boolean ifAbsent =
                redisTemplate.opsForValue().setIfAbsent(key, messageNo, 1, TimeUnit.HOURS);
        if(!ifAbsent) {
            return;
        }

        //2 获取cabinetNo  powerBankNo  slotNo  electricity
        String cabinetNo = message.getString("cNo");
        //充电宝编号
        String powerBankNo = message.getString("pNo");
        //插槽编号
        String slotNo = message.getString("sNo");
        //当前电量
        BigDecimal electricity = message.getBigDecimal("ety");

        //3 非空判断
        if (StringUtils.isEmpty(cabinetNo)
                || StringUtils.isEmpty(powerBankNo)
                || StringUtils.isEmpty(slotNo)
                || null == electricity) {
            log.info("参数为空: {}", message.toJSONString());
            return;
        }

        //4 获取相关数据 柜机、充电宝、插槽数据
        Cabinet cabinet = cabinetService.getBtCabinetNo(cabinetNo);
        // 获取充电宝
        PowerBank powerBank = powerBankService.getByPowerBankNo(powerBankNo);
        // 获取插槽
        CabinetSlot cabinetSlot = cabinetSlotService.getBtSlotNo(cabinet.getId(), slotNo);

        //5 更新充电宝数据（电量、状态）
        powerBank.setElectricity(electricity);
        //电量大于可用最低值
        // 状态（0:未投放 1：可用 2：已租用 3：充电中 4：故障）
        if (electricity.subtract(DeviceConstants.ELECTRICITY_MIN).doubleValue() > 0) {
            //可以借用
            powerBank.setStatus("1");
        } else {
            //充电中
            powerBank.setStatus("3");
        }
        powerBankService.updateById(powerBank);

        //6 更新柜机、插槽 数据
        //更新插槽状态
        cabinetSlot.setPowerBankId(powerBank.getId());
        cabinetSlot.setStatus("1");
        cabinetSlot.setUpdateTime(new Date());
        cabinetSlotService.updateById(cabinetSlot);

        //更新柜机信息
        int freeSlots = cabinet.getFreeSlots() - 1;
        cabinet.setFreeSlots(freeSlots);
        int usedSlots = cabinet.getUsedSlots() + 1;
        cabinet.setUsedSlots(usedSlots);
        //可以借用
        if ("1".equals(powerBank.getStatus())) {
            int availableNum = cabinet.getAvailableNum() + 1;
            cabinet.setAvailableNum(availableNum);
        }
        cabinet.setUpdateTime(new Date());
        cabinetService.updateById(cabinet);

        //7 发送mq消息结束订单（封装数据）
        EndOrderVo endOrderVo = new EndOrderVo();
        endOrderVo.setMessageNo(messageNo);
        endOrderVo.setEndTime(new Date());
        endOrderVo.setEndCabinetNo(cabinetNo);

        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getCabinetId,cabinet.getId());

        Station station = stationService.getOne(wrapper);
        endOrderVo.setEndStationId(station.getId());
        endOrderVo.setEndStationName(station.getName());
        endOrderVo.setPowerBankNo(powerBankNo);

        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER,
                MqConst.ROUTING_END_ORDER,
                JSONObject.toJSONString(endOrderVo));
    }
}

