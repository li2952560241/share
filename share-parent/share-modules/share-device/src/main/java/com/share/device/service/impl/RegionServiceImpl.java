package com.share.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.device.domain.Region;
import com.share.device.mapper.RegionMapper;
import com.share.device.service.IRegionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region>
        implements IRegionService {

//    @Autowired
//    private RegionMapper regionMapper;

    //根据上级code获取下级数据列表
    @Override
    public List<Region> treeSelect(String code) {
        //SELECT * FROM region WHERE parent_code=?
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getParentCode,code);
        List<Region> regionList = baseMapper.selectList(wrapper);

        //判断是否有下一层数据，如果有hasChildren=true，否则false
        if(!CollectionUtils.isEmpty(regionList)) {
            //1 把regionList遍历，得到每个Region
            regionList.forEach(region -> {
                //2 查询每个Region对象是否有下一层数据parent_code=?，
                // 如果有设置hasChildren=true，否则false
                LambdaQueryWrapper<Region> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(Region::getParentCode,region.getCode());
                Long count = baseMapper.selectCount(wrapper1);
                if(count > 0) {
                    //有下一层数据
                    region.setHasChildren(true);
                } else {
                    region.setHasChildren(false);
                }
            });
        }
        return regionList;
    }

    //根据编号返回对应名称
    @Override
    public String getNameByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        Region region = baseMapper.selectOne(new LambdaQueryWrapper<Region>()
                .eq(Region::getCode,code).select(Region::getName));
        if(null != region) {
            return region.getName();
        }
        return "";
    }
}
