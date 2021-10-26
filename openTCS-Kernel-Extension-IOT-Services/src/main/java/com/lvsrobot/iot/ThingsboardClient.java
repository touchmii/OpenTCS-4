package com.lvsrobot.iot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class ThingsboardClient {
    String ctopic        = "v1/devices/me/rpc/request/+";
    String topic        = "v1/devices/me/telemetry";
    String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker       = "tcp://119.23.228.81:1884";
    String clientId     = "AodonG2";
    String userId = "VKH83BjOY9lUpb2h6Pvw";
    MemoryPersistence persistence = new MemoryPersistence();
//    MqttConnectOptions connectOptions = new MqttConnectOptions();

    MqttClient sampleClient;


    public void ThingsboardClient() {

    }

    public void connect() {
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(userId);
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String str) {
        MqttMessage message = new MqttMessage(String.format("hello").getBytes(StandardCharsets.UTF_8));
        message.setQos(2);
        try {
            sampleClient.publish(topic, message);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }
}
