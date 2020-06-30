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
package de.re.easymodbus.modbusserver;

import java.io.IOException;
import java.util.Calendar;

import de.re.easymodbus.mqtt.EasyModbus2Mqtt;

     /**
     * @author Stefan Ro√ümann
     */
public class ModbusServer extends Thread
{
    private int port = 502;
    protected ModbusProtocoll receiveData;
    protected ModbusProtocoll sendData =  new ModbusProtocoll();
    @Deprecated
    public int[] holdingRegisters = new int[65535];
    @Deprecated
    public int[] inputRegisters = new int[65535];
    @Deprecated
    public boolean[] coils = new boolean[65535];
    @Deprecated
    public boolean[] discreteInputs = new boolean[65535];
    private DataModel dataModel = new DataModel();
    private int numberOfConnections = 0;
    public boolean udpFlag;
    private int clientConnectionTimeout = 10000;
    

    private ModbusProtocoll[] modbusLogData = new ModbusProtocoll[100];
    private boolean functionCode1Disabled;
    private boolean functionCode2Disabled;
    private boolean functionCode3Disabled;
    private boolean functionCode4Disabled;
    private boolean functionCode5Disabled;
    private boolean functionCode6Disabled;
    private boolean functionCode15Disabled;
    private boolean functionCode16Disabled;
    private boolean serverRunning;
    private ListenerThread listenerThread;
    
    
    //********************************************************
    //***               Events                             ***
    //********************************************************
    protected ICoilsChangedDelegator notifyCoilsChanged;
    protected IHoldingRegistersChangedDelegator notifyHoldingRegistersChanged;
    protected INumberOfConnectedClientsChangedDelegator notifyNumberOfConnectedClientsChanged;
    protected ILogDataChangedDelegator notifyLogDataChanged;
    
    public ModbusServer() 
    {
		System.out.println("EasyModbus Server Library");
		System.out.println("Copyright (c) Stefan Rossmann Engineering Solutions");
		System.out.println("www.rossmann-engineering.de");
		System.out.println("");
		System.out.println("Creative commons license");
		System.out.println("Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0)");
        dataModel.easyModbus2Mqtt.setMqttRootTopic("easymodbusserver");
        dataModel.easyModbus2Mqtt.setRetainMessages(true);
        dataModel.easyModbus2Mqtt.setMqttBrokerAddress(null);
	}
    
    @SuppressWarnings("deprecation")
    protected void finalize()
    {
        serverRunning = false;
        listenerThread.stop();
    }
    
    
     /**
     * Method opens port and starts listening for incomming requests from client.
     * @throws IOException
     */
    public void Listen() throws IOException
    {
        /*
        coils[1] = true;
        coils[10] = true;
        coils[8] = true;
        holdingRegisters[0] = 500;
        holdingRegisters[1] = 500;
        holdingRegisters[2] = 5000;
        holdingRegisters[3] = 5005;
        holdingRegisters[4] = 500;
        holdingRegisters[5] = 500;
        holdingRegisters[6] = 500;
        holdingRegisters[7] = 500;*/
    	
    	serverRunning = true;
    	listenerThread = new ListenerThread(this);
    	listenerThread.start();  
    }
    
     /**
     * Stops the Modbus Server
     */
    @SuppressWarnings("deprecation")
	public void StopListening()
    {
            serverRunning = false;
            listenerThread.stop();
    }
    
