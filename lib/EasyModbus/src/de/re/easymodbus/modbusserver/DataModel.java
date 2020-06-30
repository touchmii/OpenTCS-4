package de.re.easymodbus.modbusserver;

import de.re.easymodbus.mqtt.EasyModbus2Mqtt;

class DataModel
{
	EasyModbus2Mqtt easyModbus2Mqtt = new EasyModbus2Mqtt();
    private int[] holdingRegisters = new int[65535];
    private int[] inputRegisters = new int[65535];
    private boolean[] coils = new boolean[65535];
    private boolean[] discreteInputs = new boolean[65535];
    
    private int[] mqttHoldingRegistersOldValues = new int[65535];
    private int[] mqttInputRegistersOldValues = new int[65535];
    private boolean[] mqttCoilsOldValues = new boolean[65535];
    private boolean[] mqttDiscreteInputsOldValues = new boolean[65535];
    
    public void setHoldingRegister(int i, int value)
    {
    	holdingRegisters[i] = value;
    	
    	if ((easyModbus2Mqtt.getMqttBrokerAddress() != null) && (holdingRegisters[i] != mqttHoldingRegistersOldValues[i]))
    	{
    		new ProcessMQTTThread (easyModbus2Mqtt, easyModbus2Mqtt.getMqttRootTopic() + "/holdingregisters" + Integer.toString(i), Integer.toString(value));
    		mqttHoldingRegistersOldValues[i] = value;
    	}
    }
    
    public void setInputRegister(int i, int value)
    {
    	inputRegisters[i] = value;
    	
    	if ((easyModbus2Mqtt.getMqttBrokerAddress() != null) && (inputRegisters[i] != mqttInputRegistersOldValues[i]))
    	{
    		new ProcessMQTTThread (easyModbus2Mqtt, easyModbus2Mqtt.getMqttRootTopic() + "/inputregisters" + Integer.toString(i), Integer.toString(value));
    		mqttInputRegistersOldValues[i] = value;
    	}
    }
    
    public void setCoil(int i, boolean value)
    {
    	coils[i] = value;
    	
    	if ((easyModbus2Mqtt.getMqttBrokerAddress() != null) && (coils[i] != mqttCoilsOldValues[i]))
    	{
    		new ProcessMQTTThread (easyModbus2Mqtt, easyModbus2Mqtt.getMqttRootTopic() + "/coils" + Integer.toString(i), Boolean.toString(value));
    		mqttCoilsOldValues[i] = value;
    	}
    }
    
    public void setDiscreteInput(int i, boolean value)
    {
    	discreteInputs[i] = value;
    	
    	if ((easyModbus2Mqtt.getMqttBrokerAddress() != null) && (discreteInputs[i] != mqttDiscreteInputsOldValues[i]))
    	{
    		new ProcessMQTTThread (easyModbus2Mqtt, easyModbus2Mqtt.getMqttRootTopic() + "/discreteinputs" + Integer.toString(i), Boolean.toString(value));
    		mqttDiscreteInputsOldValues[i] = value;
    	}
    }
    
    public int getHoldingRegister(int i)
    {
    	return holdingRegisters[i];
    }
    
    public int getInputRegister(int i)
    {
    	return inputRegisters[i];
    }
    
    public boolean getCoil(int i)
    {
    	return coils[i];
    }
    
    public boolean getDiscreteInput(int i)
    {
    	return discreteInputs[i];
    }
   
}
