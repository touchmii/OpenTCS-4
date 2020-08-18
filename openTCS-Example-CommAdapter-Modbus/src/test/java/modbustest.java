import de.re.easymodbus.modbusclient.*;
public class modbustest {
    public static void main(String[] args) {
        ModbusClient modbusClient = new ModbusClient("127.0.0.1",1502);
        int path[] = {0, 0, 3, 4, 64, 40, 365, 5, 315, 40, 365, 6, 350, -200, 365};
        try{
            modbusClient.Connect();
//            modbusClient.WriteSigleCoil(0,true);
            modbusClient.WriteMultipleRegisters(100, path);
//            modbusClient.

        }
        catch(Exception e)
        {}
    }
}