    protected void CreateAnswer(java.net.Socket socket)
    {

        switch (receiveData.functionCode)
        {
            // Read Coils
            case 1:
                if (!functionCode1Disabled)
                    this.ReadCoils(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                break;
            // Read Input Registers
          case 2:
                if (!functionCode2Disabled)
                    this.ReadDiscreteInputs(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                
                break;
            // Read Holding Registers
            case 3:
                if (!functionCode3Disabled)
                    this.ReadHoldingRegisters(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                
                break;
            // Read Input Registers
            case 4:
                if (!functionCode4Disabled)
                    this.ReadInputRegisters(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                
                break;
            // Write single coil
            case 5:
                if (!functionCode5Disabled)
                    this.WriteSingleCoil(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                
                break;
            // Write single register
            case 6:
                if (!functionCode6Disabled)
                    this.WriteSingleRegister(socket);
                else
                {
                    sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                }
                
                    break;
            // Write Multiple coils
            case 15:
                    if (!functionCode15Disabled)
                        this.WriteMultipleCoils(socket);
                    else
                    {
                        sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
                        sendData.exceptionCode = 1;
                        sendException(sendData.errorCode, sendData.exceptionCode, socket);
                    }

                    break;
            // Write Multiple registers
            case 16:
                    if (!functionCode16Disabled)
                        this.WriteMultipleRegisters(socket);
                    else
                    {
                        sendData.errorCode = (byte)(receiveData.functionCode + 0x90);
                        sendData.exceptionCode = 1;
                        sendException(sendData.errorCode, sendData.exceptionCode, socket);
                    }

                    break;
            // Error: Function Code not supported
            default: sendData.errorCode = (byte) (receiveData.functionCode + 0x80);
                    sendData.exceptionCode = 1;
                    sendException(sendData.errorCode, sendData.exceptionCode, socket);
                    break;
                     
        }
        sendData.timeStamp = Calendar.getInstance();
    }
    
    private void ReadCoils(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        if ((receiveData.quantity < 1) | (receiveData.quantity > 0x07D0))  //Invalid quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1 + receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        if ((receiveData.quantity % 8) == 0)
            sendData.byteCount = (byte)(receiveData.quantity / 8);
        else
            sendData.byteCount = (byte)(receiveData.quantity / 8 + 1);

        sendData.sendCoilValues = new boolean[receiveData.quantity];

        System.arraycopy(coils, receiveData.startingAdress + 1, sendData.sendCoilValues, 0, sendData.sendCoilValues.length);

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[9 + sendData.byteCount];
            byte[] byteData = new byte[2];

            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;

            //ByteCount
            data[8] = (byte)(sendData.byteCount & 0xff);

            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendCoilValues = null;
            }

            if (sendData.sendCoilValues != null)
                for (int i = 0; i < (sendData.byteCount); i++)
                {
                    byteData = new byte[2];
                    for (int j = 0; j < 8; j++)
                    {

                        byte boolValue;
                        if (sendData.sendCoilValues[i * 8 + j] == true)
                            boolValue = 1;
                        else
                            boolValue = 0;
                        byteData[1] = (byte)((byteData[1]) | (boolValue << j));
                        if ((i * 8 + j + 1) >= sendData.sendCoilValues.length)
                            break;
                    }
                    data[9 + i] = byteData[1];
                }  
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
    }
    
    private void ReadDiscreteInputs(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        if ((receiveData.quantity < 1) | (receiveData.quantity > 0x07D0))  //Invalid quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1 + receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        if ((receiveData.quantity % 8) == 0)
            sendData.byteCount = (byte)(receiveData.quantity / 8);
        else
            sendData.byteCount = (byte)(receiveData.quantity / 8 + 1);

        sendData.sendCoilValues = new boolean[receiveData.quantity];
        System.arraycopy(discreteInputs, receiveData.startingAdress + 1, sendData.sendCoilValues, 0, receiveData.quantity);


            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[9 + sendData.byteCount];
            byte[] byteData = new byte[2];
            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;

            //ByteCount
            data[8] = (byte)(sendData.byteCount & 0xff);


            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendCoilValues = null;
            }

            if (sendData.sendCoilValues != null)
                for (int i = 0; i < (sendData.byteCount); i++)
                {
                    byteData = new byte[2];
                    for (int j = 0; j < 8; j++)
                    {

                        byte boolValue;
                        if (sendData.sendCoilValues[i * 8 + j] == true)
                            boolValue = 1;
                        else
                            boolValue = 0;
                        byteData[1] = (byte)((byteData[1]) | (boolValue << j));
                        if ((i * 8 + j + 1) >= sendData.sendCoilValues.length)
                            break;
                    }
                    data[9 + i] = byteData[1];
                }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
      
    }
    
    private void ReadHoldingRegisters(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        if ((receiveData.quantity < 1) | (receiveData.quantity > 0x007D))  //Invalid quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1 + receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        sendData.byteCount = 
                (short)(2 * receiveData.quantity);
        sendData.sendRegisterValues = new int[receiveData.quantity];
        System.arraycopy(holdingRegisters, receiveData.startingAdress + 1, sendData.sendRegisterValues, 0, receiveData.quantity);

        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = (short)(0x03 + sendData.byteCount);


            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[9 + sendData.byteCount];
            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;

            //ByteCount
            data[8] = (byte)(sendData.byteCount & 0xff);

            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }


            if (sendData.sendRegisterValues != null)
                for (int i = 0; i < (sendData.byteCount / 2); i++)
                {
                    data[9 + i * 2] = (byte)((sendData.sendRegisterValues[i] & 0xff00)>>8);
                    data[10 + i * 2] = (byte)(sendData.sendRegisterValues[i] & 0xff);
                }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
    }
    
    private void ReadInputRegisters(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        if ((receiveData.quantity < 1) | (receiveData.quantity > 0x007D))  //Invalid quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1 + receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        sendData.byteCount = (short)(2 * receiveData.quantity);
        sendData.sendRegisterValues = new int[receiveData.quantity];
        System.arraycopy(inputRegisters, receiveData.startingAdress +1, sendData.sendRegisterValues, 0, receiveData.quantity);

        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = (short)(0x03 + sendData.byteCount);

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[9 + sendData.byteCount];
            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;

            //ByteCount
            data[8] = (byte)(sendData.byteCount & 0xff);

            
            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }


            if (sendData.sendRegisterValues != null)
                for (int i = 0; i < (sendData.byteCount / 2); i++)
                {
                    data[9 + i * 2] = (byte)((sendData.sendRegisterValues[i] & 0xff00)>>8);
                    data[10 + i * 2] = (byte)(sendData.sendRegisterValues[i] & 0xff);
                }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
    }
   
    private void WriteSingleCoil(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        sendData.startingAdress = receiveData.startingAdress;
        sendData.receiveCoilValues = receiveData.receiveCoilValues;
        if ((receiveData.receiveCoilValues[0] != 0x0000) & (receiveData.receiveCoilValues[0] != 0xFF))  //Invalid Value
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        if ((receiveData.receiveCoilValues[0]) > 0)
        {
        	this.setCoil(true, receiveData.startingAdress+1);
        }
        if (receiveData.receiveCoilValues[0] == 0x0000)
        {
        	this.setCoil(false, receiveData.startingAdress+1);
        }
        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = 0x06;

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[12];

            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;



            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }
            else
            {
                data[8] = (byte)((receiveData.startingAdress & 0xff00)>>8);
                data[9] = (byte)(receiveData.startingAdress & 0xff);

                data[10] = (byte)((receiveData.receiveCoilValues[0]));
                data[11] = 0;
            }
            
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
            if (this.notifyCoilsChanged != null)
                notifyCoilsChanged.coilsChangedEvent();
    }
    
