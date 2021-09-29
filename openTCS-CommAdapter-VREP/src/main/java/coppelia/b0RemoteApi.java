// -------------------------------------------------------
// Add your custom functions at the bottom of the file
// and the server counterpart to lua/b0RemoteApiServer.lua
// -------------------------------------------------------

package coppelia;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

import java.io.IOException;

public class b0RemoteApi
{
    class SHandleAndCb
    {
        long handle;
        boolean dropMessages;
        Consumer<MessageUnpacker> cb;
    }
    class SHandle
    {
        long handle;
    }

    static{
        System.loadLibrary("b0");
    }
    public static void print(final MessageUnpacker msg) throws IOException 
    {
        int cnt=0;
        while (msg.hasNext())
        {
            if (cnt>0)
                System.out.printf(", ");
            System.out.print(msg.unpackValue());
            cnt=cnt+1;
        }
        if (cnt>0)
            System.out.printf("\n");
    }
    
    public static boolean hasValue(final MessageUnpacker msg) throws IOException 
    {
        return msg.hasNext();
    }
    public static Value readValue(final MessageUnpacker msg) throws IOException
    {
        return readValue(msg,0);
    }
    public static Value readValue(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        while (valuesToDiscard>0)
        {
            msg.unpackValue();
            valuesToDiscard=valuesToDiscard-1;
        }
        return msg.unpackValue();
    }
    public static boolean readBool(final MessageUnpacker msg) throws IOException
    {
        return readBool(msg,0);
    }
    public static boolean readBool(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        return val.asBooleanValue().getBoolean();
    }
    public static int readInt(final MessageUnpacker msg) throws IOException
    {
        return readInt(msg,0);
    }
    public static int readInt(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isIntegerValue())
            return val.asNumberValue().toInt();
        throw new IOException("not an int");
    }
    public static float readFloat(final MessageUnpacker msg) throws IOException
    {
        return readFloat(msg,0);
    }
    public static float readFloat(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        return (float)readDouble(msg,valuesToDiscard);
    }
    public static double readDouble(final MessageUnpacker msg) throws IOException
    {
        return readDouble(msg,0);
    }
    public static double readDouble(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        return val.asNumberValue().toDouble();
    }
    public static String readString(final MessageUnpacker msg) throws IOException
    {
        return readString(msg,0);
    }
    public static String readString(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        return val.asStringValue().asString();
    }
    public static byte[] readByteArray(final MessageUnpacker msg) throws IOException
    {
        return readByteArray(msg,0);
    }
    public static byte[] readByteArray(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isRawValue())
            return val.asRawValue().asByteArray();
        throw new IOException("not a byte array");
    }
    public static int[] readIntArray(final MessageUnpacker msg) throws IOException
    {
        return readIntArray(msg,0);
    }
    public static int[] readIntArray(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isArrayValue())
        {
            ArrayValue arr=val.asArrayValue();
            int s=arr.size();
            int[] retVal=new int[s];
            for (int i=0;i<s;i=i+1)
            {
                Value v=arr.get(i);
                if (v.isNumberValue())
                    retVal[i]=v.asNumberValue().toInt();
                else
                    retVal[i]=0;
            }
            return retVal;
        }
        throw new IOException("not an array");
    }
    public static float[] readFloatArray(final MessageUnpacker msg) throws IOException
    {
        return readFloatArray(msg,0);
    }
    public static float[] readFloatArray(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isArrayValue())
        {
            ArrayValue arr=val.asArrayValue();
            int s=arr.size();
            float[] retVal=new float[s];
            for (int i=0;i<s;i=i+1)
            {
                Value v=arr.get(i);
                if (v.isNumberValue())
                    retVal[i]=v.asNumberValue().toFloat();
                else
                    retVal[i]=0.0f;
            }
            return retVal;
        }
        throw new IOException("not an array");
    }
    public static double[] readDoubleArray(final MessageUnpacker msg) throws IOException
    {
        return readDoubleArray(msg,0);
    }
    public static double[] readDoubleArray(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isArrayValue())
        {
            ArrayValue arr=val.asArrayValue();
            int s=arr.size();
            double[] retVal=new double[s];
            for (int i=0;i<s;i=i+1)
            {
                Value v=arr.get(i);
                if (v.isNumberValue())
                    retVal[i]=v.asNumberValue().toDouble();
                else
                    retVal[i]=0.0;
            }
            return retVal;
        }
        throw new IOException("not an array");
    }
    public static String[] readStringArray(final MessageUnpacker msg) throws IOException
    {
        return readStringArray(msg,0);
    }
    public static String[] readStringArray(final MessageUnpacker msg,int valuesToDiscard) throws IOException
    {
        Value val=readValue(msg,valuesToDiscard);
        if (val.isArrayValue())
        {
            ArrayValue arr=val.asArrayValue();
            int s=arr.size();
            String[] retVal=new String[s];
            for (int i=0;i<s;i=i+1)
            {
                Value v=arr.get(i);
                if (v.isStringValue())
                    retVal[i]=v.asStringValue().asString();
                else
                    retVal[i]=new String("");
            }
            return retVal;
        }
        throw new IOException("not an array");
    }
    
    public b0RemoteApi() throws IOException
    {
        _b0RemoteApi("b0RemoteApi_c++Client","b0RemoteApi",60,false,3);
    }
    public b0RemoteApi(final String nodeName) throws IOException
    {
        _b0RemoteApi(nodeName,"b0RemoteApi",60,false,3);
    }
    public b0RemoteApi(final String nodeName,final String channelName) throws IOException
    {
        _b0RemoteApi(nodeName,channelName,60,false,3);
    }
    public b0RemoteApi(final String nodeName,final String channelName,int inactivityToleranceInSec) throws IOException
    {
        _b0RemoteApi(nodeName,channelName,inactivityToleranceInSec,false,3);
    }
    public b0RemoteApi(final String nodeName,final String channelName,int inactivityToleranceInSec,boolean setupSubscribersAsynchronously) throws IOException
    {
        _b0RemoteApi(nodeName,channelName,inactivityToleranceInSec,setupSubscribersAsynchronously,3);
    }

    public b0RemoteApi(final String nodeName,final String channelName,int inactivityToleranceInSec,boolean setupSubscribersAsynchronously,int timeout) throws IOException
    {
        _b0RemoteApi(nodeName,channelName,inactivityToleranceInSec,setupSubscribersAsynchronously,timeout);
    }
    
    private void _b0RemoteApi(final String nodeName,final String channelName,int inactivityToleranceInSec,boolean setupSubscribersAsynchronously,int timeout) throws IOException
    {
        _channelName=channelName;
        _serviceCallTopic=_channelName.concat("SerX");
        _defaultPublisherTopic=_channelName.concat("SubX");
        _defaultSubscriberTopic=_channelName.concat("PubX");
        _nextDefaultSubscriberHandle=2;
        _nextDedicatedPublisherHandle=500;
        _nextDedicatedSubscriberHandle=1000;
        b0Init();
        _node=b0NodeNew(nodeName);
        Random rand = new Random(System.currentTimeMillis());
        String alp=new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        _clientId="";
        for (int i=0;i<10;i=i+1)
        {
            char c=alp.charAt(rand.nextInt(62));
            _clientId=_clientId.concat(String.valueOf(c));
        }
        _serviceClient=b0ServiceClientNewEx(_node,_serviceCallTopic,1,1);
        b0ServiceClientSetOption(_serviceClient,B0_SOCK_OPT_READTIMEOUT,timeout*1000);
        _defaultPublisher=b0PublisherNewEx(_node,_defaultPublisherTopic,1,1);
        _defaultSubscriber=b0SubscriberNewEx(_node,_defaultSubscriberTopic,1,1);
        System.out.println("");
        System.out.println("Running B0 Remote API client with channel name ["+_channelName+"]");
        System.out.println("  make sure that: 1) the B0 resolver is running");
        System.out.println("                  2) CoppeliaSim is running the B0 Remote API server with the same channel name");
        System.out.println("  Initializing...");
        System.out.println("");
        b0NodeInit(_node);
        
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(inactivityToleranceInSec);
        _handleFunction("inactivityTolerance",args,_serviceCallTopic);
        _setupSubscribersAsynchronously=setupSubscribersAsynchronously;
        _allSubscribers=new HashMap<String,SHandleAndCb>();
        _allDedicatedPublishers=new HashMap<String,SHandle>();

        System.out.println("");
        System.out.println("  Connected!");
        System.out.println("");
    }
    
    public void delete() throws IOException
    {
        System.out.println("*************************************************************************************");
        System.out.println("** Leaving... if this is unexpected, you might have to adjust the timeout argument **");
        System.out.println("*************************************************************************************");
        _pongReceived=false;
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);
        String pingTopic=simxDefaultSubscriber(this::_pingCallback);
        _handleFunction("Ping",args,pingTopic);
        
        while (!_pongReceived)
            simxSpinOnce();

        args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packString(_clientId);
        _handleFunction("DisconnectClient",args,_serviceCallTopic);

        for (String key:_allSubscribers.keySet())
        {
            SHandleAndCb val=_allSubscribers.get(key);
            if (val.handle!=_defaultSubscriber)
                b0SubscriberDelete(val.handle);
        }

        for (String key:_allDedicatedPublishers.keySet())
        {
            SHandle val=_allDedicatedPublishers.get(key);
            b0PublisherDelete(val.handle);
        }
        
