package de.re.easymodbus.modbusserver;

import org.eclipse.paho.client.mqttv3.MqttException;

import de.re.easymodbus.mqtt.*;

public class ProcessMQTTThread extends Thread 
{
	private EasyModbus2Mqtt easyModbus2Mqtt;
	private String topic, payload;
	public ProcessMQTTThread(EasyModbus2Mqtt easyModbus2Mqtt, String topic, String payload)
	{
		this.easyModbus2Mqtt = easyModbus2Mqtt;
		this.topic = topic;
		this.payload = payload;
		
		this.start();
	}
	
	public void run()
	{
		synchronized(easyModbus2Mqtt)
		{
		try {
			easyModbus2Mqtt.publish(topic, payload, easyModbus2Mqtt.getMqttBrokerAddress());
		} catch (MqttException e) {
			e.printStackTrace();
		}	
		}
	}
	
}
