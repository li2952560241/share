package com.share.payment.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateWxPaymentForm {

    @Schema(description = "订单号")
    private String orderNo;

}
