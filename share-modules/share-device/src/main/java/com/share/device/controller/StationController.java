package com.share.device.controller;

import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.core.web.page.TableDataInfo;
import com.share.device.domain.Station;
import com.share.device.service.IStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "站点接口管理")
@RestController
@RequestMapping("/station")
public class StationController extends BaseController {

    @Autowired
    private IStationService stationService;

    /**
     *  查询站点列表 （分页）
     * @param station 站点信息
     * @return 站点列表
     */
    @Operation(summary = "查询站点列表")
    @GetMapping("/list")
    public TableDataInfo list(Station station) {
        //设置分页参数
        startPage();
        //查询
        List<Station> list = stationService.selectStationList(station);
        TableDataInfo tableDataInfo = getDataTable(list);
        return tableDataInfo;
    }

    /**
     * 新增站点
     * @param station 站点信息
     * @return 返回影响的行数 即是否成功
     */
    @Operation(summary = "新增站点")
    @PostMapping
    public AjaxResult add(@RequestBody Station station) {
        return toAjax(stationService.saveStation(station));
    }

    /**
     * 修改站点
     * @param station 站点信息
     * @return 返回影响的行数 即是否成功
     */
    @Operation(summary = "修改站点")
    @PutMapping
    public AjaxResult edit(@RequestBody Station station) {
        return toAjax(stationService.updateStation(station));
    }

    /**
     * 获取站点详细信息
     * @param id 站点id
     * @return 站点信息
     */
    @Operation(summary = "获取站点详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stationService.getById(id));
    }

    /**
     * 删除站点
     * @param ids 站点id列表
     * @return boolean 是否成功
     */
    @Operation(summary = "删除站点")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stationService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 设置站点关联数据
     * @param station 站点信息
     * @return
     */
    @Operation(summary = "站点设置关联数据")
    @PostMapping("/setData")
    public AjaxResult setData(@RequestBody Station station)
    {
        return toAjax(stationService.setData(station));
    }

    /**
     * 更新初始化数据
     * @return 将数据库数据更新到MongoDB 站点
     */
    @Operation(summary = "更新初始化数据")
    @GetMapping("/updateData")
    public AjaxResult updateData()
    {
        stationService.updateData();
        return success();
    }
}
