package com.share.device.emqx;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttDemo {

    public static void main(String[] args) {
        String subTopic = "testtopic/#";
        String pubTopic = "testtopic/1";
        String content = "Hello World";
        int qos = 2;
        String broker = "tcp://192.168.23.129:1883";
        String clientId = "emqx_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            //创建mqtt客户端
            MqttClient client = new MqttClient(broker,clientId,persistence);

            //连接emqx
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName("emqx_test");
            connectOptions.setPassword("emqx_test_password".toCharArray());
            // 保留会话
            connectOptions.setCleanSession(true);

            //进行连接
            System.out.println("Connecting to broker: " + broker);
            client.connect(connectOptions);

            System.out.println("Connected");
            System.out.println("Publishing message: " + content);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // 连接丢失后，一般在这里面进行重连
                    System.out.println("连接断开，可以做重连");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // subscribe后得到的消息会执行到这里面
                    System.out.println("接收消息主题:" + topic);
                    System.out.println("接收消息Qos:" + message.getQos());
                    System.out.println("接收消息内容:" + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("deliveryComplete---------" + token.isComplete());
                }
            });

            //订阅topic
            client.subscribe(subTopic);

            //发送消息
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);
            client.publish(pubTopic,message);
            System.out.println("Message published");

            client.disconnect();
            client.close();
            System.exit(0);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }


    }
}
