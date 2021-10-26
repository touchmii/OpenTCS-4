package com.lvsrobot.iot;

/**
 * Copyright © 2016 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author Andrew Shvayka
 */
public class SampleMqttClient {



    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(SampleMqttClient.class);

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getClientId() {
        return clientId;
    }

    private final String deviceToken;
    private final String deviceName;
    private final String clientId;
    private final MqttClientPersistence persistence;
    private final MqttAsyncClient client;

    public SampleMqttClient(String uri, String deviceName, String deviceToken) throws Exception {
        this.clientId = MqttAsyncClient.generateClientId();
        this.deviceToken = deviceToken;
        this.deviceName = deviceName;
        this.persistence = new MemoryPersistence();
        this.client = new MqttAsyncClient(uri, clientId, persistence);
    }

    public boolean subscribe(String topic, MqttCallback callback) {
        try {
            client.subscribe(topic, 1);
            client.setCallback(callback);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean connect() throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(deviceToken);
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    LOG.info("[{}] connected to Thingsboard!", deviceName);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable e) {
                    LOG.error("[{}] failed to connect to Thingsboard!", deviceName, e.getMessage());
                }
            }).waitForCompletion();
        } catch (MqttException e) {
            LOG.error("Failed to connect to the server", e.getMessage());
        }
        return client.isConnected();
    }

    public void disconnect() throws Exception {
        client.disconnect().waitForCompletion();
    }

    public void publishAttributes(JsonNode data) throws Exception {
        publish("v1/devices/me/attributes", data, true);
    }

    public void publishTelemetry(JsonNode data) throws Exception {
        publish("v1/devices/me/telemetry", data, false);
    }

    public void publishResponse(MqttMessage msg, String requestId) throws Exception {
//        publish("v1/devices/me/rpc/response"+requestId, data, false);
        IMqttDeliveryToken deliveryToken = client.publish("v1/devices/me/rpc/response"+requestId, msg, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                LOG.trace("Data updated!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                LOG.error("[{}] Data update failed!", deviceName, exception);
            }
        });
//        if (sync) {
            deliveryToken.waitForCompletion();
//        }
    }

    private void publish(String topic, JsonNode data, boolean sync) throws Exception {
        MqttMessage msg = new MqttMessage(MAPPER.writeValueAsString(data).getBytes(StandardCharsets.UTF_8));
        IMqttDeliveryToken deliveryToken = client.publish(topic, msg, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                LOG.trace("Data updated!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                LOG.error("[{}] Data update failed!", deviceName, exception.getMessage());
            }
        });
        if (sync) {
            deliveryToken.waitForCompletion();
        }
    }
}
