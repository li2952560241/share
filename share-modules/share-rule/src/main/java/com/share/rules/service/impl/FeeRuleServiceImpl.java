package com.share.rules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.rule.domain.FeeRule;
import com.share.rules.mapper.FeeRuleMapper;
import com.share.rules.service.IFeeRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FeeRuleServiceImpl extends ServiceImpl<FeeRuleMapper, FeeRule> implements IFeeRuleService
{
    @Autowired
    private FeeRuleMapper feeRuleMapper;


    @Override
    public List<FeeRule> selectFeeRuleList(FeeRule feeRule)
    {
        return feeRuleMapper.selectFeeRuleList(feeRule);
    }

    @Override
    public List<FeeRule> getALLFeeRuleList() {
        return feeRuleMapper.selectList(new LambdaQueryWrapper<FeeRule>().eq(FeeRule::getStatus, "1"));
    }

}