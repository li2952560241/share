package com.share.device.emqx.factory;


import com.share.device.emqx.handler.MassageHandler;

/**
 * 平台消息工厂
 */
public interface MessageHandlerFactory {

    MassageHandler getMassageHandler(String topic);
}
