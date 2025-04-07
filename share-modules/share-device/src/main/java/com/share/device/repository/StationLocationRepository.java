package com.share.device.repository;

import com.share.device.domain.StationLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationLocationRepository
        extends MongoRepository<StationLocation, String> {

    StationLocation getByStationId(Long id);
    void deleteByStationId(Long stationId);
}
