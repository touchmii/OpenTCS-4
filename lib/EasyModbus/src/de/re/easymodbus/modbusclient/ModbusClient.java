/*
 * (c) Stefan Roﬂmann
 *	This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.re.easymodbus.modbusclient;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;
import java.util.*;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.InputStream;
import jssc.*;
import de.re.easymodbus.datatypes.*;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.mqtt.EasyModbus2Mqtt;


     /**
     * @author Stefan Ro√ümann
     */
public class ModbusClient 
{
	private Socket tcpClientSocket = new Socket();
	protected String ipAddress = "190.201.100.100";
	protected int port = 502;
	private byte [] transactionIdentifier = new byte[2];
	private byte [] protocolIdentifier = new byte[2];
	private byte [] length = new byte[2];
	private byte[] crc = new byte[2];
	private byte unitIdentifier = 1;
	private byte functionCode;
	private byte [] startingAddress = new byte[2];
	private byte [] quantity = new byte[2];
	private boolean udpFlag = false;
    private boolean serialflag = false;
	private int connectTimeout = 1000;
	private InputStream inStream;
	private DataOutputStream outStream;
    public byte[] receiveData;
    public byte[] sendData;  
	private List<ReceiveDataChangedListener> receiveDataChangedListener = new ArrayList<ReceiveDataChangedListener>();
	private List<SendDataChangedListener> sendDataChangedListener = new ArrayList<SendDataChangedListener>();
	private SerialPort serialPort;
	private String mqttRootTopic = "easymodbusclient";
	private String mqttUserName;
	private String mqttPassword;
	private String comPort;
    private int mqttBrokerPort = 1883;
    private boolean mqttPushOnChange = true;
    private boolean mqttRetainMessages = false;
    private EasyModbus2Mqtt easyModbus2Mqtt;
    private boolean[] mqttCoilsOldValues;
    private boolean[] mqttDiscreteInputsOldValues;
    private int[] mqttInputRegistersOldValues;
    private int[] mqttHoldingRegistersOldValues;
    private int numberOfRetries = 3;				//Number of retries in case of serial connection
    private int baudrate = 9600;
    private Parity parity = Parity.Even;
    private StopBits stopBits = StopBits.One;
    private boolean debug=false;
	
	public ModbusClient(String ipAddress, int port)
	{
		System.out.println("EasyModbus Client Library");
		System.out.println("Copyright (c) Stefan Rossmann Engineering Solutions");
		System.out.println("www.rossmann-engineering.de");
		System.out.println("");
		System.out.println("Creative commons license");
		System.out.println("Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0)");
		if (debug) StoreLogData.getInstance().Store("EasyModbus library initialized for Modbus-TCP, IPAddress: " + ipAddress + ", Port: "+port);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public ModbusClient()
	{
		System.out.println("EasyModbus Client Library");
		System.out.println("Copyright (c) Stefan Rossmann Engineering Solutions");
		System.out.println("www.rossmann-engineering.de");
		System.out.println("");
		System.out.println("Creative commons license");
		System.out.println("Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0)");
		if (debug) StoreLogData.getInstance().Store("EasyModbus library initialized for Modbus-TCP");
	}
	
	public ModbusClient(String serialPort)
	{
		System.out.println("EasyModbus Client Library");
		System.out.println("Copyright (c) Stefan Rossmann Engineering Solutions");
		System.out.println("www.rossmann-engineering.de");
		System.out.println("");
		System.out.println("Creative commons license");
		System.out.println("Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0)");
		if (debug) StoreLogData.getInstance().Store("EasyModbus library initialized for Modbus-RTU, COM-Port: " + serialPort);
		this.comPort = serialPort;	
		this.serialflag = true;
		if (debug) StoreLogData.getInstance().Store("Open Serial Port: " + comPort);
	}
	
        /**
        * Connects to ModbusServer
        * @throws UnknownHostException
        * @throws IOException
        */        
	public void Connect() throws UnknownHostException, IOException
	{
		if (!udpFlag && !this.serialflag)
		{
			
			tcpClientSocket = new Socket(ipAddress, port);
			tcpClientSocket.setSoTimeout(connectTimeout);
			outStream = new DataOutputStream(tcpClientSocket.getOutputStream());
			inStream = tcpClientSocket.getInputStream();
			if (debug) StoreLogData.getInstance().Store("Open TCP-Socket, IP-Address: " + ipAddress + ", Port: " + port);
		}
		if (this.serialflag)
		{
			serialPort = new SerialPort(comPort);
			
		    try {
				serialPort.openPort();


				serialPort.setParams(this.baudrate,
									8,
									this.stopBits.getValue(),
									this.parity.getValue());

				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);	
			} catch (SerialPortException e) {
				
				e.printStackTrace();
			}
		    if (debug) StoreLogData.getInstance().Store("Open Serial Port: " + comPort);
		    
		}
	}
	
        /**
        * Connects to ModbusServer
        * @param ipAddress  IP Address of Modbus Server to connect to
        * @param port   Port Modbus Server listenning (standard 502)
        * @throws UnknownHostException
        * @throws IOException
        */   
	public void Connect(String ipAddress, int port) throws UnknownHostException, IOException
	{
		this.ipAddress = ipAddress;
		this.port = port;
		
		tcpClientSocket = new Socket(ipAddress, port);
		tcpClientSocket.setSoTimeout(connectTimeout);
		outStream = new DataOutputStream(tcpClientSocket.getOutputStream());
		inStream = tcpClientSocket.getInputStream();
		if (debug) StoreLogData.getInstance().Store("Open TCP-Socket, IP-Address: " + ipAddress + ", Port: " + port);
	}
        
