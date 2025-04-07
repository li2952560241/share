package com.share.rules.controller;

import com.alibaba.fastjson.JSON;
import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.core.web.page.TableDataInfo;
import com.share.rules.domain.FeeRule;
import com.share.rules.service.IFeeRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "费用规则接口管理")
@RestController
@RequestMapping("/feeRule")
public class FeeRuleController extends BaseController
{
    @Autowired
    private IFeeRuleService feeRuleService;

    /**
     * 查询费用规则列表
     */
    @Operation(summary = "查询费用规则列表")
    @GetMapping("/list")
    public TableDataInfo list(FeeRule feeRule)
    {
        startPage();
        List<FeeRule> list = feeRuleService.selectFeeRuleList(feeRule);
        return getDataTable(list);
    }

    /**
     * 获取费用规则详细信息
     */
    @Operation(summary = "获取费用规则详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(feeRuleService.getById(id));
    }

    /**
     * 新增费用规则
     */
    @Operation(summary = "新增费用规则")
    @PostMapping
    public AjaxResult add(@RequestBody FeeRule feeRule)
    {
        return toAjax(feeRuleService.save(feeRule));
    }

    /**
     * 修改费用规则
     */
    @Operation(summary = "修改费用规则")
    @PutMapping
    public AjaxResult edit(@RequestBody FeeRule feeRule)
    {
        //return toAjax(feeRuleService.updateById(feeRule));
        System.out.println(JSON.toJSONString(feeRule));
        return toAjax(1);
    }

    /**
     * 删除费用规则
     */
    @Operation(summary = "删除费用规则")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(feeRuleService.removeBatchByIds(Arrays.asList(ids)));
    }

    @Operation(summary = "获取全部费用规则")
    @GetMapping("/getALLFeeRuleList")
    public AjaxResult getALLFeeRuleList()
    {
        return success(feeRuleService.getALLFeeRuleList());
    }

}
