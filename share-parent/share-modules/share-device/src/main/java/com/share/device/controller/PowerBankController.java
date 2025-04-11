package com.share.device.controller;

import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.core.web.page.TableDataInfo;
import com.share.common.security.utils.SecurityUtils;
import com.share.device.domain.PowerBank;
import com.share.device.service.IPowerBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Tag(name = "充电宝接口管理")
@RestController
@RequestMapping("/powerBank")
public class PowerBankController extends BaseController {

    @Autowired
    private IPowerBankService powerBankService;

    //分页查询
    @Operation(summary = "查询充电宝列表")
    @GetMapping("/list")
    public TableDataInfo list(PowerBank powerBank) {
        //设置分页参数
        startPage();
        //调用service
        List<PowerBank> list = powerBankService.selectListPowerBank(powerBank);
        return getDataTable(list);
    }

    //根据id查询详情数据
    @Operation(summary = "获取充电宝详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(powerBankService.getById(id));
    }

    //添加
    @Operation(summary = "添加")
    @PostMapping
    public AjaxResult add(@RequestBody PowerBank powerBank) {
        //1 设置相关数据值
        powerBank.setCreateBy(SecurityUtils.getUsername()); //添加人 当前后台系统登录人名称
        powerBank.setCreateTime(new Date());
        powerBank.setUpdateTime(new Date());

        //2 调用service的方法实现添加
        int rows = powerBankService.savePowerBank(powerBank);
        return toAjax(rows);
    }

    //修改
    @Operation(summary = "修改")
    @PutMapping
    public AjaxResult update(@RequestBody PowerBank powerBank) {
        //设置相关数据
        powerBank.setUpdateBy(SecurityUtils.getUsername());
        powerBank.setUpdateTime(new Date());
        //调用service的方法实现
        int rows = powerBankService.updatePowerBank(powerBank);
        return toAjax(rows);
    }

    //删除
    @Operation(summary = "删除充电宝")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(powerBankService.removeBatchByIds(Arrays.asList(ids)));
    }
}
