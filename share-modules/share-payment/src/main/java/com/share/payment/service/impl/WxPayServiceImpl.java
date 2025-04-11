package com.share.payment.service.impl;

import com.share.common.core.domain.R;
import com.share.common.core.exception.ServiceException;
import com.share.payment.config.WxPayV3Properties;
import com.share.payment.domain.CreateWxPaymentForm;
import com.share.payment.domain.PaymentInfo;
import com.share.payment.domain.WxPrepayVo;
import com.share.payment.service.IWxPayService;
import com.share.payment.service.PaymentService;
import com.share.payment.utils.RequestUtils;
import com.share.user.api.RemoteUserService;
import com.share.user.domain.UserInfo;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WxPayServiceImpl implements IWxPayService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RSAAutoCertificateConfig rsaAutoCertificateConfig;

    @Autowired
    private WxPayV3Properties wxPayV3Properties;

    @Autowired
    private RemoteUserService remoteUserService;


    //微信下单
    @Override
    public WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm) {
        //1 添加微信支付记录（状态：未支付）  payment_info
        PaymentInfo paymentInfo =
                paymentService.savePaymentInfo(createWxPaymentForm.getOrderNo());

        //2 创建微信封装对象 JsapiServiceExtension

        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();

        //3 封装这个对象需要相关参数
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        //amount.setTotal(paymentInfo.getAmount().multiply(new BigDecimal(100)).intValue());
        //为了测试，支付0.01
        amount.setTotal(1);
        request.setAmount(amount);

        request.setAppid(wxPayV3Properties.getAppid());
        request.setMchid(wxPayV3Properties.getMerchantId());
        request.setDescription(paymentInfo.getContent());
        request.setNotifyUrl(wxPayV3Properties.getNotifyUrl());
        request.setOutTradeNo(paymentInfo.getOrderNo());

        //获取用户信息
        Payer payer = new Payer();
        //远程调用
        R<UserInfo> userInfoR = remoteUserService.getInfo(paymentInfo.getUserId());
        UserInfo userInfo = userInfoR.getData();
        if(userInfo == null) {
            throw new ServiceException("用户不存在");
        }
        String openid = userInfo.getWxOpenId();
        payer.setOpenid(openid);
        request.setPayer(payer);

        //4 调用第二步创建对象里面的方法，发起微信支付
        PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);

        //5 按照要求返回需要数据WxPrepayVo
        WxPrepayVo wxPrepayVo = new WxPrepayVo();
        BeanUtils.copyProperties(response,wxPrepayVo);
        wxPrepayVo.setTimeStamp(response.getTimeStamp());

        return wxPrepayVo;
    }

    @Override
    public void wxnotify(HttpServletRequest request) {
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String signature = request.getHeader("Wechatpay-Signature");
        String requestBody = RequestUtils.readData(request);

        //2.构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .body(requestBody)
                .build();


        //3.初始化 NotificationParser
        NotificationParser parser = new NotificationParser(rsaAutoCertificateConfig);
        //4.以支付通知回调为例，验签、解密并转换成 Transaction
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        if(null != transaction &&
                transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
            //5.处理支付业务
            paymentService.updatePaymentStatus(transaction);
        }
    }

    //根据订单编号调用微信服务接口，查询支付相关状态信息
    @Override
    public Transaction queryPayStatus(String orderNo) {
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(wxPayV3Properties.getMerchantId());
        queryRequest.setOutTradeNo(orderNo);
        Transaction transaction = service.queryOrderByOutTradeNo(queryRequest);
        return transaction;
    }
}
