package com.lvsrobot.vehicle;

import com.github.zengfr.easymodbus4j.client.ModbusClientTcpFactory;
import com.github.zengfr.easymodbus4j.handle.impl.ModbusMasterResponseHandler;
import com.github.zengfr.easymodbus4j.client.ModbusClient;
import com.github.zengfr.easymodbus4j.processor.ModbusMasterResponseProcessor;
import com.github.zengfr.easymodbus4j.sender.ChannelSender;
import com.github.zengfr.easymodbus4j.sender.ChannelSenderFactory;
import io.netty.channel.Channel;
import com.lvsrobot.http.ExampleModbusMasterResponseProcessor;

public class nettytest {

    public static void main(String[] args) throws Exception {
        short a = 0;
        ModbusMasterResponseProcessor processor = new ExampleModbusMasterResponseProcessor(a);
    ModbusMasterResponseHandler handler = new ModbusMasterResponseHandler(processor);
    ModbusClient modbusClient = ModbusClientTcpFactory.getInstance().createClient4Master("127.0.0.1", 1502, handler);
    Thread.sleep(3000);
    Channel channel = modbusClient.getChannel();
    ChannelSender sender = ChannelSenderFactory.getInstance().get(channel);
    sender.readHoldingRegisters(0, 30);
//    sender.readCoils(2, 1);
    }



}
