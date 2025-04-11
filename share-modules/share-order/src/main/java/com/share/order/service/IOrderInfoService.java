package com.share.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.order.domain.EndOrderVo;
import com.share.order.domain.OrderInfo;
import com.share.order.domain.SubmitOrderVo;

import java.util.List;
import java.util.Map;

/**
 * 订单Service接口
 *
 * @author atguigu
 * @date 2024-02-22
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    //获取未完成订单
    OrderInfo getNoFinishOrder(Long userId);

    //提交订单 返回订单id
    Long saveOrder(SubmitOrderVo submitOrderVo);

    // 结束订单
    void endOrder(EndOrderVo endOrderVo);

    // 根据用户id查询订单列表
    List<OrderInfo> selectOrderListByUserId(Long userId);

    // 根据订单id查询订单信息
    OrderInfo selectOrderInfoById(Long id);

    OrderInfo getByOrderNo(String orderNo);

    void processPaySucess(String orderNo);

    //传递过来sql语句，根据sql语句查询数据库得到报表数据
    Map<String, Object> getOrderCount(String sql);
}
