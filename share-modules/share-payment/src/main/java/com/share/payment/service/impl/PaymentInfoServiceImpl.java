package com.share.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.common.core.domain.R;
import com.share.common.rabbit.constant.MqConst;
import com.share.common.rabbit.service.RabbitService;
import com.share.order.api.RemoteOrderInfoService;
import com.share.order.domain.OrderInfo;
import com.share.payment.domain.PaymentInfo;
import com.share.payment.mapper.PaymentInfoMapper;
import com.share.payment.service.PaymentService;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        // 根据订单编号查询支付记录
        PaymentInfo paymentInfo =
                baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                        .eq(PaymentInfo::getOrderNo, orderNo));

        //2 如果支付记录不存在，进行添加
        if(paymentInfo == null) {
            //3 远程调用：根据订单编号查询订单数据
            R<OrderInfo> result = remoteOrderInfoService.getByOrderNo(orderNo);
            OrderInfo orderInfo = result.getData();

            //添加数据
            paymentInfo = new PaymentInfo();
            paymentInfo.setUserId(orderInfo.getUserId());
            paymentInfo.setContent("共享充电宝租借");
            paymentInfo.setAmount(orderInfo.getTotalAmount());
            paymentInfo.setOrderNo(orderNo);
            paymentInfo.setPaymentStatus(0);
            baseMapper.insert(paymentInfo);
        }
        return paymentInfo;
    }

    //支付成功后，后续处理
    @Override
    public void updatePaymentStatus(Transaction transaction) {
        //1 更新支付记录状态：已经支付
        String outTradeNo = transaction.getOutTradeNo();
        //根据订单编号查询支付记录
        PaymentInfo paymentInfo =
                baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, outTradeNo));

        //判断如果支付记录状态已经修改了，不需要操作
        Integer paymentStatus = paymentInfo.getPaymentStatus();
        if(paymentStatus.intValue()==1) { //已经修改状态
            return;
        }

        //没有修改，再进行修改
        paymentInfo.setPaymentStatus(1); //修改为已经支付
        paymentInfo.setOrderNo(transaction.getOutTradeNo());
        paymentInfo.setTransactionId(transaction.getTransactionId());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(com.alibaba.fastjson.JSON.toJSONString(transaction));
        baseMapper.updateById(paymentInfo);

        //2 基于MQ通知订单系统，修改订单状态
        rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY,
                                  MqConst.ROUTING_PAYMENT_PAY,
                                  paymentInfo.getOrderNo());
    }
}
