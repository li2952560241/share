package com.share.device.emqx.constant;

/**
 * Emqx常量信息
 *
 */
public class EmqxConstants {


    //充电宝插入，订阅设备端Topic， 服务器监听
    public final static String TOPIC_POWERBANK_CONNECTED = "/sys/powerBank/connected";

    /** 扫码提交指令Topic，柜机监听  */
    public final static String TOPIC_SCAN_SUBMIT = "/sys/scan/submit/%s";

    /** 柜机解锁充电宝，服务器监听  */
    public final static String TOPIC_POWERBANK_UNLOCK = "/sys/powerBank/unlock";

    /** 柜机属性上报，服务器监听  */
    public final static String TOPIC_PROPERTY_POST = "/sys/property/post";
}
