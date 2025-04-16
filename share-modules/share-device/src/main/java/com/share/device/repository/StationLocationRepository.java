package com.share.device.repository;

import com.share.device.domain.StationLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationLocationRepository
        extends MongoRepository<StationLocation, String> {

    //根据id查询 站点位置
    StationLocation getByStationId(Long id);
    void deleteByStationId(Long stationId);
}
