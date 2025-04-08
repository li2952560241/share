package com.share.rules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.rule.domain.FeeRule;

import java.util.List;

public interface IFeeRuleService extends IService<FeeRule> {


    public List<FeeRule> selectFeeRuleList(FeeRule feeRule);

    List<FeeRule> getALLFeeRuleList();

}