    private void WriteSingleRegister(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        sendData.startingAdress = receiveData.startingAdress;
        sendData.receiveRegisterValues = receiveData.receiveRegisterValues;
       
        if ((receiveData.receiveRegisterValues[0] < 0x0000) | (receiveData.receiveRegisterValues[0] > 0xFFFF))  //Invalid Value
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if ((receiveData.startingAdress + 1) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        this.setHoldingRegister(((int)receiveData.receiveRegisterValues[0]), receiveData.startingAdress+1);
        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = 0x06;

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[12];

            sendData.length = (byte)(data.length - 6);


            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;



            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }
            else
            {
                data[8] = (byte)((receiveData.startingAdress & 0xff00)>>8);
                data[9] = (byte)(receiveData.startingAdress & 0xff);

                data[10] = (byte)((receiveData.receiveRegisterValues[0] & 0xff00)>>8);
                data[11] = (byte)(receiveData.receiveRegisterValues[0] & 0xff);
            }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
             if (this.notifyHoldingRegistersChanged != null)
                notifyHoldingRegistersChanged.holdingRegistersChangedEvent();
    }
 
    private void WriteMultipleCoils(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        sendData.startingAdress = receiveData.startingAdress;
        sendData.quantity = receiveData.quantity;
        
        if ((receiveData.quantity == 0x0000) | (receiveData.quantity > 0x07B0))  //Invalid Quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 3;
        }
        if (((int)receiveData.startingAdress + 1 + (int)receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x80);
            sendData.exceptionCode = 2;
        }
        for (int i = 0; i < receiveData.quantity; i++)
        {
            int shift = i % 16;
            /*
            if ((i == receiveData.quantity - 1) & (receiveData.quantity % 2 != 0))
            {
                if (shift < 8)
                    shift = shift + 8;
                else
                    shift = shift - 8;
            }
            */
            int mask = 0x1;
            mask = mask << (shift);
            if ((receiveData.receiveCoilValues[i / 16] & mask) == 0)
            	this.setCoil(false, receiveData.startingAdress + i + 1);
            else
            	this.setCoil(true, receiveData.startingAdress + i + 1);
        }
        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = 0x06;

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[12];

            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;



            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }
            else
            {
                data[8] = (byte)((receiveData.startingAdress & 0xff00)>>8);
                data[9] = (byte)(receiveData.startingAdress & 0xff);

                data[10] = (byte)((receiveData.quantity & 0xff00)>>8);
                data[11] = (byte)(receiveData.quantity & 0xff);
            }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (Exception e) {
                                
	            
					e.printStackTrace();
				} 
            if (this.notifyCoilsChanged != null)
                notifyCoilsChanged.coilsChangedEvent();
    }
    
    private void WriteMultipleRegisters(java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.functionCode = receiveData.functionCode;
        sendData.startingAdress = receiveData.startingAdress;
        sendData.quantity = receiveData.quantity;

        if ((receiveData.quantity == 0x0000) | (receiveData.quantity > 0x07B0))  //Invalid Quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x90);
            sendData.exceptionCode = 3;
        }
        if (((int)receiveData.startingAdress + 1 + (int)receiveData.quantity) > 65535)    //Invalid Starting adress or Starting address + quantity
        {
            sendData.errorCode = (byte)(receiveData.functionCode + 0x90);
            sendData.exceptionCode = 2;
        }
        for (int i = 0; i < receiveData.quantity; i++)
        {
        	this.setHoldingRegister((receiveData.receiveRegisterValues[i]), receiveData.startingAdress + i + 1);
        }
        if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = 0x06;

            byte[] data;
            if (sendData.exceptionCode > 0)
                data = new byte[9];
            else
                data = new byte[12];

            sendData.length = (byte)(data.length - 6);

            //Send Transaction identifier
            data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
            data[1] = (byte)(sendData.transactionIdentifier & 0xff);

            //Send Protocol identifier
            data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
            data[3] = (byte)(sendData.protocolIdentifier & 0xff);

            //Send length
            data[4] = (byte)((sendData.length & 0xff00)>>8);
            data[5] = (byte)(sendData.length & 0xff);

            //Unit Identifier
            data[6] = sendData.unitIdentifier;

            //Function Code
            data[7] = sendData.functionCode;



            if (sendData.exceptionCode > 0)
            {
                data[7] = sendData.errorCode;
                data[8] = sendData.exceptionCode;
                sendData.sendRegisterValues = null;
            }
            else
            {
                data[8] = (byte)((receiveData.startingAdress & 0xff00)>>8);
                data[9] = (byte)(receiveData.startingAdress & 0xff);

                data[10] = (byte)((receiveData.quantity & 0xff00)>>8);
                data[11] = (byte)(receiveData.quantity & 0xff);
            }
            java.io.OutputStream outputStream;
            if (socket.isConnected() & !socket.isClosed())
				try {
					outputStream = socket.getOutputStream();
					outputStream.write(data);
				} catch (IOException e) {
					
					e.printStackTrace();
				} 
             if (this.notifyHoldingRegistersChanged != null)
                notifyHoldingRegistersChanged.holdingRegistersChangedEvent();
    }
    
    
    private void sendException(int errorCode, int exceptionCode, java.net.Socket socket)
    {
        sendData = new ModbusProtocoll();
        sendData.response = true;

        sendData.transactionIdentifier = receiveData.transactionIdentifier;
        sendData.protocolIdentifier = receiveData.protocolIdentifier;

        sendData.unitIdentifier = receiveData.unitIdentifier;
        sendData.errorCode = (byte)errorCode;
        sendData.exceptionCode = (byte)exceptionCode;

         if (sendData.exceptionCode > 0)
            sendData.length = 0x03;
        else
            sendData.length = (short)(0x03 + sendData.byteCount);


             byte[] data;
             if (sendData.exceptionCode > 0)
                 data = new byte[9];
             else
                 data = new byte[9 + sendData.byteCount];
             sendData.length = (byte)(data.length - 6);

             //Send Transaction identifier
             data[0] = (byte)((sendData.transactionIdentifier & 0xff00)>>8);
             data[1] = (byte)(sendData.transactionIdentifier & 0xff);

             //Send Protocol identifier
             data[2] = (byte)((sendData.protocolIdentifier & 0xff00)>>8);
             data[3] = (byte)(sendData.protocolIdentifier & 0xff);

             //Send length
             data[4] = (byte)((sendData.length & 0xff00)>>8);
             data[5] = (byte)(sendData.length & 0xff);

             //Unit Identifier
             data[6] = sendData.unitIdentifier;


             data[7] = sendData.errorCode;
             data[8] = sendData.exceptionCode;   
             java.io.OutputStream outputStream;
             if (socket.isConnected() & !socket.isClosed())
             try {
                outputStream = socket.getOutputStream();
		outputStream.write(data);
             } catch (IOException e) {
					
		e.printStackTrace();
             } 
    }
    
    protected void CreateLogData()
    {
        for (int i = 0; i < 98; i++)
        {
            modbusLogData[99 - i] = modbusLogData[99 - i - 2];
        }
            modbusLogData[0] = receiveData;
            modbusLogData[1] = sendData;
            if (this.notifyLogDataChanged != null)
                this.notifyLogDataChanged.logDataChangedEvent();
        }
    
     /**
     * Sets the Port for the ModbusServer
     * @param port	port for Server
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    /**
     * Disables or Enables Modbus Function code 1
     * @param functionCode1Disabled	true disables Function code 1
     * 								false enables Function code 1
     */
    public void setFunctionCode1Disabled(boolean functionCode1Disabled)
    {
    	this.functionCode1Disabled = functionCode1Disabled;
    }
 
    /**
     * Disables or Enables Modbus Function code 2
     * @param functionCode2Disabled	true disables Function code 2
     * 								false enables Function code 2
     */
    public void setFunctionCode2Disabled(boolean functionCode2Disabled)
    {
    	this.functionCode2Disabled = functionCode2Disabled;
    }
       
    /**
     * Disables or Enables Modbus Function code 3
     * @param functionCode3Disabled	true disables Function code 3
     * 								false enables Function code 3
     */
    public void setFunctionCode3Disabled(boolean functionCode3Disabled)
    {
    	this.functionCode3Disabled = functionCode3Disabled;
    }
    
    /**
     * Disables or Enables Modbus Function code 4
     * @param functionCode4Disabled	true disables Function code 4
     * 								false enables Function code 4
     */
    public void setFunctionCode4Disabled(boolean functionCode4Disabled)
    {
    	this.functionCode4Disabled = functionCode4Disabled;
    }
    
    /**
     * Disables or Enables Modbus Function code 5
     * @param functionCode5Disabled	true disables Function code 5
     * 								false enables Function code 5
     */
    public void setFunctionCode5Disabled(boolean functionCode5Disabled)
    {
    	this.functionCode5Disabled = functionCode5Disabled;
    }
    
    /**
     * Disables or Enables Modbus Function code 6
     * @param functionCode6Disabled	true disables Function code 6
     * 								false enables Function code 6
     */
    public void setFunctionCode6Disabled(boolean functionCode6Disabled)
    {
    	this.functionCode6Disabled = functionCode6Disabled;
    }
    
    /**
     * Disables or Enables Modbus Function code 15
     * @param functionCode15Disabled	true disables Function code 15
     * 								false enables Function code 15
     */
    public void setFunctionCode15Disabled(boolean functionCode15Disabled)
    {
    	this.functionCode15Disabled = functionCode15Disabled;
    }
    
    /**
     * Disables or Enables Modbus Function code 16
     * @param functionCode16Disabled	true disables Function code 16
     * 								false enables Function code 16
     */
    public void setFunctionCode16Disabled(boolean functionCode16Disabled)
    {
    	this.functionCode16Disabled = functionCode16Disabled;
    }
    
    public void setNumberOfConnectedClients(int value)
    {
        this.numberOfConnections = value;
        if (this.notifyNumberOfConnectedClientsChanged != null)
            this.notifyNumberOfConnectedClientsChanged.NumberOfConnectedClientsChanged();
    }
    
    
     /**
     * Gets the Port for the ModbusServer
     * @return Current Port Server is listening to
     */
    public int getPort()
    {
        return this.port;
    }
    
    public boolean getFunctionCode1Disabled()
    {
    	return this.functionCode1Disabled;
    }
    
    public boolean getFunctionCode2Disabled()
    {
    	return this.functionCode2Disabled;
    }
    
    public boolean getFunctionCode3Disabled()
    {
    	return this.functionCode3Disabled;
    }
    
    public boolean getFunctionCode4Disabled()
    {
    	return this.functionCode4Disabled;
    }
    
    public boolean getFunctionCode5Disabled()
    {
    	return this.functionCode5Disabled;
    }
    
    public boolean getFunctionCode6Disabled()
    {
    	return this.functionCode6Disabled;
    }
    
    public boolean getFunctionCode15Disabled()
    {
    	return this.functionCode15Disabled;
    }
    
    public boolean getFunctionCode16Disabled()
    {
    	return this.functionCode16Disabled;
    }
    
     /**
     * Returns number of connected clients
     * @return number of connected clients
     */
    public int getNumberOfConnectedClients()
    {
        return this.numberOfConnections;
    }
    
     /**
     * Gets Server runnig flag
     * @return TRUE if server active
     */
    public boolean getServerRunning()
    {
        return this.serverRunning;
    }
    
     /**
     * Gets logged Modbus data
     * @return logged Modbus data
     */
    public ModbusProtocoll[] getLogData()
    {
        return this.modbusLogData;
    }
    
     /**
     * Get notified if number if Coils has changed
     * @param value Implementation of Interface ICoilsChangedDelegator
     */
    public void setNotifyCoilsChanged(ICoilsChangedDelegator value)
    {
        this.notifyCoilsChanged = value;
    }
    
     /**
     * Get notified if number if Holding Registers has changed
     * @param value Implementation of Interface IHoldingRegistersChangedDelegator
     */
    public void setNotifyHoldingRegistersChanged(IHoldingRegistersChangedDelegator value)
    {
        this.notifyHoldingRegistersChanged = value;
    }
    
     /**
     * Get notified if number of connected clients has changed
     * @param value Implementation of Interface INumberOfConnectedClientsChangedDelegator
     */
    public void setNotifyNumberOfConnectedClientsChanged(INumberOfConnectedClientsChangedDelegator value)
    {
        this.notifyNumberOfConnectedClientsChanged = value;
    }
    
     /**
     * Get notified if Log Data has changed
     * @param value Implementation of Interface ILogDataChangedDelegator
     */
    public void setNotifyLogDataChanged(ILogDataChangedDelegator value)
    {
        this.notifyLogDataChanged = value;
    }
    
    /**
    * Gets the Client connection timeout, which disconnects a connection to a client
    * @return clientConnectionTimout
    */
    public int getClientConnectionTimeout()
    {
    	return clientConnectionTimeout;
    }
    
    /*Sets the Client connection timeout, which disconnects a connection to a client
    * @param value ClientConnectionTimeout
    */
    public void setClientConnectionTimeout(int value)
    {
    	clientConnectionTimeout = value;
    }

	public String getMqttBrokerAddress() {
		return dataModel.easyModbus2Mqtt.getMqttBrokerAddress();
	}

	public void setMqttBrokerAddress(String mqttBrokerAddress) 
	{
		dataModel.easyModbus2Mqtt.setMqttBrokerAddress(mqttBrokerAddress);
	}

	public int getMqttBrokerPort() {
		return dataModel.easyModbus2Mqtt.getMqttBrokerPort();
	}

	public void setMqttBrokerPort(int mqttBrokerPort) {
		dataModel.easyModbus2Mqtt.setMqttBrokerPort(mqttBrokerPort);
	}

	public String getMqttRootTopic() {
		return dataModel.easyModbus2Mqtt.getMqttRootTopic();
	}

	public void setMqttRootTopic(String mqttRootTopic) {
		dataModel.easyModbus2Mqtt.setMqttRootTopic(mqttRootTopic);
	}

	public String getMqttUserName() {
		return dataModel.easyModbus2Mqtt.getMqttUserName();
	}

	public void setMqttUserName(String mqttUserName) {
		dataModel.easyModbus2Mqtt.setMqttUserName(mqttUserName);
	}

	public String getMqttPassword() {
		return dataModel.easyModbus2Mqtt.getMqttPassword();
	}

	public void setMqttPassword(String mqttPassword) {
		dataModel.easyModbus2Mqtt.setMqttPassword(mqttPassword);
	}

	public void setRetainMessages(boolean retainMessages) {
		dataModel.easyModbus2Mqtt.setRetainMessages(retainMessages);
	}

	public int getHoldingRegister(int address) 
	{
		return dataModel.getHoldingRegister(address);
	}

	public void setHoldingRegister(int holdingRegisterValue, int address) 
	{
		this.dataModel.setHoldingRegister(address, holdingRegisterValue);
		this.holdingRegisters[address] = holdingRegisterValue;
		
	}
	
	public int getInputRegister(int address) 
	{
		return dataModel.getInputRegister(address);
	}

	public void setInputRegister(int inputRegisterValue, int address) 
	{
		this.dataModel.setInputRegister(address, inputRegisterValue);
		this.inputRegisters[address] = inputRegisterValue;
	}

	public boolean getCoil(int address) 
	{
		return dataModel.getCoil(address);
	}

	public void setCoil(boolean coilValue, int address) 
	{
		this.dataModel.setCoil(address, coilValue);
		this.coils[address] = coilValue;
	}
 
	public boolean getDiscreteInput(int address) 
	{
		return dataModel.getDiscreteInput(address);
	}

	public void setDiscreteInput(boolean discreteInputValue, int address) 
	{
		this.dataModel.setDiscreteInput(address, discreteInputValue);
		this.discreteInputs[address] = discreteInputValue;
	}
}