//        b0NodeDelete(_node);
    }

    private void _pingCallback(final MessageUnpacker msg) 
    {
        _pongReceived=true;
    }

    
    private byte[] _concatPackers(final MessageBufferPacker header,final MessageBufferPacker data) throws IOException
    {
        MessageBufferPacker msg=MessagePack.newDefaultBufferPacker();
        msg.packArrayHeader(2);
        byte[] packedData=new byte[msg.toByteArray().length+header.toByteArray().length+data.toByteArray().length];
        System.arraycopy(msg.toByteArray(),0,packedData,0,msg.toByteArray().length);
        System.arraycopy(header.toByteArray(),0,packedData,msg.toByteArray().length,header.toByteArray().length);
        System.arraycopy(data.toByteArray(),0,packedData,msg.toByteArray().length+header.toByteArray().length,data.toByteArray().length);
        return packedData;
    }
    
    private MessageUnpacker _handleFunction(final String funcName,final MessageBufferPacker packedArgs,final String topic) throws IOException
    {
        if (topic.equals(_serviceCallTopic))
        {
            MessageBufferPacker header=MessagePack.newDefaultBufferPacker();
            header.packArrayHeader(4).packString(funcName).packString(_clientId).packString(topic).packInt(0);
            byte[] rep=b0ServiceClientCall(_serviceClient,_concatPackers(header,packedArgs));
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(rep);
            int s=unpacker.unpackArrayHeader();
            if (s>=2)
//                return MessagePack.newDefaultUnpacker(rep);
                return unpacker;
            MessageBufferPacker tmp=MessagePack.newDefaultBufferPacker();
//            tmp.packArrayHeader(2).packBoolean(unpacker.unpackBoolean()).packNil();
            tmp.packBoolean(unpacker.unpackBoolean()).packNil();
            return MessagePack.newDefaultUnpacker(tmp.toByteArray());
        }
        else if (topic.equals(_defaultPublisherTopic))
        {
            MessageBufferPacker header=MessagePack.newDefaultBufferPacker();
            header.packArrayHeader(4).packString(funcName).packString(_clientId).packString(topic).packInt(1);
            b0PublisherPublish(_defaultPublisher,_concatPackers(header,packedArgs));
        }
        else
        {
            if (_allSubscribers.containsKey(topic))
            {
                SHandleAndCb val=_allSubscribers.get(topic);
                MessageBufferPacker header=MessagePack.newDefaultBufferPacker();
                if (val.handle==_defaultSubscriber)
                    header.packArrayHeader(4).packString(funcName).packString(_clientId).packString(topic).packInt(2);
                else
                    header.packArrayHeader(4).packString(funcName).packString(_clientId).packString(topic).packInt(4);
                if (_setupSubscribersAsynchronously)
                    b0PublisherPublish(_defaultPublisher,_concatPackers(header,packedArgs));
                else
                    b0ServiceClientCall(_serviceClient,_concatPackers(header,packedArgs));
            }
            else
            {
                if (_allDedicatedPublishers.containsKey(topic))
                {
                    SHandle val=_allDedicatedPublishers.get(topic);
                    MessageBufferPacker header=MessagePack.newDefaultBufferPacker();
                    header.packArrayHeader(4).packString(funcName).packString(_clientId).packString(topic).packInt(3);
                    b0PublisherPublish(val.handle,_concatPackers(header,packedArgs));
                }
            }
        }
        return null;
    }
    
    public void simxSpin() throws IOException
    {
        while (true)
            simxSpinOnce();
    }

    public void simxSpinOnce() throws IOException
    {
        boolean defaultSubscriberAlreadyProcessed=false;
        for (String key:_allSubscribers.keySet())
        {
            byte[] packedData=null;
            SHandleAndCb val=_allSubscribers.get(key);
            if ( (val.handle!=_defaultSubscriber)||(!defaultSubscriberAlreadyProcessed) )
            {
                defaultSubscriberAlreadyProcessed=defaultSubscriberAlreadyProcessed|(val.handle==_defaultSubscriber);
                while (b0SubscriberPoll(val.handle,0)>0)
                {
                    packedData=b0SubscriberRead(val.handle);
                    if (!val.dropMessages)
                        _handleReceivedMessage(packedData);
                }
                if ( val.dropMessages&&(packedData!=null) )
                    _handleReceivedMessage(packedData);
            }
        }
    }

    private void _handleReceivedMessage(final byte[] packedData) throws IOException
    {
        if (packedData.length>0)
        {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packedData);
            int s=unpacker.unpackArrayHeader();
            if (s==2)
            {
                String topic=unpacker.unpackString();
                if (_allSubscribers.containsKey(topic))
                {
                    unpacker.unpackArrayHeader();
                    SHandleAndCb val=_allSubscribers.get(topic);
                    val.cb.accept(unpacker);
                    
                    
 /*                   
                    MessageUnpacker msg=null;
                    s=unpacker.unpackArrayHeader();
                    if (s>=2)
                    {
                        msg=MessagePack.newDefaultUnpacker(packedData);
                        msg.unpackArrayHeader();
                        msg.unpackString();
                    }
                    else
                    {
                        MessageBufferPacker tmp=MessagePack.newDefaultBufferPacker();
                        tmp.packArrayHeader(2).packBoolean(unpacker.unpackBoolean()).packNil();                        
                        msg=MessagePack.newDefaultUnpacker(tmp.toByteArray());
                    }
                    SHandleAndCb val=_allSubscribers.get(topic);
                    val.cb.accept(msg);
                    */
                }
            }
        }
    }

    public long simxGetTimeInMs()
    {
        return b0NodeHardwareTimeUsec(_node)/1000;
    }

    public void simxSleep(int durationInMs) throws InterruptedException
    {
        Thread.sleep(durationInMs);
    }
    
    public String simxDefaultPublisher()
    {
        return _defaultPublisherTopic;
    }

    public String simxCreatePublisher() throws IOException
    {
        return simxCreatePublisher(false);
    }
    
    public String simxCreatePublisher(boolean dropMessages) throws IOException
    {
        String topic=_channelName+"Sub"+_nextDedicatedPublisherHandle+_clientId;
        _nextDedicatedPublisherHandle=_nextDedicatedPublisherHandle+1;
        long pub=b0PublisherNewEx(_node,topic,0,1);
        b0PublisherInit(pub);
        SHandle dat=new SHandle();
        dat.handle=pub;
        _allDedicatedPublishers.put(topic,dat);
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2).packString(topic).packBoolean(dropMessages);
        _handleFunction("createSubscriber",args,_serviceCallTopic);
        return topic;
    }

    public String simxDefaultSubscriber(final Consumer<MessageUnpacker> cb) throws IOException
    {
        return simxDefaultSubscriber(cb,1);
    }
    public String simxDefaultSubscriber(final Consumer<MessageUnpacker> cb,int publishInterval) throws IOException
    {
        String topic=_channelName+"Pub"+_nextDefaultSubscriberHandle+_clientId;
        _nextDefaultSubscriberHandle=_nextDefaultSubscriberHandle+1;
        SHandleAndCb dat=new SHandleAndCb();
        dat.handle=_defaultSubscriber;
        dat.cb=cb;
        dat.dropMessages=false;
        _allSubscribers.put(topic,dat);
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2).packString(topic).packInt(publishInterval);
        String channel=_serviceCallTopic;
        if (_setupSubscribersAsynchronously)
            channel=_defaultPublisherTopic;
        _handleFunction("setDefaultPublisherPubInterval",args,channel);
        return topic;
    }

    public String simxCreateSubscriber(final Consumer<MessageUnpacker> cb) throws IOException
    {
        return simxCreateSubscriber(cb,1);
    }
    public String simxCreateSubscriber(final Consumer<MessageUnpacker> cb,int publishInterval) throws IOException
    {
        return simxCreateSubscriber(cb,publishInterval,false);
    }
    
    public String simxCreateSubscriber(final Consumer<MessageUnpacker> cb,int publishInterval,boolean dropMessages) throws IOException
    {
        String topic=_channelName+"Pub"+_nextDedicatedSubscriberHandle+_clientId;
        _nextDedicatedSubscriberHandle=_nextDedicatedSubscriberHandle+1;
        long sub=b0SubscriberNewEx(_node,topic,0,1);
        if (dropMessages)
            b0SubscriberSetOption(sub,B0_SOCK_OPT_CONFLATE,1);
        else
            b0SubscriberSetOption(sub,B0_SOCK_OPT_CONFLATE,0);
        b0SubscriberInit(sub);
        SHandleAndCb dat=new SHandleAndCb();
        dat.handle=sub;
        dat.cb=cb;
        dat.dropMessages=dropMessages;
        _allSubscribers.put(topic,dat);
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2).packString(topic).packInt(publishInterval);
        String channel=_serviceCallTopic;
        if (_setupSubscribersAsynchronously)
            channel=_defaultPublisherTopic;
        _handleFunction("createPublisher",args,channel);
        return topic;
    }
    
    public void simxRemoveSubscriber(final String topic) throws IOException
    {
        if (_allSubscribers.containsKey(topic))
        {
            SHandleAndCb val=_allSubscribers.get(topic);
            MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
            args.packArrayHeader(1).packString(topic);
            String channel=_serviceCallTopic;
            if (_setupSubscribersAsynchronously)
                channel=_defaultPublisherTopic;
            if (val.handle==_defaultSubscriber)
                _handleFunction("stopDefaultPublisher",args,channel);
            else
            {
                b0SubscriberDelete(val.handle);
                _handleFunction("stopPublisher",args,channel);
            }
            _allSubscribers.remove(topic);
        }
    }
    
    public void simxRemovePublisher(final String topic) throws IOException
    {
        if (_allDedicatedPublishers.containsKey(topic))
        {
            SHandle val=_allDedicatedPublishers.get(topic);
            b0PublisherDelete(val.handle);            
            MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
            args.packArrayHeader(1).packString(topic);
            _handleFunction("stopSubscriber",args,_serviceCallTopic);
            _allDedicatedPublishers.remove(topic);
        }
    }
    
    public String simxServiceCall()
    {
        return _serviceCallTopic;
    }
    

    private static final int B0_SOCK_OPT_READTIMEOUT = 3;
    private static final int B0_SOCK_OPT_CONFLATE = 6;
    
    private native int b0Init();
    
    private native long b0NodeNew(final String name);
    private native void b0NodeDelete(long node);
    private native void b0NodeInit(long node);
    private native long b0NodeTimeUsec(long node);
    private native long b0NodeHardwareTimeUsec(long node);

    private native long b0PublisherNewEx(long node,final String topicName,int managed,int notifyGraph);
    private native void b0PublisherDelete(long pub);
    private native void b0PublisherInit(long pub);
    private native void b0PublisherPublish(long pub,byte[] data);
    
    private native long b0SubscriberNewEx(long node,final String topicName,int managed,int notifyGraph);
    private native void b0SubscriberDelete(long sub);
    private native void b0SubscriberInit(long sub);
    private native int b0SubscriberPoll(long sub,long timeout);
    private native byte[] b0SubscriberRead(long sub);
    private native void b0SubscriberSetOption(long sub,long option,long value);
    
    private native long b0ServiceClientNewEx(long node,final String serviceName,int managed,int notifyGraph);
    private native void b0ServiceClientDelete(long cli);
    private native byte[] b0ServiceClientCall(long cli,byte[] data);
    private native void b0ServiceClientSetOption(long cli,long option,long value);
    
    String _serviceCallTopic;
    String _defaultPublisherTopic;
    String _defaultSubscriberTopic;
    int _nextDefaultSubscriberHandle;
    int _nextDedicatedPublisherHandle;
    int _nextDedicatedSubscriberHandle;
    boolean _pongReceived;
    boolean _setupSubscribersAsynchronously;
    String _channelName;
    long _node;
    String _clientId;
    long _serviceClient;
    long _defaultPublisher;
    long _defaultSubscriber;
    HashMap<String,SHandleAndCb> _allSubscribers;
    HashMap<String,SHandle> _allDedicatedPublishers;
    
    public void simxSynchronous(boolean enable) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packBoolean(enable);
        _handleFunction("Synchronous",args,_serviceCallTopic);
    }
    
    public void simxSynchronousTrigger() throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);
        _handleFunction("SynchronousTrigger",args,_defaultPublisherTopic);
    }
    public void simxGetSimulationStepDone(final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);
        _handleFunction("GetSimulationStepDone",args,topic);
    }
    
    public void simxGetSimulationStepStarted(final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);
        _handleFunction("GetSimulationStepStarted",args,topic);
    }

    public MessageUnpacker simxCallScriptFunction(final String funcAtObjName,int scriptType,byte[] packedArg,final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packString(funcAtObjName);
        args.packInt(scriptType);
        args.packBinaryHeader(packedArg.length);
        args.writePayload(packedArg);
        return _handleFunction("CallScriptFunction",args,topic);
    }
    
    public MessageUnpacker simxCallScriptFunction(final String funcAtObjName,final String scriptType,byte[] packedArg,final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packString(funcAtObjName);
        args.packString(scriptType);
        args.packBinaryHeader(packedArg.length);
        args.writePayload(packedArg);
        return _handleFunction("CallScriptFunction",args,topic);
    }



    public MessageUnpacker simxGetObjectHandle(
        final String objectName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(objectName);
        return _handleFunction("GetObjectHandle",args,topic);
    }
    public MessageUnpacker simxAddStatusbarMessage(
        final String msg,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(msg);
        return _handleFunction("AddStatusbarMessage",args,topic);
    }
    public MessageUnpacker simxGetObjectPosition(
        int objectHandle,
        int relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        return _handleFunction("GetObjectPosition",args,topic);
    }
    public MessageUnpacker simxGetObjectPosition(
        int objectHandle,
        final String relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        return _handleFunction("GetObjectPosition",args,topic);
    }
    public MessageUnpacker simxGetObjectOrientation(
        int objectHandle,
        int relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        return _handleFunction("GetObjectOrientation",args,topic);
    }
    public MessageUnpacker simxGetObjectOrientation(
        int objectHandle,
        final String relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        return _handleFunction("GetObjectOrientation",args,topic);
    }
    public MessageUnpacker simxGetObjectQuaternion(
        int objectHandle,
        int relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        return _handleFunction("GetObjectQuaternion",args,topic);
    }
    public MessageUnpacker simxGetObjectQuaternion(
        int objectHandle,
        final String relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        return _handleFunction("GetObjectQuaternion",args,topic);
    }
    public MessageUnpacker simxGetObjectPose(
        int objectHandle,
        int relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        return _handleFunction("GetObjectPose",args,topic);
    }
    public MessageUnpacker simxGetObjectPose(
        int objectHandle,
        final String relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        return _handleFunction("GetObjectPose",args,topic);
    }
    public MessageUnpacker simxGetObjectMatrix(
        int objectHandle,
        int relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        return _handleFunction("GetObjectMatrix",args,topic);
    }
    public MessageUnpacker simxGetObjectMatrix(
        int objectHandle,
        final String relObjHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        return _handleFunction("GetObjectMatrix",args,topic);
    }
    public MessageUnpacker simxSetObjectPosition(
        int objectHandle,
        int relObjHandle,
        final float[] position,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(position[i]);
        return _handleFunction("SetObjectPosition",args,topic);
    }
    public MessageUnpacker simxSetObjectPosition(
        int objectHandle,
        final String relObjHandle,
        final float[] position,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(position[i]);
        return _handleFunction("SetObjectPosition",args,topic);
    }
    public MessageUnpacker simxSetObjectOrientation(
        int objectHandle,
        int relObjHandle,
        final float[] euler,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(euler[i]);
        return _handleFunction("SetObjectOrientation",args,topic);
    }
    public MessageUnpacker simxSetObjectOrientation(
        int objectHandle,
        final String relObjHandle,
        final float[] euler,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(euler[i]);
        return _handleFunction("SetObjectOrientation",args,topic);
    }
    public MessageUnpacker simxSetObjectQuaternion(
        int objectHandle,
        int relObjHandle,
        final float[] quat,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        args.packArrayHeader(4);
        for (int i=0;i<4;i=i+1)
            args.packFloat(quat[i]);
        return _handleFunction("SetObjectQuaternion",args,topic);
    }
    public MessageUnpacker simxSetObjectQuaternion(
        int objectHandle,
        final String relObjHandle,
        final float[] quat,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        args.packArrayHeader(4);
        for (int i=0;i<4;i=i+1)
            args.packFloat(quat[i]);
        return _handleFunction("SetObjectQuaternion",args,topic);
    }
    public MessageUnpacker simxSetObjectPose(
        int objectHandle,
        int relObjHandle,
        final float[] pose,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        args.packArrayHeader(7);
        for (int i=0;i<7;i=i+1)
            args.packFloat(pose[i]);
        return _handleFunction("SetObjectPose",args,topic);
    }
    public MessageUnpacker simxSetObjectPose(
        int objectHandle,
        final String relObjHandle,
        final float[] pose,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        args.packArrayHeader(7);
        for (int i=0;i<7;i=i+1)
            args.packFloat(pose[i]);
        return _handleFunction("SetObjectPose",args,topic);
    }
    public MessageUnpacker simxSetObjectMatrix(
        int objectHandle,
        int relObjHandle,
        final float[] matr,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(relObjHandle);
        args.packArrayHeader(12);
        for (int i=0;i<12;i=i+1)
            args.packFloat(matr[i]);
        return _handleFunction("SetObjectMatrix",args,topic);
    }
    public MessageUnpacker simxSetObjectMatrix(
        int objectHandle,
        final String relObjHandle,
        final float[] matr,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(relObjHandle);
        args.packArrayHeader(12);
        for (int i=0;i<12;i=i+1)
            args.packFloat(matr[i]);
        return _handleFunction("SetObjectMatrix",args,topic);
    }
    public MessageUnpacker simxClearFloatSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("ClearFloatSignal",args,topic);
    }
    public MessageUnpacker simxClearIntegerSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("ClearIntegerSignal",args,topic);
    }
    public MessageUnpacker simxClearStringSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("ClearStringSignal",args,topic);
    }
    public MessageUnpacker simxSetFloatSignal(
        final String sigName,
        float sigValue,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(sigName);
        args.packFloat(sigValue);
        return _handleFunction("SetFloatSignal",args,topic);
    }
    public MessageUnpacker simxSetIntSignal(
        final String sigName,
        int sigValue,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(sigName);
        args.packInt(sigValue);
        return _handleFunction("SetIntSignal",args,topic);
    }
    public MessageUnpacker simxSetStringSignal(
        final String sigName,
        final byte[] sigValue,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(sigName);
        args.packBinaryHeader(sigValue.length);
        args.writePayload(sigValue);
        return _handleFunction("SetStringSignal",args,topic);
    }
    public MessageUnpacker simxGetFloatSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("GetFloatSignal",args,topic);
    }
    public MessageUnpacker simxGetIntSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("GetIntSignal",args,topic);
    }
    public MessageUnpacker simxGetStringSignal(
        final String sigName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(sigName);
        return _handleFunction("GetStringSignal",args,topic);
    }
    public MessageUnpacker simxAuxiliaryConsoleClose(
        int consoleHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(consoleHandle);
        return _handleFunction("AuxiliaryConsoleClose",args,topic);
    }
    public MessageUnpacker simxAuxiliaryConsolePrint(
        int consoleHandle,
        final String text,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(consoleHandle);
        args.packString(text);
        return _handleFunction("AuxiliaryConsolePrint",args,topic);
    }
    public MessageUnpacker simxAuxiliaryConsoleShow(
        int consoleHandle,
        boolean showState,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(consoleHandle);
        args.packBoolean(showState);
        return _handleFunction("AuxiliaryConsoleShow",args,topic);
    }
    public MessageUnpacker simxAuxiliaryConsoleOpen(
        final String title,
        int maxLines,
        int mode,
        final int[] position,
        final int[] size,
        final int[] textColor,
        final int[] backgroundColor,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(7);
        args.packString(title);
        args.packInt(maxLines);
        args.packInt(mode);
        args.packArrayHeader(2);
        for (int i=0;i<2;i=i+1)
            args.packInt(position[i]);
        args.packArrayHeader(2);
        for (int i=0;i<2;i=i+1)
            args.packInt(size[i]);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(textColor[i]);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(backgroundColor[i]);
        return _handleFunction("AuxiliaryConsoleOpen",args,topic);
    }
    public MessageUnpacker simxStartSimulation(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("StartSimulation",args,topic);
    }
    public MessageUnpacker simxStopSimulation(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("StopSimulation",args,topic);
    }
    public MessageUnpacker simxPauseSimulation(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("PauseSimulation",args,topic);
    }
    public MessageUnpacker simxGetVisionSensorImage(
        int objectHandle,
        boolean greyScale,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packBoolean(greyScale);
        return _handleFunction("GetVisionSensorImage",args,topic);
    }
    public MessageUnpacker simxSetVisionSensorImage(
        int objectHandle,
        boolean greyScale,
        final byte[] img,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packBoolean(greyScale);
        args.packBinaryHeader(img.length);
        args.writePayload(img);
        return _handleFunction("SetVisionSensorImage",args,topic);
    }
    public MessageUnpacker simxGetVisionSensorDepthBuffer(
        int objectHandle,
        boolean toMeters,
        boolean asByteArray,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packBoolean(toMeters);
        args.packBoolean(asByteArray);
        return _handleFunction("GetVisionSensorDepthBuffer",args,topic);
    }
    public MessageUnpacker simxAddDrawingObject_points(
        int size,
        final int[] color,
        final float[] coords,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(size);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        args.packArrayHeader(coords.length);
        for (int i=0;i<coords.length;i=i+1)
            args.packFloat(coords[i]);
        return _handleFunction("AddDrawingObject_points",args,topic);
    }
    public MessageUnpacker simxAddDrawingObject_spheres(
        float size,
        final int[] color,
        final float[] coords,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packFloat(size);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        args.packArrayHeader(coords.length);
        for (int i=0;i<coords.length;i=i+1)
            args.packFloat(coords[i]);
        return _handleFunction("AddDrawingObject_spheres",args,topic);
    }
    public MessageUnpacker simxAddDrawingObject_cubes(
        float size,
        final int[] color,
        final float[] coords,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packFloat(size);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        args.packArrayHeader(coords.length);
        for (int i=0;i<coords.length;i=i+1)
            args.packFloat(coords[i]);
        return _handleFunction("AddDrawingObject_cubes",args,topic);
    }
    public MessageUnpacker simxAddDrawingObject_segments(
        int lineSize,
        final int[] color,
        final float[] segments,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(lineSize);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        args.packArrayHeader(segments.length);
        for (int i=0;i<segments.length;i=i+1)
            args.packFloat(segments[i]);
        return _handleFunction("AddDrawingObject_segments",args,topic);
    }
    public MessageUnpacker simxAddDrawingObject_triangles(
        final int[] color,
        final float[] triangles,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        args.packArrayHeader(triangles.length);
        for (int i=0;i<triangles.length;i=i+1)
            args.packFloat(triangles[i]);
        return _handleFunction("AddDrawingObject_triangles",args,topic);
    }
    public MessageUnpacker simxRemoveDrawingObject(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("RemoveDrawingObject",args,topic);
    }
    public MessageUnpacker simxGetCollisionHandle(
        final String nameOfObject,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(nameOfObject);
        return _handleFunction("GetCollisionHandle",args,topic);
    }
    public MessageUnpacker simxGetDistanceHandle(
        final String nameOfObject,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(nameOfObject);
        return _handleFunction("GetDistanceHandle",args,topic);
    }
    public MessageUnpacker simxReadCollision(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("ReadCollision",args,topic);
    }
    public MessageUnpacker simxReadDistance(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("ReadDistance",args,topic);
    }
    public MessageUnpacker simxCheckCollision(
        int entity1,
        int entity2,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(entity1);
        args.packInt(entity2);
        return _handleFunction("CheckCollision",args,topic);
    }
    public MessageUnpacker simxCheckCollision(
        int entity1,
        final String entity2,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(entity1);
        args.packString(entity2);
        return _handleFunction("CheckCollision",args,topic);
    }
    public MessageUnpacker simxCheckDistance(
        int entity1,
        int entity2,
        float threshold,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(entity1);
        args.packInt(entity2);
        args.packFloat(threshold);
        return _handleFunction("CheckDistance",args,topic);
    }
    public MessageUnpacker simxCheckDistance(
        int entity1,
        final String entity2,
        float threshold,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(entity1);
        args.packString(entity2);
        args.packFloat(threshold);
        return _handleFunction("CheckDistance",args,topic);
    }
    public MessageUnpacker simxReadProximitySensor(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("ReadProximitySensor",args,topic);
    }
    public MessageUnpacker simxCheckProximitySensor(
        int handle,
        int entity,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(handle);
        args.packInt(entity);
        return _handleFunction("CheckProximitySensor",args,topic);
    }
    public MessageUnpacker simxCheckProximitySensor(
        int handle,
        final String entity,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(handle);
        args.packString(entity);
        return _handleFunction("CheckProximitySensor",args,topic);
    }
    public MessageUnpacker simxReadForceSensor(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("ReadForceSensor",args,topic);
    }
    public MessageUnpacker simxBreakForceSensor(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("BreakForceSensor",args,topic);
    }
    public MessageUnpacker simxReadVisionSensor(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("ReadVisionSensor",args,topic);
    }
    public MessageUnpacker simxCheckVisionSensor(
        int handle,
        int entity,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(handle);
        args.packInt(entity);
        return _handleFunction("CheckVisionSensor",args,topic);
    }
    public MessageUnpacker simxCheckVisionSensor(
        int handle,
        final String entity,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(handle);
        args.packString(entity);
        return _handleFunction("CheckVisionSensor",args,topic);
    }
    public MessageUnpacker simxCopyPasteObjects(
        final int[] objectHandles,
        int options,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packArrayHeader(objectHandles.length);
        for (int i=0;i<objectHandles.length;i=i+1)
            args.packInt(objectHandles[i]);
        args.packInt(options);
        return _handleFunction("CopyPasteObjects",args,topic);
    }
    public MessageUnpacker simxRemoveObjects(
        final int[] objectHandles,
        int options,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packArrayHeader(objectHandles.length);
        for (int i=0;i<objectHandles.length;i=i+1)
            args.packInt(objectHandles[i]);
        args.packInt(options);
        return _handleFunction("RemoveObjects",args,topic);
    }
    public MessageUnpacker simxCloseScene(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("CloseScene",args,topic);
    }
    public MessageUnpacker simxSetStringParameter(
        int paramId,
        final String paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(paramId);
        args.packString(paramVal);
        return _handleFunction("SetStringParameter",args,topic);
    }
    public MessageUnpacker simxSetStringParameter(
        final String paramId,
        final String paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(paramId);
        args.packString(paramVal);
        return _handleFunction("SetStringParameter",args,topic);
    }
    public MessageUnpacker simxSetFloatParameter(
        int paramId,
        float paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(paramId);
        args.packFloat(paramVal);
        return _handleFunction("SetFloatParameter",args,topic);
    }
    public MessageUnpacker simxSetFloatParameter(
        final String paramId,
        float paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(paramId);
        args.packFloat(paramVal);
        return _handleFunction("SetFloatParameter",args,topic);
    }
    public MessageUnpacker simxSetArrayParameter(
        int paramId,
        final float[] paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(paramId);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(paramVal[i]);
        return _handleFunction("SetArrayParameter",args,topic);
    }
    public MessageUnpacker simxSetArrayParameter(
        final String paramId,
        final float[] paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(paramId);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packFloat(paramVal[i]);
        return _handleFunction("SetArrayParameter",args,topic);
    }
    public MessageUnpacker simxSetIntParameter(
        int paramId,
        int paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(paramId);
        args.packInt(paramVal);
        return _handleFunction("SetIntParameter",args,topic);
    }
    public MessageUnpacker simxSetIntParameter(
        final String paramId,
        int paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(paramId);
        args.packInt(paramVal);
        return _handleFunction("SetIntParameter",args,topic);
    }
    public MessageUnpacker simxSetBoolParameter(
        int paramId,
        boolean paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(paramId);
        args.packBoolean(paramVal);
        return _handleFunction("SetBoolParameter",args,topic);
    }
    public MessageUnpacker simxSetBoolParameter(
        final String paramId,
        boolean paramVal,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packString(paramId);
        args.packBoolean(paramVal);
        return _handleFunction("SetBoolParameter",args,topic);
    }
    public MessageUnpacker simxGetStringParameter(
        int paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(paramId);
        return _handleFunction("GetStringParameter",args,topic);
    }
    public MessageUnpacker simxGetStringParameter(
        final String paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(paramId);
        return _handleFunction("GetStringParameter",args,topic);
    }
    public MessageUnpacker simxGetFloatParameter(
        int paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(paramId);
        return _handleFunction("GetFloatParameter",args,topic);
    }
    public MessageUnpacker simxGetFloatParameter(
        final String paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(paramId);
        return _handleFunction("GetFloatParameter",args,topic);
    }
    public MessageUnpacker simxGetArrayParameter(
        int paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(paramId);
        return _handleFunction("GetArrayParameter",args,topic);
    }
    public MessageUnpacker simxGetArrayParameter(
        final String paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(paramId);
        return _handleFunction("GetArrayParameter",args,topic);
    }
    public MessageUnpacker simxGetIntParameter(
        int paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(paramId);
        return _handleFunction("GetIntParameter",args,topic);
    }
    public MessageUnpacker simxGetIntParameter(
        final String paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(paramId);
        return _handleFunction("GetIntParameter",args,topic);
    }
    public MessageUnpacker simxGetBoolParameter(
        int paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(paramId);
        return _handleFunction("GetBoolParameter",args,topic);
    }
    public MessageUnpacker simxGetBoolParameter(
        final String paramId,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(paramId);
        return _handleFunction("GetBoolParameter",args,topic);
    }
    public MessageUnpacker simxDisplayDialog(
        final String titleText,
        final String mainText,
        int dialogType,
        final String inputText,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(4);
        args.packString(titleText);
        args.packString(mainText);
        args.packInt(dialogType);
        args.packString(inputText);
        return _handleFunction("DisplayDialog",args,topic);
    }
    public MessageUnpacker simxDisplayDialog(
        final String titleText,
        final String mainText,
        final String dialogType,
        final String inputText,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(4);
        args.packString(titleText);
        args.packString(mainText);
        args.packString(dialogType);
        args.packString(inputText);
        return _handleFunction("DisplayDialog",args,topic);
    }
    public MessageUnpacker simxGetDialogResult(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("GetDialogResult",args,topic);
    }
    public MessageUnpacker simxGetDialogInput(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("GetDialogInput",args,topic);
    }
    public MessageUnpacker simxEndDialog(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("EndDialog",args,topic);
    }
    public MessageUnpacker simxExecuteScriptString(
        final String code,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(code);
        return _handleFunction("ExecuteScriptString",args,topic);
    }
    public MessageUnpacker simxGetCollectionHandle(
        final String collectionName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(collectionName);
        return _handleFunction("GetCollectionHandle",args,topic);
    }
    public MessageUnpacker simxGetJointForce(
        int jointHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(jointHandle);
        return _handleFunction("GetJointForce",args,topic);
    }
    public MessageUnpacker simxGetJointMaxForce(
        int jointHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(jointHandle);
        return _handleFunction("GetJointMaxForce",args,topic);
    }
    public MessageUnpacker simxSetJointForce(
        int jointHandle,
        float forceOrTorque,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(jointHandle);
        args.packFloat(forceOrTorque);
        return _handleFunction("SetJointForce",args,topic);
    }
    public MessageUnpacker simxSetJointMaxForce(
        int jointHandle,
        float forceOrTorque,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(jointHandle);
        args.packFloat(forceOrTorque);
        return _handleFunction("SetJointMaxForce",args,topic);
    }
    public MessageUnpacker simxGetJointPosition(
        int jointHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(jointHandle);
        return _handleFunction("GetJointPosition",args,topic);
    }
    public MessageUnpacker simxSetJointPosition(
        int jointHandle,
        float position,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(jointHandle);
        args.packFloat(position);
        return _handleFunction("SetJointPosition",args,topic);
    }
    public MessageUnpacker simxGetJointTargetPosition(
        int jointHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(jointHandle);
        return _handleFunction("GetJointTargetPosition",args,topic);
    }
    public MessageUnpacker simxSetJointTargetPosition(
        int jointHandle,
        float targetPos,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(jointHandle);
        args.packFloat(targetPos);
        return _handleFunction("SetJointTargetPosition",args,topic);
    }
    public MessageUnpacker simxGetJointTargetVelocity(
        int jointHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(jointHandle);
        return _handleFunction("GetJointTargetVelocity",args,topic);
    }
    public MessageUnpacker simxSetJointTargetVelocity(
        int jointHandle,
        float targetPos,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(jointHandle);
        args.packFloat(targetPos);
        return _handleFunction("SetJointTargetVelocity",args,topic);
    }
    public MessageUnpacker simxGetObjectChild(
        int objectHandle,
        int index,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(index);
        return _handleFunction("GetObjectChild",args,topic);
    }
    public MessageUnpacker simxGetObjectParent(
        int objectHandle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(objectHandle);
        return _handleFunction("GetObjectParent",args,topic);
    }
    public MessageUnpacker simxSetObjectParent(
        int objectHandle,
        int parentHandle,
        boolean assembly,
        boolean keepInPlace,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(4);
        args.packInt(objectHandle);
        args.packInt(parentHandle);
        args.packBoolean(assembly);
        args.packBoolean(keepInPlace);
        return _handleFunction("SetObjectParent",args,topic);
    }
    public MessageUnpacker simxGetObjectsInTree(
        int treeBaseHandle,
        final String objectType,
        int options,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(treeBaseHandle);
        args.packString(objectType);
        args.packInt(options);
        return _handleFunction("GetObjectsInTree",args,topic);
    }
    public MessageUnpacker simxGetObjectsInTree(
        final String treeBaseHandle,
        final String objectType,
        int options,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packString(treeBaseHandle);
        args.packString(objectType);
        args.packInt(options);
        return _handleFunction("GetObjectsInTree",args,topic);
    }
    public MessageUnpacker simxGetObjectName(
        int objectHandle,
        boolean altName,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packBoolean(altName);
        return _handleFunction("GetObjectName",args,topic);
    }
    public MessageUnpacker simxGetObjectFloatParameter(
        int objectHandle,
        int parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        return _handleFunction("GetObjectFloatParameter",args,topic);
    }
    public MessageUnpacker simxGetObjectFloatParameter(
        int objectHandle,
        final String parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(parameterID);
        return _handleFunction("GetObjectFloatParameter",args,topic);
    }
    public MessageUnpacker simxGetObjectIntParameter(
        int objectHandle,
        int parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        return _handleFunction("GetObjectIntParameter",args,topic);
    }
    public MessageUnpacker simxGetObjectIntParameter(
        int objectHandle,
        final String parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(parameterID);
        return _handleFunction("GetObjectIntParameter",args,topic);
    }
    public MessageUnpacker simxGetObjectStringParameter(
        int objectHandle,
        int parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        return _handleFunction("GetObjectStringParameter",args,topic);
    }
    public MessageUnpacker simxGetObjectStringParameter(
        int objectHandle,
        final String parameterID,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packInt(objectHandle);
        args.packString(parameterID);
        return _handleFunction("GetObjectStringParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectFloatParameter(
        int objectHandle,
        int parameterID,
        float parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        args.packFloat(parameter);
        return _handleFunction("SetObjectFloatParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectFloatParameter(
        int objectHandle,
        final String parameterID,
        float parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(parameterID);
        args.packFloat(parameter);
        return _handleFunction("SetObjectFloatParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectIntParameter(
        int objectHandle,
        int parameterID,
        int parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        args.packInt(parameter);
        return _handleFunction("SetObjectIntParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectIntParameter(
        int objectHandle,
        final String parameterID,
        int parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(parameterID);
        args.packInt(parameter);
        return _handleFunction("SetObjectIntParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectStringParameter(
        int objectHandle,
        int parameterID,
        final String parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packInt(parameterID);
        args.packString(parameter);
        return _handleFunction("SetObjectStringParameter",args,topic);
    }
    public MessageUnpacker simxSetObjectStringParameter(
        int objectHandle,
        final String parameterID,
        final String parameter,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(3);
        args.packInt(objectHandle);
        args.packString(parameterID);
        args.packString(parameter);
        return _handleFunction("SetObjectStringParameter",args,topic);
    }
    public MessageUnpacker simxGetSimulationTime(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("GetSimulationTime",args,topic);
    }
    public MessageUnpacker simxGetSimulationTimeStep(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("GetSimulationTimeStep",args,topic);
    }
    public MessageUnpacker simxGetServerTimeInMs(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("GetServerTimeInMs",args,topic);
    }
    public MessageUnpacker simxGetSimulationState(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("GetSimulationState",args,topic);
    }
    public MessageUnpacker simxEvaluateToInt(
        final String str,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(str);
        return _handleFunction("EvaluateToInt",args,topic);
    }
    public MessageUnpacker simxEvaluateToStr(
        final String str,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(str);
        return _handleFunction("EvaluateToStr",args,topic);
    }
    public MessageUnpacker simxGetObjects(
        int objectType,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(objectType);
        return _handleFunction("GetObjects",args,topic);
    }
    public MessageUnpacker simxGetObjects(
        final String objectType,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(objectType);
        return _handleFunction("GetObjects",args,topic);
    }
    public MessageUnpacker simxCreateDummy(
        float size,
        final int[] color,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(2);
        args.packFloat(size);
        args.packArrayHeader(3);
        for (int i=0;i<3;i=i+1)
            args.packInt(color[i]);
        return _handleFunction("CreateDummy",args,topic);
    }
    public MessageUnpacker simxGetObjectSelection(
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1).packInt(0);

        return _handleFunction("GetObjectSelection",args,topic);
    }
    public MessageUnpacker simxSetObjectSelection(
        final int[] selection,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packArrayHeader(selection.length);
        for (int i=0;i<selection.length;i=i+1)
            args.packInt(selection[i]);
        return _handleFunction("SetObjectSelection",args,topic);
    }
    public MessageUnpacker simxGetObjectVelocity(
        int handle,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packInt(handle);
        return _handleFunction("GetObjectVelocity",args,topic);
    }
    public MessageUnpacker simxLoadModelFromFile(
        final String filename,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(filename);
        return _handleFunction("LoadModelFromFile",args,topic);
    }
    public MessageUnpacker simxLoadModelFromBuffer(
        final byte[] buffer,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packBinaryHeader(buffer.length);
        args.writePayload(buffer);
        return _handleFunction("LoadModelFromBuffer",args,topic);
    }
    public MessageUnpacker simxLoadScene(
        final String filename,
        final String topic) throws IOException
    {
        MessageBufferPacker args=MessagePack.newDefaultBufferPacker();
        args.packArrayHeader(1);
        args.packString(filename);
        return _handleFunction("LoadScene",args,topic);
    }

    // -----------------------------------------------------------
    // Add your custom functions here, or even better,
    // add them to b0RemoteApiBindings/generate/simxFunctions.xml,
    // and generate this file again.
    // Then add the server part of your custom functions at the
    // beginning of file lua/b0RemoteApiServer.lua
    // -----------------------------------------------------------
}
