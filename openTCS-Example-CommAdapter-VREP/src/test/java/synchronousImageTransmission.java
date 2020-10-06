// Make sure to have CoppeliaSim running, with followig scene loaded:
//
// scenes/synchronousImageTransmissionViaRemoteApi.ttt
//
// Do not launch simulation, and make sure that the B0 resolver
// is running. Then run this program
//
// The client side (i.e. this program) depends on:
//
// coppelia/b0RemoteApi (package), which depends on:
// org/msgpack (package)
// b0 (shared library), which depends on:
// boost_chrono (shared library)
// boost_system (shared library)
// boost_thread (shared library)
// libzmq (shared library)

import coppelia.b0RemoteApi;

import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

public class synchronousImageTransmission
{
    public static boolean runInSynchronousMode=true;
    public static boolean doNextStep=true;
    public static int visionSensorHandle;
    public static int passiveVisionSensorHandle;
    public static b0RemoteApi client;
    
    public static void simulationStepStarted(final MessageUnpacker msg)
    {
        try
        {
            Map<Value,Value> map=client.readValue(msg,1).asMapValue().map();
            float simTime=map.get(ValueFactory.newString("simulationTime")).asNumberValue().toFloat();
            System.out.println("Simulation step started. Simulation time: "+simTime);
        }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
        
    public static void simulationStepDone(final MessageUnpacker msg)
    {
        try
        {
            Map<Value,Value> map=client.readValue(msg,1).asMapValue().map();
            float simTime=map.get(ValueFactory.newString("simulationTime")).asNumberValue().toFloat();
            System.out.println("Simulation step done. Simulation time: "+simTime);
            doNextStep=true;
        }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
        
    public static void imageCallback(final MessageUnpacker msg)
    {
        try
        {
            byte[] img=client.readByteArray(msg,2);
            client.simxSetVisionSensorImage(passiveVisionSensorHandle,false,img,client.simxDefaultPublisher());
            System.out.println("Received image.");
        }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
    
    public static void stepSimulation()
    {
        try
        {
            if (runInSynchronousMode)
            {
                while (!doNextStep)
                    client.simxSpinOnce();
                doNextStep=false;
                client.simxSynchronousTrigger();
            }
            else
                client.simxSpinOnce();
        }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
        
    public static void main(String[] args) throws IOException
    {
        
        client = new b0RemoteApi();
        client.simxAddStatusbarMessage("Hello world!",client.simxDefaultPublisher());
        MessageUnpacker msg=client.simxGetObjectHandle("VisionSensor",client.simxServiceCall());
        visionSensorHandle=client.readInt(msg,1);
        msg=client.simxGetObjectHandle("PassiveVisionSensor",client.simxServiceCall());
        passiveVisionSensorHandle=client.readInt(msg,1);
        
        if (runInSynchronousMode)
            client.simxSynchronous(true);
        
        client.simxGetVisionSensorImage(visionSensorHandle,false,client.simxDefaultSubscriber(synchronousImageTransmission::imageCallback));
//        client.simxGetVisionSensorImage(visionSensorHandle,false,client.simxCreateSubscriber(synchronousImageTransmission::imageCallback,1,true));
        client.simxGetSimulationStepStarted(client.simxDefaultSubscriber(synchronousImageTransmission::simulationStepStarted));
        client.simxGetSimulationStepDone(client.simxDefaultSubscriber(synchronousImageTransmission::simulationStepDone));
        client.simxStartSimulation(client.simxDefaultPublisher());
        
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()<startTime+5000)
            stepSimulation();
        
        client.simxStopSimulation(client.simxDefaultPublisher());
        
        client.delete();
        System.out.println("Program ended");
    }
}
