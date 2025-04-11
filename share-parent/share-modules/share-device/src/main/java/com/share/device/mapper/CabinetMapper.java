package com.share.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.share.device.domain.Cabinet;

import java.util.List;

public interface CabinetMapper extends BaseMapper<Cabinet> {
    //分页查询
    List<Cabinet> selectListCabinet(Cabinet cabinet);
}
