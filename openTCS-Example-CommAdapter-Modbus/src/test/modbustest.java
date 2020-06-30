import de.re.easymodbus.modbusclient.*;
public class modbustest {
    public static void main(String[] args) {
        ModbusClient modbusClient = new ModbusClient("127.0.0.1",1502);
        try{
            modbusClient.Connet();
            modbusClient.WriteSigleCoil(0,true);
//            modbusClient.

        }
        catch(Exception e)
        {}
    }
}