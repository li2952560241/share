package com.share.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.payment.domain.PaymentInfo;
import com.wechat.pay.java.service.payments.model.Transaction;

public interface PaymentService extends IService<PaymentInfo> {
    // 保存支付信息
    PaymentInfo savePaymentInfo(String orderNo);

    void updatePaymentStatus(Transaction transaction);
}
