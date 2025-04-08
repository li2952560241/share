package com.share.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.share.common.core.constant.SecurityConstants;
import com.share.common.core.domain.R;
import com.share.device.domain.*;
import com.share.device.service.ICabinetService;
import com.share.device.service.IDeviceService;
import com.share.device.service.IMapService;
import com.share.device.service.IStationService;
import com.share.rule.api.RemoteFeeRuleService;
import com.share.rule.domain.FeeRule;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    //获取附近站点信息列表
    @Override
    public List<StationVo> nearbyStation(String latitude, String longitude) {
        //确定中心点
        //经度  和  纬度
        GeoJsonPoint geoJsonPoint =
                new GeoJsonPoint(Double.parseDouble(longitude),
                        Double.parseDouble(latitude));

        //设置查询半径，比如查询50公里
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

}