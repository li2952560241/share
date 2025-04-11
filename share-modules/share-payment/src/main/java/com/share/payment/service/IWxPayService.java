package com.share.payment.service;

import com.share.payment.domain.CreateWxPaymentForm;
import com.share.payment.domain.WxPrepayVo;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;

public interface IWxPayService {
    // 微信下单
    WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm);

    void wxnotify(HttpServletRequest request);

    //根据订单编号调用微信服务接口，查询支付相关状态信息
    Transaction queryPayStatus(String orderNo);
}
