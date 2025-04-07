package com.share.rules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.share.rules.domain.FeeRule;

import java.util.List;

public interface FeeRuleMapper extends BaseMapper<FeeRule> {

    public List<FeeRule> selectFeeRuleList(FeeRule feeRule);

}
