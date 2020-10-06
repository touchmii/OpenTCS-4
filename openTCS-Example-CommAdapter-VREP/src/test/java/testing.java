import coppelia.b0RemoteApi;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

public class testing
{
    public static void callb(final MessageUnpacker msg)
    {
        try
        {
             b0RemoteApi.print(msg);
        }
        catch(IOException e) { throw new UncheckedIOException(e); }
    }
        
    public static void main(String[] args) throws IOException,InterruptedException
    {
        
        b0RemoteApi client = new b0RemoteApi();

        int[] colPurple={255,0,255};
        int s1=b0RemoteApi.readInt(client.simxGetObjectHandle("shape1",client.simxServiceCall()),1);
        int s2=b0RemoteApi.readInt(client.simxGetObjectHandle("shape2",client.simxServiceCall()),1);
        int prox=b0RemoteApi.readInt(client.simxGetObjectHandle("prox",client.simxServiceCall()),1);
        int vis=b0RemoteApi.readInt(client.simxGetObjectHandle("vis",client.simxServiceCall()),1);
        int fs=b0RemoteApi.readInt(client.simxGetObjectHandle("fs",client.simxServiceCall()),1);
        int coll=b0RemoteApi.readInt(client.simxGetCollisionHandle("coll",client.simxServiceCall()),1);
        int dist=b0RemoteApi.readInt(client.simxGetDistanceHandle("dist",client.simxServiceCall()),1);
        /*
        client.simxAddStatusbarMessage("Hello",client.simxDefaultPublisher());
        int[] pos={10,400};
        int[] size={1024,100};
        float[] tCol={1.0f,1.0f,0.0f};
        float[] bCol={0.0f,0.0f,0.0f};
        int ch=b0RemoteApi.readInt(client.simxAuxiliaryConsoleOpen("theTitle",50,4,pos,size,tCol,bCol,client.simxServiceCall()),1);
        client.simxAuxiliaryConsolePrint(ch,"Hello World!!!\n",client.simxServiceCall());
        client.simxSleep(1000);
        client.simxAuxiliaryConsoleShow(ch,false,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxAuxiliaryConsoleShow(ch,true,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxAuxiliaryConsoleClose(ch,client.simxServiceCall());
        client.simxStartSimulation(client.simxServiceCall());
        client.simxStopSimulation(client.simxServiceCall());
        //*/
                /*
        float[] coords1={0.0f,0.0f,0.0f,1.0f,0.0f,0.0f,0.0f,0.0f,1.0f};
        MessageUnpacker res=client.simxAddDrawingObject_points(8,colPurple,coords1,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxRemoveDrawingObject(b0RemoteApi.readInt(res,1),client.simxServiceCall());
        int[] colRed={255,0,0};
        res=client.simxAddDrawingObject_spheres(0.05f,colRed,coords1,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxRemoveDrawingObject(b0RemoteApi.readInt(res,1),client.simxServiceCall());
        res=client.simxAddDrawingObject_cubes(0.05f,colRed,coords1,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxRemoveDrawingObject(b0RemoteApi.readInt(res,1),client.simxServiceCall());
        int[] colGreen={0,255,0};
        float[] coords2={0.0f,0.0f,0.0f,1.0f,0.0f,0.0f, 1.0f,0.0f,0.0f,0.0f,0.0f,1.0f, 0.0f,0.0f,1.0f,0.0f,0.0f,0.0f};
        res=client.simxAddDrawingObject_segments(4,colGreen,coords2,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxRemoveDrawingObject(b0RemoteApi.readInt(res,1),client.simxServiceCall());
        int[] colOrange={255,128,0};
        res=client.simxAddDrawingObject_triangles(colOrange,coords1,client.simxServiceCall());
        client.simxSleep(1000);
        client.simxRemoveDrawingObject(b0RemoteApi.readInt(res,1),client.simxServiceCall());
    //*/
    /*
        client.simxStartSimulation(client.simxServiceCall());
        b0RemoteApi.print(client.simxCheckCollision(s1,s2,client.simxServiceCall()));
        b0RemoteApi.print(client.simxCheckDistance(s1,s2,0.0f,client.simxServiceCall()));
        b0RemoteApi.print(client.simxCheckProximitySensor(prox,s2,client.simxServiceCall()));
        b0RemoteApi.print(client.simxCheckVisionSensor(vis,s2,client.simxServiceCall()));
        b0RemoteApi.print(client.simxReadCollision(coll,client.simxServiceCall()));
        b0RemoteApi.print(client.simxReadDistance(dist,client.simxServiceCall()));
        b0RemoteApi.print(client.simxReadProximitySensor(prox,client.simxServiceCall()));
        b0RemoteApi.print(client.simxReadVisionSensor(vis,client.simxServiceCall()));
        b0RemoteApi.print(client.simxReadForceSensor(fs,client.simxServiceCall()));
        b0RemoteApi.print(client.simxBreakForceSensor(fs,client.simxServiceCall()));
        client.simxSleep(1000);
        client.simxStopSimulation(client.simxServiceCall());
                //*/
                /*
        client.simxSetFloatSignal("floatSignal",123.456f,client.simxServiceCall());
        client.simxSetIntegerSignal("integerSignal",59,client.simxServiceCall());
        client.simxSetStringSignal("stringSignal",("Hello World").getBytes(),client.simxServiceCall());
        b0RemoteApi.print(client.simxGetFloatSignal("floatSignal",client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetIntegerSignal("integerSignal",client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetStringSignal("stringSignal",client.simxServiceCall()));
        client.simxSleep(1000);
        client.simxClearFloatSignal("floatSignal",client.simxServiceCall());
        client.simxClearIntegerSignal("integerSignal",client.simxServiceCall());
        client.simxClearStringSignal("stringSignal",client.simxServiceCall());
        client.simxSleep(1000);
        b0RemoteApi.print(client.simxGetFloatSignal("floatSignal",client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetIntegerSignal("integerSignal",client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetStringSignal("stringSignal",client.simxServiceCall()));

        client.simxCheckProximitySensor(prox,s2,client.simxDefaultSubscriber(testing::callb));
        long startTime=client.simxGetTimeInMs();
        while (client.simxGetTimeInMs()<startTime+5000)
            client.simxSpinOnce();
        //*/
       /* 
        float[] pos={0.0f,0.0f,0.2f};
        b0RemoteApi.print(client.simxSetObjectPosition(s1,-1,pos,client.simxServiceCall()));
        client.simxSleep(1000);
        b0RemoteApi.print(client.simxSetObjectOrientation(s1,-1,pos,client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetObjectOrientation(s1,-1,client.simxServiceCall()));
        client.simxSleep(1000);
        float[] quat={0.0f,0.0f,0.2f,1.0f};
        b0RemoteApi.print(client.simxSetObjectQuaternion(s1,-1,quat,client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetObjectQuaternion(s1,-1,client.simxServiceCall()));
        client.simxSleep(1000);
        float[] pose={0.1f,0.1f,0.0f,0.0f,0.0f,0.0f,1.0f};
        b0RemoteApi.print(client.simxSetObjectPose(s1,-1,pose,client.simxServiceCall()));
        b0RemoteApi.print(client.simxGetObjectPose(s1,-1,client.simxServiceCall()));
        client.simxSleep(1000);
        MessageUnpacker matr=client.simxGetObjectMatrix(s1,-1,client.simxServiceCall());
        float[] m=b0RemoteApi.readFloatArray(matr,1);
        m[3]=0.0f;
        m[7]=0.0f;
        b0RemoteApi.print(client.simxSetObjectMatrix(s1,-1,m,client.simxServiceCall()));
*/        


        //*
        MessageBufferPacker data=MessagePack.newDefaultBufferPacker();
        data.packArrayHeader(4);
        data.packString("Hello World :)");
        data.packArrayHeader(3).packFloat(colPurple[0]).packFloat(colPurple[1]).packFloat(colPurple[2]);
        data.packNil();
        data.packFloat(42.123f);
        b0RemoteApi.print(client.simxCallScriptFunction("myFunction@DefaultCamera","sim.scripttype_customizationscript",data.toByteArray(),client.simxServiceCall()));
        data=MessagePack.newDefaultBufferPacker();
        data.packString("Hello World :)");
        b0RemoteApi.print(client.simxCallScriptFunction("myFunction@DefaultCamera","sim.scripttype_customizationscript",data.toByteArray(),client.simxServiceCall()));
        data=MessagePack.newDefaultBufferPacker();
        data.packFloat(59.57f);
        b0RemoteApi.print(client.simxCallScriptFunction("myFunction@DefaultCamera","sim.scripttype_customizationscript",data.toByteArray(),client.simxServiceCall()));
        data=MessagePack.newDefaultBufferPacker();
        data.packNil();
        b0RemoteApi.print(client.simxCallScriptFunction("myFunction@DefaultCamera","sim.scripttype_customizationscript",data.toByteArray(),client.simxServiceCall()));
    //*/


        
        client.delete();
        System.out.println("Program ended");
    }
}