        /**
        * Connects to ModbusServer with serial connection
        * @param comPort  used Com-Port
        * @throws UnknownHostException
        * @throws IOException
        */   
	public void Connect(String comPort) throws SerialPortException
	{
		this.serialflag = true;
		serialPort = new SerialPort(comPort);
		
	    serialPort.openPort();

	    serialPort.setParams(this.baudrate,
	                         8,
	                         this.stopBits.getValue(),
	                         this.parity.getValue());

	    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);		
    }         
	
        /**
        * Convert two 16 Bit Registers to 32 Bit real value
        * @param        registers   16 Bit Registers
        * @return       32 bit real value
        */
    public static float ConvertRegistersToFloat(int[] registers) throws IllegalArgumentException
    {
        if (registers.length != 2)
            throw new IllegalArgumentException("Input Array length invalid");
        int highRegister = registers[1];
        int lowRegister = registers[0];
        byte[] highRegisterBytes = toByteArray(highRegister);
        byte[] lowRegisterBytes = toByteArray(lowRegister);
        byte[] floatBytes = {
                                highRegisterBytes[1],
                                highRegisterBytes[0],
                                lowRegisterBytes[1],
                                lowRegisterBytes[0]
                            };
        return ByteBuffer.wrap(floatBytes).getFloat();
    }  
    
    /**
    * Convert two 16 Bit Registers to 64 Bit double value  Reg0: Low Word.....Reg3: High Word
    * @param        registers  16 Bit Registers
    * @return       64 bit double value
    */
    public static double ConvertRegistersToDouble(int[] registers) throws IllegalArgumentException
    {
    	if (registers.length != 4)
    		throw new IllegalArgumentException("Input Array length invalid");
    	byte[] highRegisterBytes = toByteArray(registers[3]);
    	byte[] highLowRegisterBytes = toByteArray(registers[2]); 
    	byte[] lowHighRegisterBytes = toByteArray(registers[1]);
    	byte[] lowRegisterBytes = toByteArray(registers[0]);
    	byte[] doubleBytes = {
                            highRegisterBytes[1],
                            highRegisterBytes[0],
                            highLowRegisterBytes[1],
                            highLowRegisterBytes[0],
                            lowHighRegisterBytes[1],
                            lowHighRegisterBytes[0],
                            lowRegisterBytes[1],
                            lowRegisterBytes[0]
                        };
    	return ByteBuffer.wrap(doubleBytes).getDouble();
    }  
    
    /**
    * Convert two 16 Bit Registers to 64 Bit double value  Order "LowHigh": Reg0: Low Word.....Reg3: High Word, "HighLow": Reg0: High Word.....Reg3: Low Word
    * @param        registers   16 Bit Registers
    * @param        registerOrder High Register first or low Register first 
    * @return       64 bit double value
    */
    public static double ConvertRegistersToDouble(int[] registers, RegisterOrder registerOrder) throws IllegalArgumentException
    {
    	if (registers.length != 4)
    		throw new IllegalArgumentException("Input Array length invalid");
    	int[] swappedRegisters = { registers[0], registers[1], registers[2], registers[3] };
    	if (registerOrder == RegisterOrder.HighLow)
    		swappedRegisters = new int[] { registers[3], registers[2], registers[1], registers[0] };
    	return ConvertRegistersToDouble(swappedRegisters);
    }
   
        /**
        * Convert two 16 Bit Registers to 32 Bit real value 
        * @param        registers   16 Bit Registers
        * @param        registerOrder    High Register first or low Register first 
        * @return       32 bit real value
        */
    public static float ConvertRegistersToFloat(int[] registers, RegisterOrder registerOrder) throws IllegalArgumentException
    {
        int [] swappedRegisters = {registers[0],registers[1]};
        if (registerOrder == RegisterOrder.HighLow) 
            swappedRegisters = new int[] {registers[1],registers[0]};
        return ConvertRegistersToFloat(swappedRegisters);
    }
   
    
    /**
    * Convert four 16 Bit Registers to 64 Bit long value Reg0: Low Word.....Reg3: High Word
    * @param        registers   16 Bit Registers
    * @return       64 bit value
    */
    public static long ConvertRegistersToLong(int[] registers) throws IllegalArgumentException
    {
    	if (registers.length != 4)
    		throw new IllegalArgumentException("Input Array length invalid");
    	byte[] highRegisterBytes = toByteArray(registers[3]);
    	byte[] highLowRegisterBytes = toByteArray(registers[2]); 
    	byte[] lowHighRegisterBytes = toByteArray(registers[1]);
    	byte[] lowRegisterBytes = toByteArray(registers[0]);
    	byte[] longBytes = {
                            highRegisterBytes[1],
                            highRegisterBytes[0],
                            highLowRegisterBytes[1],
                            highLowRegisterBytes[0],
                            lowHighRegisterBytes[1],
                            lowHighRegisterBytes[0],
                            lowRegisterBytes[1],
                            lowRegisterBytes[0]
                        };
    	return ByteBuffer.wrap(longBytes).getLong();
}  	

    /**
     * Convert four 16 Bit Registers to 64 Bit long value Register Order "LowHigh": Reg0: Low Word.....Reg3: High Word, "HighLow": Reg0: High Word.....Reg3: Low Word
     * @param        registers   16 Bit Registers
     * @return       64 bit value
     */
    public static long ConvertRegistersToLong(int[] registers, RegisterOrder registerOrder) throws IllegalArgumentException
    {
    	if (registers.length != 4)
    		throw new IllegalArgumentException("Input Array length invalid");
    	int[] swappedRegisters = { registers[0], registers[1], registers[2], registers[3] };
    	if (registerOrder == RegisterOrder.HighLow)
    		swappedRegisters = new int[] { registers[3], registers[2], registers[1], registers[0] };
    	return ConvertRegistersToLong(swappedRegisters);
    }
    
        /**
        * Convert two 16 Bit Registers to 32 Bit long value
        * @param        registers   16 Bit Registers
        * @return       32 bit value
        */
    public static int ConvertRegistersToInt(int[] registers) throws IllegalArgumentException
    {
        if (registers.length != 2)
            throw new IllegalArgumentException("Input Array length invalid");
        int highRegister = registers[1];
        int lowRegister = registers[0];
        byte[] highRegisterBytes = toByteArray(highRegister);
        byte[] lowRegisterBytes = toByteArray(lowRegister);
        byte[] doubleBytes = {
                                highRegisterBytes[1],
                                highRegisterBytes[0],
                                lowRegisterBytes[1],
                                lowRegisterBytes[0]
                            };
        return ByteBuffer.wrap(doubleBytes).getInt();
    }
    
        /**
        * Convert two 16 Bit Registers to 32 Bit long value
        * @param        registers   16 Bit Registers
        * @param        registerOrder    High Register first or low Register first
        * @return       32 bit value
        */
    public static int ConvertRegistersToInt(int[] registers, RegisterOrder registerOrder) throws IllegalArgumentException
    {
        int[] swappedRegisters = { registers[0], registers[1] };
        if (registerOrder == RegisterOrder.HighLow)
            swappedRegisters = new int[] { registers[1], registers[0] };
        return ConvertRegistersToInt(swappedRegisters);
    }
    
        /**
        * Convert 32 Bit real Value to two 16 Bit Value to send as Modbus Registers
        * @param        floatValue      real to be converted
        * @return       16 Bit Register values
        */
    public static int[] ConvertFloatToRegisters(float floatValue)
    {
        byte[] floatBytes = toByteArray(floatValue);
        byte[] highRegisterBytes = 
        {
        		0,0,
            floatBytes[0],
            floatBytes[1],

        };
        byte[] lowRegisterBytes = 
        {
            0,0,
            floatBytes[2],
            floatBytes[3],

        };
        int[] returnValue =
        {
        		ByteBuffer.wrap(lowRegisterBytes).getInt(),
        		ByteBuffer.wrap(highRegisterBytes).getInt()
        };
        return returnValue;
    }
    
        /**
        * Convert 32 Bit real Value to two 16 Bit Value to send as Modbus Registers
        * @param        floatValue      real to be converted
        * @param        registerOrder    High Register first or low Register first
        * @return       16 Bit Register values
        */
    public static int[] ConvertFloatToRegisters(float floatValue, RegisterOrder registerOrder)
    {
        int[] registerValues = ConvertFloatToRegisters(floatValue);
        int[] returnValue = registerValues;
        if (registerOrder == RegisterOrder.HighLow)
            returnValue = new int[] { registerValues[1], registerValues[0] };
        return returnValue;
    }
    
        /**
        * Convert 32 Bit Value to two 16 Bit Value to send as Modbus Registers
        * @param        intValue      Value to be converted
        * @return       16 Bit Register values
        */
    public static int[] ConvertIntToRegisters(int intValue)
    {
        byte[] doubleBytes = toByteArrayInt(intValue);
        byte[] highRegisterBytes = 
        {
        		0,0,
            doubleBytes[0],
            doubleBytes[1],

        };
        byte[] lowRegisterBytes = 
        {
            0,0,
            doubleBytes[2],
            doubleBytes[3],

        };
        int[] returnValue =
        {
        		ByteBuffer.wrap(lowRegisterBytes).getInt(),
        		ByteBuffer.wrap(highRegisterBytes).getInt()
        };
        return returnValue;
    }
    
       	/**
        * Convert 32 Bit Value to two 16 Bit Value to send as Modbus Registers
        * @param        intValue      Value to be converted
        * @param        registerOrder    High Register first or low Register first
        * @return       16 Bit Register values
        */
    public static int[] ConvertIntToRegisters(int intValue, RegisterOrder registerOrder)
    {
        int[] registerValues = ConvertIntToRegisters(intValue);
        int[] returnValue = registerValues;
        if (registerOrder == RegisterOrder.HighLow)
            returnValue = new int[] { registerValues[1], registerValues[0] };
        return returnValue;
    }
 
    /**
     * Convert 64 Bit Value to four 16 Bit Value to send as Modbus Registers
     * @param        longValue      Value to be converted
     * @return       16 Bit Register values
     */
	 public static int[] ConvertLongToRegisters(long longValue)
	 {
	     byte[] doubleBytes = toByteArrayLong(longValue);
	     byte[] highhighRegisterBytes = 
	     {
	     		0,0,
	         doubleBytes[0],
	         doubleBytes[1],
	
	     };
	     byte[] highlowRegisterBytes = 
	     {
	         0,0,
	         doubleBytes[2],
	         doubleBytes[3],
	
	     };
	     byte[] lowHighRegisterBytes = 
	     {
	         0,0,
	         doubleBytes[4],
	         doubleBytes[5],
	     };    
	     byte[] lowlowRegisterBytes = 
	     {
	         0,0,
	         doubleBytes[6],
	         doubleBytes[7],
	
	     };
	     int[] returnValue =
	     {
	     		ByteBuffer.wrap(lowlowRegisterBytes).getInt(),
	     		ByteBuffer.wrap(lowHighRegisterBytes).getInt(),
	     		ByteBuffer.wrap(highlowRegisterBytes).getInt(),
	     		ByteBuffer.wrap(highhighRegisterBytes).getInt(),
	     };
	     return returnValue;
	 }

    	/**
     * Convert 64 Bit Value to two 16 Bit Value to send as Modbus Registers
     * @param        longValue      Value to be converted
     * @param        registerOrder    High Register first or low Register first
     * @return       16 Bit Register values
     */
	 public static int[] ConvertLongToRegisters(int longValue, RegisterOrder registerOrder)
	 {
	     int[] registerValues = ConvertLongToRegisters(longValue);
	     int[] returnValue = registerValues;
	     if (registerOrder == RegisterOrder.HighLow)
	         returnValue = new int[] { registerValues[3], registerValues[2], registerValues[1], registerValues[0]};
	     return returnValue;
	 }
	 
	    /**
	     * Convert 64 Bit Value to four 16 Bit Value to send as Modbus Registers
	     * @param        doubleValue      Value to be converted
	     * @return       16 Bit Register values
	     */
		 public static int[] ConvertDoubleToRegisters(double doubleValue)
		 {
		     byte[] doubleBytes = toByteArrayDouble(doubleValue);
		     byte[] highhighRegisterBytes = 
		     {
		     		0,0,
		         doubleBytes[0],
		         doubleBytes[1],
		
		     };
		     byte[] highlowRegisterBytes = 
		     {
		         0,0,
		         doubleBytes[2],
		         doubleBytes[3],
		
		     };
		     byte[] lowHighRegisterBytes = 
		     {
		         0,0,
		         doubleBytes[4],
		         doubleBytes[5],
		     };    
		     byte[] lowlowRegisterBytes = 
		     {
		         0,0,
		         doubleBytes[6],
		         doubleBytes[7],
		
		     };
		     int[] returnValue =
		     {
		     		ByteBuffer.wrap(lowlowRegisterBytes).getInt(),
		     		ByteBuffer.wrap(lowHighRegisterBytes).getInt(),
		     		ByteBuffer.wrap(highlowRegisterBytes).getInt(),
		     		ByteBuffer.wrap(highhighRegisterBytes).getInt(),
		     };
		     return returnValue;
		 }
		 
		
    	/**
	     * Convert 64 Bit Value to two 16 Bit Value to send as Modbus Registers
	     * @param        doubleValue      Value to be converted
	     * @param        registerOrder    High Register first or low Register first
	     * @return       16 Bit Register values
	     */
		 public static int[] ConvertDoubleToRegisters(double doubleValue, RegisterOrder registerOrder)
		 {
		     int[] registerValues = ConvertDoubleToRegisters(doubleValue);
		     int[] returnValue = registerValues;
		     if (registerOrder == RegisterOrder.HighLow)
		         returnValue = new int[] { registerValues[3], registerValues[2], registerValues[1], registerValues[0]};
		     return returnValue;
		 }
  
   	/**
    * Converts 16 - Bit Register values to String
    * @param registers Register array received via Modbus
    * @param offset First Register containing the String to convert
    * @param stringLength number of characters in String (must be even)
    * @return Converted String
    */
    public static String ConvertRegistersToString(int[] registers, int offset, int stringLength)
    { 
    byte[] result = new byte[stringLength];
    byte[] registerResult = new byte[2];
    
        for (int i = 0; i < stringLength/2; i++)
        {
            registerResult = toByteArray(registers[offset + i]);
            result[i * 2] = registerResult[0];
            result[i * 2 + 1] = registerResult[1];
        }
        return new String(result);
    }  

   	/**
    * Converts a String to 16 - Bit Registers
    * @param stringToConvert String to Convert<
    * @return Converted String
    */
    public static int[] ConvertStringToRegisters(String stringToConvert)
    {
        byte[] array = stringToConvert.getBytes();
        int[] returnarray = new int[stringToConvert.length() / 2 + stringToConvert.length() % 2];
        for (int i = 0; i < returnarray.length; i++)
        {
            returnarray[i] = array[i * 2];
            if (i*2 +1< array.length)
            {
                returnarray[i] = returnarray[i] | ((int)array[i * 2 + 1] << 8);
            }
        }
        return returnarray;
    }

    
    public static byte[] calculateCRC(byte[] data, int numberOfBytes, int startByte)
        { 
           byte[] auchCRCHi = {
            (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81,
            (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,
            (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01,
            (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
            (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81,
            (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0,
            (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01,
            (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40,
            (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81,
            (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,
            (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01,
            (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
            (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81,
            (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0,
            (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01,
            (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41,
            (byte)0x00, (byte)0xC1, (byte)0x81, (byte)0x40, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x01, (byte)0xC0, (byte)0x80, (byte)0x41, (byte)0x00, (byte)0xC1, (byte)0x81,
            (byte)0x40
            };
		
            byte[] auchCRCLo = {
            (byte)0x00, (byte)0xC0, (byte)0xC1, (byte)0x01, (byte)0xC3, (byte)0x03, (byte)0x02, (byte)0xC2, (byte)0xC6, (byte)0x06, (byte)0x07, (byte)0xC7, (byte)0x05, (byte)0xC5, (byte)0xC4,
            (byte)0x04, (byte)0xCC, (byte)0x0C, (byte)0x0D, (byte)0xCD, (byte)0x0F, (byte)0xCF, (byte)0xCE, (byte)0x0E, (byte)0x0A, (byte)0xCA, (byte)0xCB, (byte)0x0B, (byte)0xC9, (byte)0x09,
            (byte)0x08, (byte)0xC8, (byte)0xD8, (byte)0x18, (byte)0x19, (byte)0xD9, (byte)0x1B, (byte)0xDB, (byte)0xDA, (byte)0x1A, (byte)0x1E, (byte)0xDE, (byte)0xDF, (byte)0x1F, (byte)0xDD,
            (byte)0x1D, (byte)0x1C, (byte)0xDC, (byte)0x14, (byte)0xD4, (byte)0xD5, (byte)0x15, (byte)0xD7, (byte)0x17, (byte)0x16, (byte)0xD6, (byte)0xD2, (byte)0x12, (byte)0x13, (byte)0xD3,
            (byte)0x11, (byte)0xD1, (byte)0xD0, (byte)0x10, (byte)0xF0, (byte)0x30, (byte)0x31, (byte)0xF1, (byte)0x33, (byte)0xF3, (byte)0xF2, (byte)0x32, (byte)0x36, (byte)0xF6, (byte)0xF7,
            (byte)0x37, (byte)0xF5, (byte)0x35, (byte)0x34, (byte)0xF4, (byte)0x3C, (byte)0xFC, (byte)0xFD, (byte)0x3D, (byte)0xFF, (byte)0x3F, (byte)0x3E, (byte)0xFE, (byte)0xFA, (byte)0x3A,
            (byte)0x3B, (byte)0xFB, (byte)0x39, (byte)0xF9, (byte)0xF8, (byte)0x38, (byte)0x28, (byte)0xE8, (byte)0xE9, (byte)0x29, (byte)0xEB, (byte)0x2B, (byte)0x2A, (byte)0xEA, (byte)0xEE,
            (byte)0x2E, (byte)0x2F, (byte)0xEF, (byte)0x2D, (byte)0xED, (byte)0xEC, (byte)0x2C, (byte)0xE4, (byte)0x24, (byte)0x25, (byte)0xE5, (byte)0x27, (byte)0xE7, (byte)0xE6, (byte)0x26,
            (byte)0x22, (byte)0xE2, (byte)0xE3, (byte)0x23, (byte)0xE1, (byte)0x21, (byte)0x20, (byte)0xE0, (byte)0xA0, (byte)0x60, (byte)0x61, (byte)0xA1, (byte)0x63, (byte)0xA3, (byte)0xA2,
            (byte)0x62, (byte)0x66, (byte)0xA6, (byte)0xA7, (byte)0x67, (byte)0xA5, (byte)0x65, (byte)0x64, (byte)0xA4, (byte)0x6C, (byte)0xAC, (byte)0xAD, (byte)0x6D, (byte)0xAF, (byte)0x6F,
            (byte)0x6E, (byte)0xAE, (byte)0xAA, (byte)0x6A, (byte)0x6B, (byte)0xAB, (byte)0x69, (byte)0xA9, (byte)0xA8, (byte)0x68, (byte)0x78, (byte)0xB8, (byte)0xB9, (byte)0x79, (byte)0xBB,
            (byte)0x7B, (byte)0x7A, (byte)0xBA, (byte)0xBE, (byte)0x7E, (byte)0x7F, (byte)0xBF, (byte)0x7D, (byte)0xBD, (byte)0xBC, (byte)0x7C, (byte)0xB4, (byte)0x74, (byte)0x75, (byte)0xB5,
            (byte)0x77, (byte)0xB7, (byte)0xB6, (byte)0x76, (byte)0x72, (byte)0xB2, (byte)0xB3, (byte)0x73, (byte)0xB1, (byte)0x71, (byte)0x70, (byte)0xB0, (byte)0x50, (byte)0x90, (byte)0x91,
            (byte)0x51, (byte)0x93, (byte)0x53, (byte)0x52, (byte)0x92, (byte)0x96, (byte)0x56, (byte)0x57, (byte)0x97, (byte)0x55, (byte)0x95, (byte)0x94, (byte)0x54, (byte)0x9C, (byte)0x5C,
            (byte)0x5D, (byte)0x9D, (byte)0x5F, (byte)0x9F, (byte)0x9E, (byte)0x5E, (byte)0x5A, (byte)0x9A, (byte)0x9B, (byte)0x5B, (byte)0x99, (byte)0x59, (byte)0x58, (byte)0x98, (byte)0x88,
            (byte)0x48, (byte)0x49, (byte)0x89, (byte)0x4B, (byte)0x8B, (byte)0x8A, (byte)0x4A, (byte)0x4E, (byte)0x8E, (byte)0x8F, (byte)0x4F, (byte)0x8D, (byte)0x4D, (byte)0x4C, (byte)0x8C,
            (byte)0x44, (byte)0x84, (byte)0x85, (byte)0x45, (byte)0x87, (byte)0x47, (byte)0x46, (byte)0x86, (byte)0x82, (byte)0x42, (byte)0x43, (byte)0x83, (byte)0x41, (byte)0x81, (byte)0x80,
            (byte)0x40
            };
            short usDataLen = (short)numberOfBytes;
            byte  uchCRCHi = (byte)0xFF ; 
            byte uchCRCLo = (byte)0xFF ; 
            int i = 0;
            int uIndex ;
            while (usDataLen>0) 
            {
                usDataLen--;
                uIndex = (int)(uchCRCLo ^ (int)data[i+startByte]); 
                if (uIndex<0)
                    uIndex = 256+uIndex;
                uchCRCLo = (byte) (uchCRCHi ^ auchCRCHi[uIndex]) ; 
                uchCRCHi = (byte) auchCRCLo[uIndex] ;
                i++;
            }
            byte[] returnValue = {uchCRCLo, uchCRCHi};
            return returnValue ;
        }
    
 
    /**
     *  Read Discrete Inputs from Server device (FC2) and publishes the values to a MQTT-Broker.
     *  The Topic will be easymodbusclient/discreteinputs/'address' e.g. easymodbusclient/discreteinputs/0 for address "0".
     *  Note that the Address that will be publishes is "0"-Based. The Root topic can be changed using the Parameter
     *  By default we are using the Standard-Port 1883. This Port can be changed using the Property "MqttBrokerPort"
     *  A Username and Passowrd can be provided using the Properties "MqttUserName" and "MqttPassword"
     *  'MqttRootTopic' Default is 'easymodbusclient'
     * @param startingAddress	First discrete input to read
     * @param quantity	Number of discrete Inputs to read
     * @param mqttBrokerAddress	Broker address the values will be published to
     * @return	Boolean Array which contains the discrete Inputs
     * @throws MqttException 
     * @throws MqttPersistenceException 
     * @throws SerialPortTimeoutException 
     * @throws SerialPortException 
     * @throws IOException 
     * @throws ModbusException 
     * @throws SocketException 
     * @throws UnknownHostException 
     */
    public boolean[] ReadDiscreteInputs(int startingAddress, int quantity, String mqttBrokerAddress) throws MqttPersistenceException, MqttException, UnknownHostException, SocketException, ModbusException, IOException, SerialPortException, SerialPortTimeoutException
    {
        boolean[] returnValue = this.ReadDiscreteInputs(startingAddress, quantity);
        List<String> topic = new ArrayList<String>();
        List<String> payload = new ArrayList<String>();
        if (mqttPushOnChange && mqttDiscreteInputsOldValues == null)
            mqttDiscreteInputsOldValues = new boolean[65535];
        for (int i = 0; i < returnValue.length; i++)
        {
            if (mqttDiscreteInputsOldValues == null ? true : (mqttDiscreteInputsOldValues[i] != returnValue[i]))
            {
                topic.add(mqttRootTopic + "/discreteinputs/" + Integer.toString(i + startingAddress));
                payload.add(Boolean.toString(returnValue[i]));
                mqttDiscreteInputsOldValues[i] = returnValue[i];
            }
        }
        
        if (easyModbus2Mqtt == null)
            easyModbus2Mqtt = new EasyModbus2Mqtt();
        easyModbus2Mqtt.setMqttBrokerPort(this.mqttBrokerPort);
        easyModbus2Mqtt.setMqttUserName(this.mqttUserName);
        easyModbus2Mqtt.setMqttPassword(this.mqttPassword);
        easyModbus2Mqtt.setRetainMessages(this.mqttRetainMessages);
        easyModbus2Mqtt.publish(topic.toArray(new String[topic.size()]), payload.toArray(new String[payload.size()]), mqttBrokerAddress);

        return returnValue;
    }
    
    
        /**
        * Read Discrete Inputs from Server
        * @param        startingAddress      Fist Address to read; Shifted by -1	
        * @param        quantity            Number of Inputs to read
        * @return       Discrete Inputs from Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */    
	public boolean[] ReadDiscreteInputs(int startingAddress, int quantity) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
	{
		if (tcpClientSocket == null)
			throw new de.re.easymodbus.exceptions.ConnectionException("connection Error");
		if (startingAddress > 65535 | quantity > 2000)
			throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 2000");
		boolean[] response = null;
		this.transactionIdentifier = toByteArray(0x0001);
		this.protocolIdentifier = toByteArray(0x0000);
		this.length = toByteArray(0x0006);
		this.functionCode = 0x02;
		this.startingAddress = toByteArray(startingAddress);
		this.quantity = toByteArray(quantity);
		byte[] data = new byte[]
				{
					this.transactionIdentifier[1],
					this.transactionIdentifier[0],
					this.protocolIdentifier[1],
					this.protocolIdentifier[0],
					this.length[1],
					this.length[0],
					this.unitIdentifier,
					this.functionCode,
					this.startingAddress[1],
					this.startingAddress[0],
					this.quantity[1],
					this.quantity[0],
                    this.crc[0],
                    this.crc[1]					
				};
        if (this.serialflag)
        {
            crc = calculateCRC(data, 6, 6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata = null;
        if (serialflag)
        {        	
        	serialdata = new byte[8];
        	java.lang.System.arraycopy(data, 6,serialdata,0,8);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
        	if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));          		
            long dateTimeSend = DateTime.getDateTimeTicks();
            byte receivedUnitIdentifier = (byte)0xFF;
            serialdata = new byte[256];
            int expectedlength = 5+quantity/8+1;
            if (quantity % 8 == 0)
                expectedlength = 5+quantity/8;
            while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
            {

                	serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
                   
         
               receivedUnitIdentifier = serialdata[0];
            }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                serialdata = new byte[256];     
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
		
		if (tcpClientSocket.isConnected() | udpFlag)
		{
			if (udpFlag)
			{
				InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length-2, ipAddress, this.port);
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.setSoTimeout(500);
			    clientSocket.send(sendPacket);
			    data = new byte[2100];
			    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			    clientSocket.receive(receivePacket);
			    clientSocket.close();
			    data = receivePacket.getData();
			}
			else
			{
				outStream.write(data, 0, data.length-2);
        		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));          		
				if (sendDataChangedListener.size() > 0)
				{
					sendData = new byte[data.length-2];
					System.arraycopy(data, 0, sendData, 0, data.length-2);
					for (SendDataChangedListener hl : sendDataChangedListener)
						hl.SendDataChanged();
				}
				data = new byte[2100];
				int numberOfBytes = inStream.read(data, 0, data.length);
				if (receiveDataChangedListener.size() > 0)
				{
					receiveData = new byte[numberOfBytes];
					System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
					for (ReceiveDataChangedListener hl : receiveDataChangedListener)
						hl.ReceiveDataChanged();
                    if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));

				}
			}
			}
			if (((int) (data[7] & 0xff)) == 0x82 & ((int) data[8]) == 0x01)
			{
				if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
				throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
			}
			if (((int) (data[7] & 0xff)) == 0x82 & ((int) data[8]) == 0x02)
			{
				if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
				throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
			}
			if (((int) (data[7] & 0xff)) == 0x82 & ((int) data[8]) == 0x03)
			{
				if (debug) StoreLogData.getInstance().Store("Quantity invalid");
				throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
			}
			if (((int) (data[7] & 0xff)) == 0x82 & ((int) data[8]) == 0x04)
			{
				if (debug) StoreLogData.getInstance().Store("Error reading");
				throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
			}
			response = new boolean [quantity];
			for (int i = 0; i < quantity; i++)
			{
				int intData = data[9 + i/8];
				int mask = (int)Math.pow(2, (i%8));
				intData = ((intData & mask)/mask);
				if (intData >0)
					response[i] = true;
				else
					response[i] = false;
			}
			
		
		return (response);
	}
	
    /**
     *  Read Coils from Server device (FC1) and publishes the values to a MQTT-Broker.
     *  The Topic will be easymodbusclient/coils/'address' e.g. easymodbusclient/coils/0 for address "0".
     *  Note that the Address that will be publishes is "0"-Based. The Root topic can be changed using the Parameter
     *  By default we are using the Standard-Port 1883. This Port can be changed using the Property "MqttBrokerPort"
     *  A Username and Passowrd can be provided using the Properties "MqttUserName" and "MqttPassword"
     *  'MqttRootTopic' Default is 'easymodbusclient'
     * @param startingAddress	First coil to read
     * @param quantity	Number of coils to read
     * @param mqttBrokerAddress	Broker address the values will be published to
     * @return	Boolean Array which contains the coils
     * @throws MqttException 
     * @throws MqttPersistenceException 
     * @throws SerialPortTimeoutException 
     * @throws SerialPortException 
     * @throws IOException 
     * @throws ModbusException 
     * @throws SocketException 
     * @throws UnknownHostException 
     */
    public boolean[] ReadCoils(int startingAddress, int quantity, String mqttBrokerAddress) throws MqttPersistenceException, MqttException, UnknownHostException, SocketException, ModbusException, IOException, SerialPortException, SerialPortTimeoutException
    {
        boolean[] returnValue = this.ReadCoils(startingAddress, quantity);
        List<String> topic = new ArrayList<String>();
        List<String> payload = new ArrayList<String>();
        if (mqttPushOnChange && mqttCoilsOldValues == null)
            mqttCoilsOldValues = new boolean[65535];
        for (int i = 0; i < returnValue.length; i++)
        {
            if (mqttCoilsOldValues == null ? true : (mqttCoilsOldValues[i] != returnValue[i]))
            {
                topic.add(mqttRootTopic + "/coils/" + Integer.toString(i + startingAddress));
                payload.add(Boolean.toString(returnValue[i]));
                mqttCoilsOldValues[i] = returnValue[i];
            }
        }
        
        if (easyModbus2Mqtt == null)
            easyModbus2Mqtt = new EasyModbus2Mqtt();
        easyModbus2Mqtt.setMqttBrokerPort(this.mqttBrokerPort);
        easyModbus2Mqtt.setMqttUserName(this.mqttUserName);
        easyModbus2Mqtt.setMqttPassword(this.mqttPassword);
        easyModbus2Mqtt.setRetainMessages(this.mqttRetainMessages);
        easyModbus2Mqtt.publish(topic.toArray(new String[topic.size()]), payload.toArray(new String[payload.size()]), mqttBrokerAddress);

        return returnValue;
    }
	
        /**
        * Read Coils from Server
        * @param        startingAddress      Fist Address to read; Shifted by -1	
        * @param        quantity            Number of Inputs to read
        * @return       coils from Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */
	public boolean[] ReadCoils(int startingAddress, int quantity) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
	{
		if (tcpClientSocket == null)
			throw new de.re.easymodbus.exceptions.ConnectionException("connection Error");
		if (startingAddress > 65535 | quantity > 2000)
			throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 2000");
		boolean[] response = new boolean[quantity];
		this.transactionIdentifier = toByteArray(0x0001);
		this.protocolIdentifier = toByteArray(0x0000);
		this.length = toByteArray(0x0006);
		//this.unitIdentifier = 0x00;
		this.functionCode = 0x01;
		this.startingAddress = toByteArray(startingAddress);
		this.quantity = toByteArray(quantity);
		byte[] data = new byte[]
				{
					this.transactionIdentifier[1],
					this.transactionIdentifier[0],
					this.protocolIdentifier[1],
					this.protocolIdentifier[0],
					this.length[1],
					this.length[0],
					this.unitIdentifier,
					this.functionCode,
					this.startingAddress[1],
					this.startingAddress[0],
					this.quantity[1],
					this.quantity[0],
                    this.crc[0],
                    this.crc[1]		
				};
            if (this.serialflag)
            {
                crc = calculateCRC(data, 6, 6);
                data[data.length -2] = crc[0];
                data[data.length -1] = crc[1];
                
            }
            byte[] serialdata = null;
            if (serialflag)
            {
            	serialdata = new byte[8];
            	java.lang.System.arraycopy(data, 6,serialdata,0,8);
        		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
        		serialPort.writeBytes(serialdata);
        		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
                long dateTimeSend = DateTime.getDateTimeTicks();
                byte receivedUnitIdentifier = (byte)0xFF;
                serialdata = new byte[256];
                int expectedlength = 5+quantity/8+1;
                if (quantity % 8 == 0)
                    expectedlength = 5+quantity/8;
                while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
                {           	
                	serialdata = serialPort.readBytes(expectedlength, this.connectTimeout);              

                	receivedUnitIdentifier = serialdata[0];
                }
               if (receivedUnitIdentifier != this.unitIdentifier)
               {
                    serialdata = new byte[256];     
               }
            }
            if (serialdata != null)
            {
                data = new byte[262]; 
                System.arraycopy(serialdata, 0, data, 6, serialdata.length);
                if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
            }
		if (tcpClientSocket.isConnected() | udpFlag)
		{
			if (udpFlag)
			{
                            InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
                            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
                            DatagramSocket clientSocket = new DatagramSocket();
                            clientSocket.setSoTimeout(500);
			    clientSocket.send(sendPacket);
			    data = new byte[2100];
			    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			    clientSocket.receive(receivePacket);
			    clientSocket.close();
			    data = receivePacket.getData();
			}
			else
			{
				outStream.write(data, 0, data.length-2);
        		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
				if (sendDataChangedListener.size() > 0)
				{
					sendData = new byte[data.length-2];
					System.arraycopy(data, 0, sendData, 0, data.length-2);
					for (SendDataChangedListener hl : sendDataChangedListener)
						hl.SendDataChanged();
				}
				data = new byte[2100];
				int numberOfBytes = inStream.read(data, 0, data.length);
				if (receiveDataChangedListener.size() > 0)
				{
					receiveData = new byte[numberOfBytes];
					System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
					for (ReceiveDataChangedListener hl : receiveDataChangedListener)
						hl.ReceiveDataChanged();
					if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
				}
			}
                }
			if (((int) (data[7] & 0xff)) == 0x81 & ((int) data[8]) == 0x01)
			{
				if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
				throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
			}
			if (((int) (data[7] & 0xff)) == 0x81 & ((int) data[8]) == 0x02)
			{
				if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
				throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
			}			
			if (((int) (data[7] & 0xff)) == 0x81 & ((int) data[8]) == 0x03)
			{
				if (debug) StoreLogData.getInstance().Store("Quantity invalid");
				throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
			}
			if (((int) (data[7] & 0xff)) == 0x81 & ((int) data[8]) == 0x04)
			{
				if (debug) StoreLogData.getInstance().Store("Error reading");
				throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
			}
			for (int i = 0; i < quantity; i++)
			{
				int intData = (int) data[9 + i/8];
				int mask = (int)Math.pow(2, (i%8));
				intData = ((intData & mask)/mask);
				if (intData >0)
					response[i] = true;
				else
					response[i] = false;
			}
			
		
		return (response);
	}
 
    /**
     *  Read Holding Registers from Server device and publishes the values to a MQTT-Broker.
     *  The Topic will be easymodbusclient/holdingregisters/'address' e.g. easymodbusclient/holdingregisters/0 for address "0".
     *  Note that the Address that will be publishes is "0"-Based. The Root topic can be changed using the Parameter
     *  By default we are using the Standard-Port 1883. This Port can be changed using the Property "MqttBrokerPort"
     *  A Username and Passowrd can be provided using the Properties "MqttUserName" and "MqttPassword"
     *  'MqttRootTopic' Default is 'easymodbusclient'
     * @param startingAddress	First Holding Register to read
     * @param quantity	Number of Holding Registers to read
     * @param mqttBrokerAddress	Broker address the values will be published to
     * @return	Boolean Array which contains the Holding Registers
     * @throws MqttException 
     * @throws MqttPersistenceException 
     * @throws SerialPortTimeoutException 
     * @throws SerialPortException 
     * @throws IOException 
     * @throws ModbusException 
     * @throws SocketException 
     * @throws UnknownHostException 
     */
    public int[] ReadHoldingRegisters(int startingAddress, int quantity, String mqttBrokerAddress) throws MqttPersistenceException, MqttException, UnknownHostException, SocketException, ModbusException, IOException, SerialPortException, SerialPortTimeoutException
    {
        int[] returnValue = this.ReadHoldingRegisters(startingAddress, quantity);
        List<String> topic = new ArrayList<String>();
        List<String> payload = new ArrayList<String>();
        if (mqttPushOnChange && mqttHoldingRegistersOldValues == null)
        	mqttHoldingRegistersOldValues = new int[65535];
        for (int i = 0; i < returnValue.length; i++)
        {
            if (mqttHoldingRegistersOldValues == null ? true : (mqttHoldingRegistersOldValues[i] != returnValue[i]))
            {
                topic.add(mqttRootTopic + "/holdingregisters/" + Integer.toString(i + startingAddress));
                payload.add(Integer.toString(returnValue[i]));
                mqttHoldingRegistersOldValues[i] = returnValue[i];
            }
        }
        
        if (easyModbus2Mqtt == null)
            easyModbus2Mqtt = new EasyModbus2Mqtt();
        easyModbus2Mqtt.setMqttBrokerPort(this.mqttBrokerPort);
        easyModbus2Mqtt.setMqttUserName(this.mqttUserName);
        easyModbus2Mqtt.setMqttPassword(this.mqttPassword);
        easyModbus2Mqtt.setRetainMessages(this.mqttRetainMessages);
        easyModbus2Mqtt.publish(topic.toArray(new String[topic.size()]), payload.toArray(new String[payload.size()]), mqttBrokerAddress);

        return returnValue;
    }	
	
        /**
        * Read Holding Registers from Server
        * @param        startingAddress      Fist Address to read; Shifted by -1	
        * @param        quantity            Number of Inputs to read
        * @return       Holding Registers from Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */
	public int[] ReadHoldingRegisters(int startingAddress, int quantity) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
	{
		if (tcpClientSocket == null)
			throw new de.re.easymodbus.exceptions.ConnectionException("connection Error");
		if (startingAddress > 65535 | quantity > 125)
			throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 125");
		int[] response = new int[quantity];
		this.transactionIdentifier = toByteArray(0x0001);
		this.protocolIdentifier = toByteArray(0x0000);
		this.length = toByteArray(0x0006);
		//serialdata = this.unitIdentifier;
		this.functionCode = 0x03;
		this.startingAddress = toByteArray(startingAddress);
		this.quantity = toByteArray(quantity);

		byte[] data = new byte[]
				{
					this.transactionIdentifier[1],
					this.transactionIdentifier[0],
					this.protocolIdentifier[1],
					this.protocolIdentifier[0],
					this.length[1],
					this.length[0],
					this.unitIdentifier,
					this.functionCode,
					this.startingAddress[1],
					this.startingAddress[0],
					this.quantity[1],
					this.quantity[0],
                    this.crc[0],
                    this.crc[1]		
				};
            
            if (this.serialflag)
            {
                crc = calculateCRC(data, 6, 6);
                data[data.length -2] = crc[0];
                data[data.length -1] = crc[1];
            }
            byte[] serialdata = null;   
            if (serialflag)
            {          
            	serialdata = new byte[8];
            	java.lang.System.arraycopy(data, 6,serialdata,0,8);
        		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
        		serialPort.writeBytes(serialdata);
        		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
               long dateTimeSend = DateTime.getDateTimeTicks();
               byte receivedUnitIdentifier = (byte)0xFF;
               serialdata = new byte[256];
               int expectedlength = 5+2*quantity;
               while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
               {
            	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
            	   receivedUnitIdentifier = serialdata[0];
               }
               if (receivedUnitIdentifier != this.unitIdentifier)
               {
                    data = new byte[256];                       
               }
               if (serialdata != null)
               {
                   data = new byte[262]; 
                   System.arraycopy(serialdata, 0, data, 6, serialdata.length);
                   if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
               }
                for (int i = 0; i < quantity; i++)
                {
                    byte[] bytes = new byte[2];
                    bytes[0] = data[3+i*2];
                    bytes[1] = data[3+i*2+1];
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);						
                    response[i] = byteBuffer.getShort();
		}	
            }

                
		if (tcpClientSocket.isConnected() | udpFlag)
		{
			if (udpFlag)
			{
				InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.setSoTimeout(500);
			    clientSocket.send(sendPacket);
			    data = new byte[2100];
			    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			    clientSocket.receive(receivePacket);
			    clientSocket.close();
			    data = receivePacket.getData();
			}
			else
			{
				outStream.write(data, 0, data.length-2);
        		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
				if (sendDataChangedListener.size() > 0)
				{
					sendData = new byte[data.length-2];
					System.arraycopy(data, 0, sendData, 0, data.length-2);
					for (SendDataChangedListener hl : sendDataChangedListener)
						hl.SendDataChanged();
				}
				data = new byte[2100];
				int numberOfBytes = inStream.read(data, 0, data.length);
				if (receiveDataChangedListener.size() > 0)
				{
					receiveData = new byte[numberOfBytes];
					System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
					for (ReceiveDataChangedListener hl : receiveDataChangedListener)
						hl.ReceiveDataChanged();
					if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
				}
                        }
			}
			if (((int) data[7]) == 0x83 & ((int) data[8]) == 0x01)
			{
				if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
				throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
			}
			if (((int) data[7]) == 0x83 & ((int) data[8]) == 0x02)
			{
				if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
				throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
			}			
			if (((int) data[7]) == 0x83 & ((int) data[8]) == 0x03)
			{
				if (debug) StoreLogData.getInstance().Store("Quantity invalid");
				throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
			}
			if (((int) data[7]) == 0x83 & ((int) data[8]) == 0x04)
			{
				if (debug) StoreLogData.getInstance().Store("Error reading");
				throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
			}
			for (int i = 0; i < quantity; i++)
			{
				byte[] bytes = new byte[2];
				bytes[0] = data[9+i*2];
				bytes[1] = data[9+i*2+1];
				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
						
				response[i] = byteBuffer.getShort();
			}
			
		
		return (response);
	}

    /**
     *  Read Input Registers from Server device and publishes the values to a MQTT-Broker.
     *  The Topic will be easymodbusclient/inputregisters/'address' e.g. easymodbusclient/inputregisters/0 for address "0".
     *  Note that the Address that will be publishes is "0"-Based. The Root topic can be changed using the Parameter
     *  By default we are using the Standard-Port 1883. This Port can be changed using the Property "MqttBrokerPort"
     *  A Username and Passowrd can be provided using the Properties "MqttUserName" and "MqttPassword"
     *  'MqttRootTopic' Default is 'easymodbusclient'
     * @param startingAddress	First Input Register to read
     * @param quantity	Number of Input Registers to read
     * @param mqttBrokerAddress	Broker address the values will be published to
     * @return	Boolean Array which contains the Input Registers
     * @throws MqttException 
     * @throws MqttPersistenceException 
     * @throws SerialPortTimeoutException 
     * @throws SerialPortException 
     * @throws IOException 
     * @throws ModbusException 
     * @throws SocketException 
     * @throws UnknownHostException 
     */
    public int[] ReadInputRegisters(int startingAddress, int quantity, String mqttBrokerAddress) throws MqttPersistenceException, MqttException, UnknownHostException, SocketException, ModbusException, IOException, SerialPortException, SerialPortTimeoutException
    {
        int[] returnValue = this.ReadInputRegisters(startingAddress, quantity);
        List<String> topic = new ArrayList<String>();
        List<String> payload = new ArrayList<String>();
        if (mqttPushOnChange && mqttInputRegistersOldValues == null)
        	mqttInputRegistersOldValues = new int[65535];
        for (int i = 0; i < returnValue.length; i++)
        {
            if (mqttInputRegistersOldValues == null ? true : (mqttInputRegistersOldValues[i] != returnValue[i]))
            {
                topic.add(mqttRootTopic + "/inputregisters/" + Integer.toString(i + startingAddress));
                payload.add(Integer.toString(returnValue[i]));
                mqttInputRegistersOldValues[i] = returnValue[i];
            }
        }
        
        if (easyModbus2Mqtt == null)
            easyModbus2Mqtt = new EasyModbus2Mqtt();
        easyModbus2Mqtt.setMqttBrokerPort(this.mqttBrokerPort);
        easyModbus2Mqtt.setMqttUserName(this.mqttUserName);
        easyModbus2Mqtt.setMqttPassword(this.mqttPassword);
        easyModbus2Mqtt.setRetainMessages(this.mqttRetainMessages);
        easyModbus2Mqtt.publish(topic.toArray(new String[topic.size()]), payload.toArray(new String[payload.size()]), mqttBrokerAddress);

        return returnValue;
    }		
	
	/**
        * Read Input Registers from Server
        * @param        startingAddress      Fist Address to read; Shifted by -1	
        * @param        quantity            Number of Inputs to read
        * @return       Input Registers from Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
	 * @throws SerialPortTimeoutException 
	 * @throws SerialPortException 
        */
	public int[] ReadInputRegisters(int startingAddress, int quantity) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
	{
		if (tcpClientSocket == null)
			throw new de.re.easymodbus.exceptions.ConnectionException("connection Error");
		if (startingAddress > 65535 | quantity > 125)
			throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 125");
		int[] response = new int[quantity];
		this.transactionIdentifier = toByteArray(0x0001);
		this.protocolIdentifier = toByteArray(0x0000);
		this.length = toByteArray(0x0006);
		//this.unitIdentifier = 0x00;
		this.functionCode = 0x04;
		this.startingAddress = toByteArray(startingAddress);
		this.quantity = toByteArray(quantity);
		byte[] data = new byte[]
				{
					this.transactionIdentifier[1],
					this.transactionIdentifier[0],
					this.protocolIdentifier[1],
					this.protocolIdentifier[0],
					this.length[1],
					this.length[0],
					this.unitIdentifier,
					this.functionCode,
					this.startingAddress[1],
					this.startingAddress[0],
					this.quantity[1],
					this.quantity[0],
                    this.crc[0],
                    this.crc[1]		
				};
        if (this.serialflag)
        {
            crc = calculateCRC(data, 6, 6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata=null;
        if (serialflag)
        {        
        	serialdata = new byte[8];
        	java.lang.System.arraycopy(data, 6,serialdata,0,8);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 5+2*quantity;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
           
        	   receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
           if (serialdata != null)
           {
               data = new byte[262]; 
               System.arraycopy(serialdata, 0, data, 6, serialdata.length);
               if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
           }
            for (int i = 0; i < quantity; i++)
            {
                byte[] bytes = new byte[2];
                bytes[0] = data[3+i*2];
                bytes[1] = data[3+i*2+1];
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);						
                response[i] = byteBuffer.getShort();
            }	
        }

		if (tcpClientSocket.isConnected() | udpFlag)
		{
			if (udpFlag)
			{
				InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.setSoTimeout(500);
			    clientSocket.send(sendPacket);
			    data = new byte[2100];
			    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			    clientSocket.receive(receivePacket);
			    clientSocket.close();
			    data = receivePacket.getData();
			}
			else
			{
				outStream.write(data, 0, data.length-2);
        		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
				if (sendDataChangedListener.size() > 0)
				{
					sendData = new byte[data.length-2];
					System.arraycopy(data, 0, sendData, 0, data.length-2);
					for (SendDataChangedListener hl : sendDataChangedListener)
						hl.SendDataChanged();
				}
				data = new byte[2100];
				int numberOfBytes = inStream.read(data, 0, data.length);
				if (receiveDataChangedListener.size() > 0)
				{
					receiveData = new byte[numberOfBytes];
					System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
					for (ReceiveDataChangedListener hl : receiveDataChangedListener)
						hl.ReceiveDataChanged();
					if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
				}
			}
			if (((int) (data[7] & 0xff)) == 0x84 & ((int) data[8]) == 0x01)
			{
				if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
				throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
			}			
			if (((int) (data[7] & 0xff)) == 0x84 & ((int) data[8]) == 0x02)
			{
				if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
				throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
			}			
			if (((int) (data[7] & 0xff)) == 0x84 & ((int) data[8]) == 0x03)
			{
				if (debug) StoreLogData.getInstance().Store("Quantity invalid");
				throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
			}
			if (((int) (data[7] & 0xff)) == 0x84 & ((int) data[8]) == 0x04)
			{
				if (debug) StoreLogData.getInstance().Store("Error reading");
				throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
			}
			}
			for (int i = 0; i < quantity; i++)
			{
				byte[] bytes = new byte[2];
				bytes[0] = (byte) data[9+i*2];
				bytes[1] = (byte) data[9+i*2+1];
				ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
				response[i] = byteBuffer.getShort();
			}
			
		
		return (response);
	}
	
        /**
        * Write Single Coil to Server
        * @param        startingAddress      Address to write; Shifted by -1	
        * @param        value            Value to write to Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */
    public void WriteSingleCoil(int startingAddress, boolean value) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
    {
        if (tcpClientSocket == null & !udpFlag)
            throw new de.re.easymodbus.exceptions.ConnectionException("connection error");
        byte[] coilValue = new byte[2];
		this.transactionIdentifier = toByteArray(0x0001);
		this.protocolIdentifier = toByteArray(0x0000);
		this.length = toByteArray(0x0006);
		//this.unitIdentifier = 0;
		this.functionCode = 0x05;
		this.startingAddress = toByteArray(startingAddress);
        if (value == true)
        {
            coilValue = toByteArray((int)0xFF00);
        }
        else
        {
            coilValue = toByteArray((int)0x0000);
        }
        byte[] data = new byte[]{	this.transactionIdentifier[1],
						this.transactionIdentifier[0],
						this.protocolIdentifier[1],
						this.protocolIdentifier[0],
						this.length[1],
						this.length[0],
						this.unitIdentifier,
						this.functionCode,
						this.startingAddress[1],
						this.startingAddress[0],
						coilValue[1],
						coilValue[0],
	                    this.crc[0],
	                    this.crc[1]		
                        };
        if (this.serialflag)
        {
            crc = calculateCRC(data, 6, 6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata= null;
        if (serialflag)
        {             
        	serialdata = new byte[8];
        	java.lang.System.arraycopy(data, 6,serialdata,0,8);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 8;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {          
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
        	   receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
        if (tcpClientSocket.isConnected() | udpFlag)
        {
			if (udpFlag)
			{
				InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.setSoTimeout(500);
			    clientSocket.send(sendPacket);
			    data = new byte[2100];
			    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			    clientSocket.receive(receivePacket);
			    clientSocket.close();
			    data = receivePacket.getData();
			}
			else
			{
				outStream.write(data, 0, data.length-2);
        		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
				if (sendDataChangedListener.size() > 0)
				{
					sendData = new byte[data.length-2];
					System.arraycopy(data, 0, sendData, 0, data.length-2);
					for (SendDataChangedListener hl : sendDataChangedListener)
						hl.SendDataChanged();
				}
				data = new byte[2100];
				int numberOfBytes = inStream.read(data, 0, data.length);
				if (receiveDataChangedListener.size() > 0)
				{
					receiveData = new byte[numberOfBytes];
					System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
					for (ReceiveDataChangedListener hl : receiveDataChangedListener)
						hl.ReceiveDataChanged();
					if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
				}
			}
        }
        if (((int)(data[7] & 0xff)) == 0x85 & data[8] == 0x01)
		{
			if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
			throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
		}        
        if (((int)(data[7] & 0xff)) == 0x85 & data[8] == 0x02)
		{
			if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
			throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
		}        
        if (((int)(data[7] & 0xff)) == 0x85 & data[8] == 0x03)
		{
			if (debug) StoreLogData.getInstance().Store("Quantity invalid");
			throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
		}        
        if (((int)(data[7] & 0xff)) == 0x85 & data[8] == 0x04)
		{
			if (debug) StoreLogData.getInstance().Store("Error reading");
			throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
		}
        }
    
        /**
        * Write Single Register to Server
        * @param        startingAddress      Address to write; Shifted by -1	
        * @param        value            Value to write to Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */
    public void WriteSingleRegister(int startingAddress, int value) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
    {
        if (tcpClientSocket == null & !udpFlag)
            throw new de.re.easymodbus.exceptions.ConnectionException("connection error");
        byte[] registerValue = new byte[2];
        this.transactionIdentifier = toByteArray((int)0x0001);
        this.protocolIdentifier = toByteArray((int)0x0000);
        this.length = toByteArray((int)0x0006);
        this.functionCode = 0x06;
        this.startingAddress = toByteArray(startingAddress);
            registerValue = toByteArray((short)value);

        byte[] data = new byte[]{	this.transactionIdentifier[1],
						this.transactionIdentifier[0],
						this.protocolIdentifier[1],
						this.protocolIdentifier[0],
						this.length[1],
						this.length[0],
						this.unitIdentifier,
						this.functionCode,
						this.startingAddress[1],
						this.startingAddress[0],
						registerValue[1],
						registerValue[0],
	                    this.crc[0],
	                    this.crc[1]		
                        };
        if (this.serialflag)
        {
            crc = calculateCRC(data, 6, 6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata =null;   
        if (serialflag)
        {             
        	serialdata = new byte[8];
        	java.lang.System.arraycopy(data, 6,serialdata,0,8);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 8;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {           
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
           receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
        if (tcpClientSocket.isConnected() | udpFlag)
        {
		if (udpFlag)
		{
			InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(500);
		    clientSocket.send(sendPacket);
		    data = new byte[2100];
		    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		    clientSocket.receive(receivePacket);
		    clientSocket.close();
		    data = receivePacket.getData();
		}
		else
		{
			outStream.write(data, 0, data.length-2);
    		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
			if (sendDataChangedListener.size() > 0)
			{
				sendData = new byte[data.length-2];
				System.arraycopy(data, 0, sendData, 0, data.length-2);
				for (SendDataChangedListener hl : sendDataChangedListener)
					hl.SendDataChanged();
			}
			data = new byte[2100];
			int numberOfBytes = inStream.read(data, 0, data.length);
			if (receiveDataChangedListener.size() > 0)
			{
				receiveData = new byte[numberOfBytes];
				System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
				for (ReceiveDataChangedListener hl : receiveDataChangedListener)
					hl.ReceiveDataChanged();
				if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
			}
		}
        }
        if (((int)(data[7] & 0xff)) == 0x86 & data[8] == 0x01)
		{
			if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
			throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
		}        
        if (((int)(data[7] & 0xff)) == 0x86 & data[8] == 0x02)
		{
			if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
			throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
		}        
        if (((int)(data[7] & 0xff)) == 0x86 & data[8] == 0x03)
		{
			if (debug) StoreLogData.getInstance().Store("Quantity invalid");
			throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x86 & data[8] == 0x04)
		{
			if (debug) StoreLogData.getInstance().Store("Error reading");
			throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
		}
       }
    
       /**
        * Write Multiple Coils to Server
        * @param        startingAddress      Firts Address to write; Shifted by -1	
        * @param        values           Values to write to Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
     * @throws SerialPortTimeoutException 
     * @throws SerialPortException 
        */
    public void WriteMultipleCoils(int startingAddress, boolean[] values) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
    {
        byte byteCount = (byte)(values.length/8+1);
        if (values.length % 8 == 0)
        	byteCount=(byte)(byteCount-1);
        byte[] quantityOfOutputs = toByteArray((int)values.length);
        byte singleCoilValue = 0;
        if (tcpClientSocket == null & !udpFlag)
            throw new de.re.easymodbus.exceptions.ConnectionException("connection error");
        this.transactionIdentifier = toByteArray((int)0x0001);
        this.protocolIdentifier = toByteArray((int)0x0000);
        this.length = toByteArray((int)(7+(values.length/8+1)));
        this.functionCode = 0x0F;
        this.startingAddress = toByteArray(startingAddress);

        byte[] data = new byte[16 + byteCount-1];
        data[0] = this.transactionIdentifier[1];
        data[1] = this.transactionIdentifier[0];
        data[2] = this.protocolIdentifier[1];
        data[3] = this.protocolIdentifier[0];
		data[4] = this.length[1];
		data[5] = this.length[0];
		data[6] = this.unitIdentifier;
		data[7] = this.functionCode;
		data[8] = this.startingAddress[1];
		data[9] = this.startingAddress[0];
        data[10] = quantityOfOutputs[1];
        data[11] = quantityOfOutputs[0];
        data[12] = byteCount;
        for (int i = 0; i < values.length; i++)
        {
            if ((i % 8) == 0)
                singleCoilValue = 0;
            byte CoilValue;
            if (values[i] == true)
                CoilValue = 1;
            else
                CoilValue = 0;


            singleCoilValue = (byte)((int)CoilValue<<(i%8) | (int)singleCoilValue);

            data[13 + (i / 8)] = singleCoilValue;            
        }
        if (this.serialflag)
        {
            crc = calculateCRC(data, data.length-8,6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata=null;  
        if (serialflag)
        {       
        	serialdata = new byte[9+byteCount];
        	java.lang.System.arraycopy(data, 6,serialdata,0,9+byteCount);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 8;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               

           receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
        if (tcpClientSocket.isConnected() | udpFlag)
        {
		if (udpFlag)
		{
			InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(500);
		    clientSocket.send(sendPacket);
		    data = new byte[2100];
		    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		    clientSocket.receive(receivePacket);
		    clientSocket.close();
		    data = receivePacket.getData();
		}
		else
		{
			outStream.write(data, 0, data.length-2);
    		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
			if (sendDataChangedListener.size() > 0)
			{
				sendData = new byte[data.length-2];
				System.arraycopy(data, 0, sendData, 0, data.length-2);
				for (SendDataChangedListener hl : sendDataChangedListener)
					hl.SendDataChanged();
			}
			data = new byte[2100];
			int numberOfBytes = inStream.read(data, 0, data.length);
			if (receiveDataChangedListener.size() > 0)
			{
				receiveData = new byte[numberOfBytes];
				System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
				for (ReceiveDataChangedListener hl : receiveDataChangedListener)
					hl.ReceiveDataChanged();
				if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
			}
		}
        }
        if (((int)(data[7] & 0xff)) == 0x8F & data[8] == 0x01)
		{
			if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
			throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
		}       
        if (((int)(data[7] & 0xff)) == 0x8F & data[8] == 0x02)
		{
			if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
			throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x8F & data[8] == 0x03)
		{
			if (debug) StoreLogData.getInstance().Store("Quantity invalid");
			throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x8F & data[8] == 0x04)
		{
			if (debug) StoreLogData.getInstance().Store("Error reading");
			throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
		}
        }
    
        /**
        * Write Multiple Registers to Server
        * @param        startingAddress      Firts Address to write; Shifted by -1	
        * @param        values           Values to write to Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */    public void WriteMultipleRegisters(int startingAddress, int[] values) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException

    {
        byte byteCount = (byte)(values.length * 2);
        byte[] quantityOfOutputs = toByteArray((int)values.length);
        if (tcpClientSocket == null & !udpFlag)
            throw new de.re.easymodbus.exceptions.ConnectionException("connection error");
        this.transactionIdentifier = toByteArray((int)0x0001);
        this.protocolIdentifier = toByteArray((int)0x0000);
        this.length = toByteArray((int)(7+values.length*2));
        this.functionCode = 0x10;
        this.startingAddress = toByteArray(startingAddress);

        byte[] data = new byte[15 + values.length*2];
        data[0] = this.transactionIdentifier[1];
        data[1] = this.transactionIdentifier[0];
        data[2] = this.protocolIdentifier[1];
        data[3] = this.protocolIdentifier[0];
        data[4] = this.length[1];
        data[5] = this.length[0];
        data[6] = this.unitIdentifier;
        data[7] = this.functionCode;
        data[8] = this.startingAddress[1];
        data[9] = this.startingAddress[0];
        data[10] = quantityOfOutputs[1];
        data[11] = quantityOfOutputs[0];
        data[12] = byteCount;
        for (int i = 0; i < values.length; i++)
        {
            byte[] singleRegisterValue = toByteArray((int)values[i]);
            data[13 + i*2] = singleRegisterValue[1];
            data[14 + i*2] = singleRegisterValue[0];
        }
        if (this.serialflag)
        {
            crc = calculateCRC(data, data.length-8,6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata =null;
        if (serialflag)
        {             
        	serialdata =new byte[9+byteCount]; 
        	java.lang.System.arraycopy(data, 6,serialdata,0,9+byteCount);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 8;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
        	   receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
        if (tcpClientSocket.isConnected() | udpFlag)
        {
		if (udpFlag)
		{
			InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(500);
		    clientSocket.send(sendPacket);
		    data = new byte[2100];
		    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		    clientSocket.receive(receivePacket);
		    clientSocket.close();
		    data = receivePacket.getData();
		}
		else
		{
			outStream.write(data, 0, data.length-2);
    		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
			if (sendDataChangedListener.size() > 0)
			{
				sendData = new byte[data.length-2];
				System.arraycopy(data, 0, sendData, 0, data.length-2);
				for (SendDataChangedListener hl : sendDataChangedListener)
					hl.SendDataChanged();
			}
			data = new byte[2100];
			int numberOfBytes = inStream.read(data, 0, data.length);
			if (receiveDataChangedListener.size() > 0)
			{
				receiveData = new byte[numberOfBytes];
				System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
				for (ReceiveDataChangedListener hl : receiveDataChangedListener)
					hl.ReceiveDataChanged();
				if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
			}
		}
        }
        if (((int)(data[7] & 0xff)) == 0x90 & data[8] == 0x01)
		{
			if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
			throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
		}        
        if (((int)(data[7] & 0xff)) == 0x90 & data[8] == 0x02)
		{
			if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
			throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x90 & data[8] == 0x03)
		{
			if (debug) StoreLogData.getInstance().Store("Quantity invalid");
			throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x90 & data[8] == 0x04)
		{
			if (debug) StoreLogData.getInstance().Store("Error reading");
			throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
		}
        }
	
        /**
        * Read and Write Multiple Registers to Server
        * @param        startingAddressRead      Firts Address to Read; Shifted by -1	
        * @param        quantityRead            Number of Values to Read
        * @param        startingAddressWrite      Firts Address to write; Shifted by -1	
        * @param        values                  Values to write to Server
        * @return       Register Values from Server
        * @throws de.re.easymodbus.exceptions.ModbusException
        * @throws UnknownHostException
        * @throws SocketException
        * @throws SerialPortTimeoutException 
        * @throws SerialPortException 
        */
    public int[] ReadWriteMultipleRegisters(int startingAddressRead, int quantityRead, int startingAddressWrite, int[] values) throws de.re.easymodbus.exceptions.ModbusException,
                UnknownHostException, SocketException, IOException, SerialPortException, SerialPortTimeoutException
    {
        byte [] startingAddressReadLocal = new byte[2];
	    byte [] quantityReadLocal = new byte[2];
        byte[] startingAddressWriteLocal = new byte[2];
        byte[] quantityWriteLocal = new byte[2];
        byte writeByteCountLocal = 0;
        if (tcpClientSocket == null & !udpFlag)
            throw new de.re.easymodbus.exceptions.ConnectionException("connection error");
        if (startingAddressRead > 65535 | quantityRead > 125 | startingAddressWrite > 65535 | values.length > 121)
            throw new IllegalArgumentException("Starting address must be 0 - 65535; quantity must be 0 - 125");
        int[] response;
        this.transactionIdentifier = toByteArray((int)0x0001);
        this.protocolIdentifier = toByteArray((int)0x0000);
        this.length = toByteArray((int)0x0006);
        this.functionCode = 0x17;
        startingAddressReadLocal = toByteArray(startingAddressRead);
        quantityReadLocal = toByteArray(quantityRead);
        startingAddressWriteLocal = toByteArray(startingAddressWrite);
        quantityWriteLocal = toByteArray(values.length);
        writeByteCountLocal = (byte)(values.length * 2);
        byte[] data = new byte[19+ values.length*2];
        data[0] =               this.transactionIdentifier[1];
        data[1] =   		    this.transactionIdentifier[0];
		data[2] =   			this.protocolIdentifier[1];
		data[3] =   			this.protocolIdentifier[0];
		data[4] =   			this.length[1];
		data[5] =   			this.length[0];
		data[6] =   			this.unitIdentifier;
		data[7] =   		    this.functionCode;
		data[8] =   			startingAddressReadLocal[1];
		data[9] =   			startingAddressReadLocal[0];
		data[10] =   			quantityReadLocal[1];
		data[11] =   			quantityReadLocal[0];
        data[12] =               startingAddressWriteLocal[1];
		data[13] =   			startingAddressWriteLocal[0];
		data[14] =   			quantityWriteLocal[1];
		data[15] =   			quantityWriteLocal[0];
        data[16] =              writeByteCountLocal;

        for (int i = 0; i < values.length; i++)
        {
            byte[] singleRegisterValue = toByteArray((int)values[i]);
            data[17 + i*2] = singleRegisterValue[1];
            data[18 + i*2] = singleRegisterValue[0];
        }
        if (this.serialflag)
        {
            crc = calculateCRC(data, data.length-8,6);
            data[data.length -2] = crc[0];
            data[data.length -1] = crc[1];
        }
        byte[] serialdata =null;
        if (serialflag)
        {       
        	serialdata =new byte[13+writeByteCountLocal];  
        	java.lang.System.arraycopy(data, 6,serialdata,0,13+writeByteCountLocal);
    		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    		serialPort.writeBytes(serialdata);
    		if (debug) StoreLogData.getInstance().Store("Send Serial-Data: "+ Arrays.toString(serialdata));
           long dateTimeSend = DateTime.getDateTimeTicks();
           byte receivedUnitIdentifier = (byte)0xFF;
           serialdata = new byte[256];
           int expectedlength = 5+quantityRead;
           while (receivedUnitIdentifier != this.unitIdentifier & !((DateTime.getDateTimeTicks() - dateTimeSend) > 10000 * this.connectTimeout))
           {           
        	   serialdata = serialPort.readBytes(expectedlength, this.connectTimeout); 
               
        	   receivedUnitIdentifier = serialdata[0];
           }
           if (receivedUnitIdentifier != this.unitIdentifier)
           {
                data = new byte[256];                       
           }
        }
        if (serialdata != null)
        {
            data = new byte[262]; 
            System.arraycopy(serialdata, 0, data, 6, serialdata.length);
            if (debug) StoreLogData.getInstance().Store("Receive ModbusRTU-Data: " + Arrays.toString(data));
        }
        if (tcpClientSocket.isConnected() | udpFlag)
        {
		if (udpFlag)
		{
			InetAddress ipAddress = InetAddress.getByName(this.ipAddress);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, this.port);
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(500);
		    clientSocket.send(sendPacket);
		    data = new byte[2100];
		    DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		    clientSocket.receive(receivePacket);
		    clientSocket.close();
		    data = receivePacket.getData();
		}
		else
		{
			outStream.write(data, 0, data.length-2);
    		if (debug) StoreLogData.getInstance().Store("Send ModbusTCP-Data: "+Arrays.toString(data));   
			if (sendDataChangedListener.size() > 0)
			{
				sendData = new byte[data.length-2];
				System.arraycopy(data, 0, sendData, 0, data.length-2);
				for (SendDataChangedListener hl : sendDataChangedListener)
					hl.SendDataChanged();
			}
			data = new byte[2100];
			int numberOfBytes = inStream.read(data, 0, data.length);
			if (receiveDataChangedListener.size() > 0)
			{
				receiveData = new byte[numberOfBytes];
				System.arraycopy(data, 0, receiveData, 0, numberOfBytes);
				for (ReceiveDataChangedListener hl : receiveDataChangedListener)
					hl.ReceiveDataChanged();
				if (debug) StoreLogData.getInstance().Store("Receive ModbusTCP-Data: " + Arrays.toString(data));
			}
		}
        }
        if (((int)(data[7] & 0xff)) == 0x97 & data[8] == 0x01)
		{
			if (debug) StoreLogData.getInstance().Store("FunctionCodeNotSupportedException Throwed");
			throw new de.re.easymodbus.exceptions.FunctionCodeNotSupportedException("Function code not supported by master");
		}        
        if (((int)(data[7] & 0xff)) == 0x97 & data[8] == 0x02)
		{
			if (debug) StoreLogData.getInstance().Store("Starting adress invalid or starting adress + quantity invalid");
			throw new de.re.easymodbus.exceptions.StartingAddressInvalidException("Starting adress invalid or starting adress + quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x97 & data[8] == 0x03)
		{
			if (debug) StoreLogData.getInstance().Store("Quantity invalid");
			throw new de.re.easymodbus.exceptions.QuantityInvalidException("Quantity invalid");
		}
        if (((int)(data[7] & 0xff)) == 0x97 & data[8] == 0x04)
		{
			if (debug) StoreLogData.getInstance().Store("Error reading");
			throw new de.re.easymodbus.exceptions.ModbusException("Error reading");
		}
        response = new int[quantityRead];
        for (int i = 0; i < quantityRead; i++)
        {
            byte lowByte;
            byte highByte;
            highByte = data[9 + i * 2];
            lowByte = data[9 + i * 2 + 1];
            
            byte[] bytes = new byte[] {highByte, lowByte};
            
            
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			response[i] = byteBuffer.getShort();
        }
        return (response);
    }
    
        /**
        * Close connection to Server
        * @throws IOException
         * @throws SerialPortException 
        */
	public void Disconnect() throws IOException, SerialPortException
	{
		if (!serialflag)
		{
			if (inStream!=null)
				inStream.close();
			if (outStream!=null)
				outStream.close();
			if (tcpClientSocket != null)
				tcpClientSocket.close();
			tcpClientSocket = null;

		}
		else
		{
			if (serialPort != null)
			{
				serialPort.closePort();
			}
			
		}
		if (this.easyModbus2Mqtt != null)
		{
			if (this.easyModbus2Mqtt.isConnected())
				try {
					easyModbus2Mqtt.Disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public static byte[] toByteArray(int value)
    {
		byte[] result = new byte[2];
	    result[1] = (byte) (value >> 8);
		result[0] = (byte) (value);
	    return result;
	}

	public static byte[] toByteArrayInt(int value)
    {
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	
	public static byte[] toByteArrayLong(long value)
    {
		return ByteBuffer.allocate(8).putLong(value).array();
	}
	
	public static byte[] toByteArrayDouble(double value)
    {
		return ByteBuffer.allocate(8).putDouble(value).array();
	}
	
	public static byte[] toByteArray(float value)
    {
		 return ByteBuffer.allocate(4).putFloat(value).array();
	}
	
        /**
        * client connected to Server
        * @return  if Client is connected to Server
        */
	public boolean isConnected()
	{
		if (serialflag)
		{
			if (serialPort == null)
				return false;
			if (serialPort.isOpened())
				return true;
			else
				return false;
		}
			
		boolean returnValue = false;
		if (tcpClientSocket == null)
			returnValue = false;
		else
		{
			if (tcpClientSocket.isConnected())
				returnValue = true;
			else
				returnValue = false;
		}
		return returnValue;
	}
	
	public boolean Available(int timeout)
	{
        InetAddress address;
		try {
			address = InetAddress.getByName(this.ipAddress);
			boolean reachable = address.isReachable(timeout);
			return reachable;
		} catch (IOException e) 
		{
			e.printStackTrace();
			return false;			
		}
        
	}
	
	
	
        /**
        * Returns ip Address of Server
        * @return ip address of server
        */
	public String getipAddress()
	{
		return ipAddress;
	}
        
         /**
        * sets IP-Address of server
        * @param        ipAddress                  ipAddress of Server
        */
	public void setipAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
        /**
        * Returns port of Server listening
        * @return port of Server listening
        */
	public int getPort()
	{
		return port;
	}
        
        /**
        * sets Portof server
        * @param        port                  Port of Server
        */
	public void setPort(int port)
	{
		this.port = port;
	}	
	
        /**
        * Returns UDP-Flag which enables Modbus UDP and disabled Modbus TCP
        * @return UDP Flag
        */
	public boolean getUDPFlag()
	{
		return udpFlag;
	}
        
        /**
        * sets UDP-Flag which enables Modbus UDP and disables Mopdbus TCP
        * @param        udpFlag      UDP Flag
        */
	public void setUDPFlag(boolean udpFlag)
	{
		this.udpFlag = udpFlag;
	}
	
	public int getConnectionTimeout()
	{
		return connectTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectTimeout = connectionTimeout;
	}
        
    public void setSerialFlag(boolean serialflag)
    {
        this.serialflag = serialflag;
    }
    
    public boolean getSerialFlag()
    {
        return this.serialflag;
    }
    
    public void setUnitIdentifier(byte unitIdentifier)
    {
        this.unitIdentifier = unitIdentifier;
    }
    
    public byte getUnitIdentifier()
    {
        return this.unitIdentifier;
    }
    
    /**
    * Sets the Mqtt Root Topic
    * @param        mqttRootTopic      Mqtt Root Topic
    */
    public void setMqttRootTopic(String mqttRootTopic)
    {
    	this.mqttRootTopic = mqttRootTopic;
    }
    
    /**
    * Gets the Mqtt Root Topic
    * @return   Mqtt Root Topic
    */
    public String getMqttRootTopic()
    {
    	return this.mqttRootTopic;
    }
    
    /**
    * Sets the Mqtt Username (if required)
    * @param        mqttUserName      Mqtt Username
    */
    public void setMqttUserName(String mqttUserName)
    {
    	this.mqttUserName = mqttUserName;
    }
    
    /**
    * Gets the Mqtt UserName (if required)
    * @return  Mqtt UserName
    */
    public String getMqttUserName()
    {
    	return this.mqttUserName;
    }
    
    /**
    * Sets the Mqtt Password (if required)
    * @param        mqttPassword      Mqtt Password
    */
    public void setMqttPassword(String mqttPassword)
    {
    	this.mqttPassword = mqttPassword;
    }
    
    /**
    * Gets the Mqtt Password (if required)
    * @return  Mqtt Password
    */
    public String getMqttPassword()
    {
    	return this.mqttPassword;
    }
    
    /**
    * Sets the Mqtt Broker Port - Standard is 1883
    * @param        mqttBrokerPort      Mqtt Broker Port
    */
    public void setMqttBrokerPort(int mqttBrokerPort)
    {
    	this.mqttBrokerPort = mqttBrokerPort;
    }
    
    
    /**
    * Gets the Mqtt Broker Port - Standard is 1883
    * @return  Mqtt Broker Port
    */
    public int getMqttBrokerPort()
    {
    	return this.mqttBrokerPort;
    }
    
    /**
    * True: Values will be published only on change - FALSE: after every request
    * @param        mqttPushOnChange      True: Values will be published only on change - FALSE: after every request
    */
    public void setMqttPushOnChange(boolean mqttPushOnChange)
    {
    	this.mqttPushOnChange = mqttPushOnChange;
    }
    
    /**
    * True: Values will be published only on change - FALSE: after every request
    * @return  MQTT Push on change
    */
    public boolean getMqttPushOnChange()
    {
    	return this.mqttPushOnChange;
    }
    
    /**
    * Disables or Enables to Retain the Messages in the Broker - default is false (Enabled)
    * @param        mqttRetainMessages     Retain Messages
    */
    public void setMqttRetainMessages(boolean mqttRetainMessages)
    {
    	this.mqttRetainMessages = mqttRetainMessages;
    }
    
    /**
    * Disables or Enables to Retain the Messages in the Broker - default is false (Enabled)
    * @return  mqttRetainMessages
    */
    public boolean getMqttRetainMessages()
    {
    	return this.mqttRetainMessages;
    }
    
    /**
     * Sets and enables the Logfilename which writes information about received and send messages to File
     * @param logFileName	File name to log files
     */
    public void setLogFileName(String logFileName)
    {
    	StoreLogData.getInstance().setFilename(logFileName);
    	debug = true;
    }
        
    /**
     * Sets the Name of the serial port
     * @param serialPort Name of the Serial port
     */
    public void setSerialPort(String serialPort)
    {
    	this.serialflag = true;
    	this.comPort = serialPort;
    }
    
    /**
     * 
     * @return the Name of the Serial port
     */
    public String getSerialPort()
    {
    	return this.comPort;
    }
    
    /**
     * Sets the Baudrate for Serial connection (Modbus RTU)
     * @param baudrate Sets the Baudrate for Serial connection (Modbus RTU)
     */
    public void setBaudrate(int baudrate)
    {
    	this.baudrate = baudrate;
    }
    
    /**
     * returns the Baudrate for Serial connection (Modbus RTU)
     * @return returns the Baudrate for Serial connection (Modbus RTU)
     */
    public int getBaudrate  ()
    {
    	return this.baudrate;
    }
    
    /**
     * sets the Parity for Serial connection (Modbus RTU)
     * @param parity sets the Parity for Serial connection (Modbus RTU)
     */
    public void setParity(Parity parity)
    {
    	this.parity = parity;
    }
    
    /**
     * returns the Parity for Serial connection (Modbus RTU)
     * @return returns the Parity for Serial connection (Modbus RTU)
     */
    public Parity getParity()
    {
    	return this.parity;
    }
    
    /**
     * sets the stopbots for serial connection (Modbus RTU)
     * @param stopBits sets the Stopbits for serial connection (Modbus RTU)
     */
    public void setStopBits(StopBits stopBits)
    {
    	this.stopBits = stopBits;
    }
    
    /**
     * returns the Stopbits for serial connection (Modbus RTU)
     * @return returns the Stopbits for serial connection (Modbus RTU)
     */
    public StopBits getStopBits()
    {
    	return this.stopBits;
    }
      
    public void addReveiveDataChangedListener(ReceiveDataChangedListener toAdd) 
    {
        receiveDataChangedListener.add(toAdd);
    }
    public void addSendDataChangedListener(SendDataChangedListener toAdd) 
    {
        sendDataChangedListener.add(toAdd);
    }	
	
}                                                                                                