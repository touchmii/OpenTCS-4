package de.re.easymodbus.mqtt;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class EasyModbus2Mqtt extends Thread
{

    String mqttBrokerAddress = "www.mqtt-dashboard.com";
	int mqttBrokerPort = 1883;
	String mqttRootTopic = "easymodbusclient";
	MqttClient mqttClient;
    String mqttUserName;
    String mqttPassword;
    boolean retainMessages;


    public EasyModbus2Mqtt()
    {       
    }
    
 
    String MqttBrokerAddressPublish = "";
    public void publish(String[] topic, String[] payload, String mqttBrokerAddress) throws MqttPersistenceException, MqttException
    {
        if (mqttClient != null)
            if (!mqttBrokerAddress.equals(this.MqttBrokerAddressPublish) & mqttClient.isConnected())
                mqttClient.disconnect();
        if (topic.length != payload.length)
            throw new IllegalArgumentException("Array topic and payload must be the same size");
        String clientID = MqttClient.generateClientId();
        mqttClient = new MqttClient("tcp://"+mqttBrokerAddress + ":"+mqttBrokerPort, clientID);
        
        if (!mqttClient.isConnected())
        {
            if (this.mqttUserName == null || this.mqttPassword == null)
                mqttClient.connect();
            else
            {
            	MqttConnectOptions options = new MqttConnectOptions();
            	//Setzen einer Persistent Session
            	options.setCleanSession(true);
            	options.setUserName(mqttUserName);
            	options.setPassword(mqttPassword.toCharArray());
            	mqttClient.connect(options);
            }
        }
            
        for (int i = 0; i < payload.length; i++)
            mqttClient.publish(topic[i], (payload[i]).getBytes(), 0, this.retainMessages);
     
    }

    public void publish(String topic, String payload, String mqttBrokerAddress) throws MqttSecurityException, MqttException
    {
        if (mqttClient != null)
            if (!mqttBrokerAddress.equals(this.MqttBrokerAddressPublish) & mqttClient.isConnected())
                mqttClient.disconnect();
        String clientID = MqttClient.generateClientId();
        mqttClient = new MqttClient("tcp://"+mqttBrokerAddress + ":"+mqttBrokerPort, clientID);
       
        if (!mqttClient.isConnected())
        {
            if (this.mqttUserName == null || this.mqttPassword == null)
                mqttClient.connect();
            else
            {
            	MqttConnectOptions options = new MqttConnectOptions();
            	//Setzen einer Persistent Session
            	options.setCleanSession(true);
            	options.setUserName(mqttUserName);
            	options.setPassword(mqttPassword.toCharArray());
            	mqttClient.connect(options);
            }
        }

            if (payload != null)
                mqttClient.publish(topic, (payload).getBytes(), 0, this.retainMessages);
            else
                mqttClient.publish(topic, new byte[0], 0, this.retainMessages);
    }

    public void Disconnect() throws MqttException
    {
        mqttClient.disconnect();
        mqttClient.close();
    }
    
    public boolean isConnected()
    {
    	return mqttClient.isConnected();
    }
    
    
    public String getMqttBrokerAddress() {
		return mqttBrokerAddress;
	}

	public void setMqttBrokerAddress(String mqttBrokerAddress) {
		this.mqttBrokerAddress = mqttBrokerAddress;
	}
	
    public int getMqttBrokerPort() {
		return mqttBrokerPort;
	}

	public void setMqttBrokerPort(int mqttBrokerPort) {
		this.mqttBrokerPort = mqttBrokerPort;
	}
	
    public String getMqttRootTopic() {
		return mqttRootTopic;
	}

	public void setMqttRootTopic(String mqttRootTopic) {
		this.mqttRootTopic = mqttRootTopic;
	}


	public String getMqttUserName() {
		return mqttUserName;
	}

	public void setMqttUserName(String mqttUserName) {
		this.mqttUserName = mqttUserName;
	}

	public String getMqttPassword() {
		return mqttPassword;
	}

	public void setMqttPassword(String mqttPassword) {
		this.mqttPassword = mqttPassword;
	}

	public boolean isRetainMessages() {
		return retainMessages;
	}

	public void setRetainMessages(boolean retainMessages) {
		this.retainMessages = retainMessages;
	}

}
