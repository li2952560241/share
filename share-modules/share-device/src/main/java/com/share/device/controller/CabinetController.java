package com.share.device.controller;

import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.core.web.page.TableDataInfo;
import com.share.device.domain.Cabinet;
import com.share.device.service.ICabinetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "充电宝柜机接口管理")
@RestController
@RequestMapping("/cabinet")
public class CabinetController extends BaseController {

    @Autowired
    private ICabinetService cabinetService;

    /**
     * 查询充电宝柜机列表 (分页)
     * @param cabinet 充电宝柜机
     * @return 充电宝柜机集合
     */
    @Operation(summary = "查询充电宝柜机列表")
    @GetMapping("/list")
    public TableDataInfo list(Cabinet cabinet) {
        //设置分页参数
        startPage();
        //调用service方法
        List<Cabinet> list = cabinetService.selectListCabinet(cabinet);
        TableDataInfo dataTable = getDataTable(list);
        return dataTable;
    }

    /**
     * 删除充电宝柜机
     * @param ids
     * @return
     */
    @Operation(summary = "删除")
    @DeleteMapping("{ids}")
    public AjaxResult delete(@PathVariable Long[] ids) {
        boolean flag = cabinetService.removeBatchByIds(Arrays.asList(ids));
        AjaxResult ajaxResult = toAjax(flag);
        return ajaxResult;
    }

    /**
     * 修改充电宝柜机
     * @param cabinet
     * @return
     */
    @Operation(summary = "修改")
    @PutMapping
    public AjaxResult update(@RequestBody Cabinet cabinet) {
        boolean flag = cabinetService.updateById(cabinet);
        AjaxResult ajaxResult = toAjax(flag);
        return ajaxResult;
    }

    /**
     * 添加充电宝柜机
     * @param cabinet
     * @return
     */
    @Operation(summary = "添加")
    @PostMapping
    public AjaxResult add(@RequestBody Cabinet cabinet) {
        boolean flag = cabinetService.save(cabinet);
        AjaxResult ajaxResult = toAjax(flag);
        return ajaxResult;
    }

    /**
     * 根据id查询详情  充电宝柜机
     * @param id
     * @return
     */
    @Operation(summary = "根据id查询详情")
    @GetMapping("{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Cabinet cabinet = cabinetService.getById(id);
        AjaxResult ajaxResult = success(cabinet);
        return ajaxResult;
    }

    /**
     * 获取充电宝柜机全部详细信息 包括所有电宝
     * @param id
     * @return
     */
    @Operation(summary = "获取充电宝柜机全部详细信息")
    @GetMapping(value = "/getAllInfo/{id}")
    public AjaxResult getAllInfo(@PathVariable("id") Long id)
    {
        return success(cabinetService.getAllInfo(id));
    }

    /**
     * 搜索未使用柜机
     * @param keyword 关键字
     * @return 充电宝柜机集合
     */
    @Operation(summary = "搜索未使用柜机")
    @GetMapping(value = "/searchNoUseList/{keyword}")
    public AjaxResult searchNoUseList(@PathVariable String keyword)
    {
        return success(cabinetService.searchNoUseList(keyword));
    }


}
