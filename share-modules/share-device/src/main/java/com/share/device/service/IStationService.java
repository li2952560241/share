package com.share.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.device.domain.Station;

import java.util.List;

public interface IStationService extends IService<Station> {

    //分页查询
    List<Station> selectStationList(Station station);

    int saveStation(Station station);

    int updateStation(Station station);

    int setData(Station station);

    void updateData();
    Station getByCabinetId(Long cabinetId);
}
