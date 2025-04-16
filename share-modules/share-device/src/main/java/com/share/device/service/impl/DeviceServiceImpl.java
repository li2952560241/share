package com.share.device.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.share.common.core.constant.SecurityConstants;
import com.share.common.core.context.SecurityContextHolder;
import com.share.common.core.domain.R;
import com.share.common.core.exception.ServiceException;
import com.share.common.core.utils.StringUtils;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.security.utils.SecurityUtils;
import com.share.device.domain.*;
import com.share.device.emqx.EmqxClientWrapper;
import com.share.device.emqx.ProtocolConvertUtil;
import com.share.device.emqx.constant.EmqxConstants;
import com.share.device.emqx.handler.impl.PowerBankUnlockHandler;
import com.share.device.service.*;
import com.share.order.api.RemoteOrderInfoService;
import com.share.order.domain.OrderInfo;
import com.share.rule.api.RemoteFeeRuleService;
import com.share.rule.domain.FeeRule;
import com.share.user.api.RemoteUserService;
import com.share.user.domain.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IStationService stationService;

    @Autowired
    private ICabinetService cabinetService;

    @Autowired
    private IMapService mapService;

    @Autowired
    private RemoteFeeRuleService remoteFeeRuleService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    // emqx客户端
    @Autowired
    private EmqxClientWrapper emqxClientWrapper;
    //获取附近站点信息列表
    @Override
    public List<StationVo> nearbyStation(String latitude, String longitude) {
        //确定中心点
        //经度  和  纬度
        GeoJsonPoint geoJsonPoint =
                new GeoJsonPoint(Double.parseDouble(longitude),
                        Double.parseDouble(latitude));

        //设置查询半径，比如查询50公里 前面是距离，后面是单位
        Distance distance = new Distance(50, Metrics.KILOMETERS);

        //画圆
        Circle circle = new Circle(geoJsonPoint,distance);

        //查询mongoDB数据
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        List<StationLocation> list = mongoTemplate.find(query, StationLocation.class);

        //查询其他需要数据，进行封装
        //根据查询mongoDB的list集合里面获取站点其他数据
        // 从list获取所有站点id
        List<Long> stationIdList =
                list.stream().map(StationLocation::getStationId).collect(Collectors.toList());

        // 根据所有站点id获取对应站点数据  in(1,2,3)
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper();
        // 添加判断，仅当 stationIdList 不为空时添加 in 条件
        if (!CollectionUtils.isEmpty(stationIdList)) {
            wrapper.in(Station::getId, stationIdList);
        }
        List<Station> stationList = stationService.list(wrapper);

        // List<Station> --  List<StationVo>
        List<StationVo> stationVoList = new ArrayList<>();
        //遍历
        stationList.forEach(station -> {
            // Station --  StationVo
            StationVo stationVo = new StationVo();
            BeanUtils.copyProperties(station, stationVo);

            //计算距离
            Double distanceStation = mapService.calculateDistance(longitude, latitude,
                    station.getLongitude().toString(),
                    station.getLatitude().toString());
            stationVo.setDistance(distanceStation);

            // 获取柜机信息
            Long cabinetId = station.getCabinetId();

            Cabinet cabinet = cabinetService.getById(cabinetId);
            //可用充电宝数量大于0，可借用
            if (cabinet.getAvailableNum() > 0) {
                stationVo.setIsUsable("1");
            } else {
                stationVo.setIsUsable("0");
            }
            // 获取空闲插槽数量大于0，可归还
            if (cabinet.getFreeSlots() > 0) {
                stationVo.setIsReturn("1");
            } else {
                stationVo.setIsReturn("0");
            }

            //获取站点规则数据
            Long feeRuleId = station.getFeeRuleId();
            R<FeeRule> feeRuleResult = remoteFeeRuleService.getFeeRule(feeRuleId);
            FeeRule feeRule = feeRuleResult.getData();
            String description = feeRule.getDescription();
            stationVo.setFeeRule(description);

            stationVoList.add(stationVo);
        });
        return stationVoList;
    }

    //根据id获取门店详情
    @Override
    public StationVo getStation(Long id, String latitude, String longitude) {
        Station station = stationService.getById(id);
        StationVo stationVo = new StationVo();
        BeanUtils.copyProperties(station, stationVo);
        // 计算距离
        Double distance = mapService.calculateDistance(longitude, latitude, station.getLongitude().toString(), station.getLatitude().toString());
        stationVo.setDistance(distance);

        // 获取柜机信息
        Cabinet cabinet = cabinetService.getById(station.getCabinetId());
        //可用充电宝数量大于0，可借用
        if(cabinet.getAvailableNum() > 0) {
            stationVo.setIsUsable("1");
        } else {
            stationVo.setIsUsable("0");
        }
        // 获取空闲插槽数量大于0，可归还
        if (cabinet.getFreeSlots() > 0) {
            stationVo.setIsReturn("1");
        } else {
            stationVo.setIsReturn("0");
        }

        // 获取费用规则
        FeeRule feeRule = remoteFeeRuleService.getFeeRule(station.getFeeRuleId()).getData();
        stationVo.setFeeRule(feeRule.getDescription());
        return stationVo;
    }

    //扫码充电接口
    @Override
    public ScanChargeVo scanCharge(String cabinetNo) {
        //1 远程调用：根据当前登录用户id查询用户信息，
        // 从用户信息获取是否支持免押金充电
        R<UserInfo> userInfoR = remoteUserService.getInfo(SecurityContextHolder.getUserId());
        UserInfo userInfo = userInfoR.getData();
        //判断
        if(userInfo == null) {
            throw new ServiceException("获取用户信息失败");
        }
        //判断是否免押金
        if("0".equals(userInfo.getDepositStatus())) {
            throw new ServiceException("未申请免押金使用");
        }

        ScanChargeVo scanChargeVo = new ScanChargeVo();
        //2 远程调用：判断用户是否有未完成订单
        R<OrderInfo> orderInfoR = remoteOrderInfoService.getNoFinishOrder(SecurityUtils.getUserId());
        OrderInfo orderInfo = orderInfoR.getData();
        if(orderInfo != null) {//有 未完成订单
            String status = userInfo.getStatus();
            if("0".equals(status)) {
                scanChargeVo.setStatus("2");
                scanChargeVo.setMessage("有未归还充电宝，请归还后使用");
                return scanChargeVo;
            }
            if("1".equals(status)) {
                scanChargeVo.setStatus("3");
                scanChargeVo.setMessage("有未支付订单，去支付");
                return scanChargeVo;
            }
        }

        //3 从柜机里面获取最优充电宝
        AvailableProwerBankVo availableProwerBankVo =
                this.checkAvailableProwerBank(cabinetNo); //在一个方法中实现
        if(null == availableProwerBankVo) {
            throw new ServiceException("无可用充电宝");
        }
        if(!StringUtils.isEmpty(availableProwerBankVo.getErrMessage())) {
            throw new ServiceException(availableProwerBankVo.getErrMessage());
        }

        //4 把选择最优充电宝弹出
        // 使用MQTT弹出充电宝
        // 生成借取指令，弹出充电宝
        JSONObject object = new JSONObject();
        object.put("uId", SecurityContextHolder.getUserId());//SecurityUtils.getUserId()
        object.put("mNo", "mm"+ RandomUtil.randomString(8));
        object.put("cNo", cabinetNo);
        object.put("pNo", availableProwerBankVo.getPowerBankNo());
        object.put("sNo", availableProwerBankVo.getSlotNo());
        String topic = String.format(EmqxConstants.TOPIC_SCAN_SUBMIT, cabinetNo);
        String message = ProtocolConvertUtil.convertString(object);
        //发布消息
        emqxClientWrapper.publish(topic, message);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //5 返回封装需要数据
        scanChargeVo.setStatus("1");
        return scanChargeVo;
    }

    @Autowired
    private ICabinetSlotService cabinetSlotService;

    @Autowired
    private IPowerBankService powerBankService;
    //获取柜机的充电宝信息
    private AvailableProwerBankVo checkAvailableProwerBank(String cabinetNo) {
        //1 创建AvailableProwerBankVo对象
        AvailableProwerBankVo availableProwerBankVo = new AvailableProwerBankVo();

        //2 根据cabinetNo柜机编号查询柜机信息
        Cabinet cabinet = cabinetService.getBtCabinetNo(cabinetNo);

        //3 判断柜机可用充电宝数量是否大于0
        Integer availableNum = cabinet.getAvailableNum();
        if(availableNum == 0) {
            availableProwerBankVo.setErrMessage("无可用充电宝");
            return availableProwerBankVo;
        }

        //4 根据柜机id查询插槽列表，返回list集合
        LambdaQueryWrapper<CabinetSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CabinetSlot::getCabinetId,cabinet.getId());
        wrapper.eq(CabinetSlot::getStatus, "1");
        List<CabinetSlot> cabinetSlotList = cabinetSlotService.list(wrapper);

        //5 从第四步返回插槽列表list集合 获取对应充电宝id集合
        List<Long> powerBankIdList = cabinetSlotList.stream()
                .filter(item -> null != item.getPowerBankId())
                .map(CabinetSlot::getPowerBankId).collect(Collectors.toList());

        //6 根据充电宝id列表查询对应充电宝信息
        LambdaQueryWrapper<PowerBank> wrapperPowerBank = new LambdaQueryWrapper<>();
        wrapperPowerBank.in(PowerBank::getId,powerBankIdList);
        wrapperPowerBank.eq(PowerBank::getStatus,"1");
        List<PowerBank> powerBankList = powerBankService.list(wrapperPowerBank);
        //判断集合不为空
        if(CollectionUtils.isEmpty(powerBankList)) {
            availableProwerBankVo.setErrMessage("无可用充电宝");
            return availableProwerBankVo;
        }

        //7 把上一步获取充电宝信息集合进行排序（根据电量降序）
        if(powerBankList.size()>1) {
            Collections.sort(powerBankList,
                    (o1,o2)->o2.getElectricity().compareTo(o1.getElectricity()));
        }

        //8 获取电量最多充电宝信息
        PowerBank powerBank = powerBankList.get(0);

        //9 获取电量最多的充电宝对应插槽信息
        CabinetSlot cabinetSlot = cabinetSlotList.stream()
                .filter(item -> null != item.getPowerBankId()
                        && item.getPowerBankId().equals(powerBank.getId())).collect(Collectors.toList()).get(0);

        //10 锁定插槽（更新插槽状态）
        cabinetSlot.setStatus("2");
        cabinetSlotService.updateById(cabinetSlot);

        //11 返回需要vo数据
        availableProwerBankVo.setPowerBankNo(powerBank.getPowerBankNo());
        availableProwerBankVo.setSlotNo(cabinetSlot.getSlotNo());
        return availableProwerBankVo;
    }

}