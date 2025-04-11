package com.share.payment.api;

import com.share.common.core.web.controller.BaseController;
import com.share.common.core.web.domain.AjaxResult;
import com.share.common.security.annotation.RequiresLogin;
import com.share.payment.domain.CreateWxPaymentForm;
import com.share.payment.domain.WxPrepayVo;
import com.share.payment.service.IWxPayService;
import com.share.payment.service.PaymentService;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "微信支付接口")
@RestController
@RequestMapping("/wxPay")
public class WxPayApiController extends BaseController {

    @Autowired
    private IWxPayService wxPayService;

    @Autowired
    private PaymentService paymentService;

    @RequiresLogin
    @Operation(summary = "支付状态查询")
    @GetMapping("/queryPayStatus/{orderNo}")
    public AjaxResult queryPayStatus(@PathVariable String orderNo) {
        //根据订单编号调用微信服务接口，查询支付相关状态信息
        Transaction transaction = wxPayService.queryPayStatus(orderNo);
        //根据微信服务返回结果，判断支付是否成功
        if(transaction != null
                && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
            paymentService.updatePaymentStatus(transaction);
            return success(true);
        }
        return success(false);
    }

    @Operation(summary = "微信支付异步通知接口")
    @PostMapping("/notify")
    public Map<String, Object> notify(HttpServletRequest request) {
        try {
            wxPayService.wxnotify(request);

            //返回成功
            Map<String, Object> result = new HashMap<>();
            result.put("code", "SUCCESS");
            result.put("message", "成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回失败
        Map<String, Object> result = new HashMap<>();
        result.put("code", "FAIL");
        result.put("message", "失败");
        return result;
    }

    /**
     *  微信下单
     * @param createWxPaymentForm
     * @return
     */

    @RequiresLogin
    @Operation(summary = "微信下单")
    @PostMapping("/createWxPayment")
    public AjaxResult createWxPayment(@RequestBody CreateWxPaymentForm createWxPaymentForm) {
        WxPrepayVo wxPrepayVo =
                wxPayService.createWxPayment(createWxPaymentForm);
        return success(wxPrepayVo);
    }
}
