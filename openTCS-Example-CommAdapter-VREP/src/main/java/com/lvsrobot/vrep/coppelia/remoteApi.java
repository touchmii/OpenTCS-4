package com.lvsrobot.vrep.coppelia;

public class remoteApi
{
    static{
        System.loadLibrary("remoteApiJava");
    }

    /**
     * 设置连接
     * @param connectionAddress 地址 如"127.0.0.1"
     * @param connectionPort 端口
     * @param waitUntilConnected 是否等待直到连接成功
     * @param doNotReconnectOnceDisconnected 是否断连不自动连接
     * @param timeOutInMs 连接超时毫秒
     * @param commThreadCycleInMs 线程循环时间毫秒
     * @return clientID 返回clientID
     */
    public native int simxStart(final String connectionAddress, int connectionPort, boolean waitUntilConnected, boolean doNotReconnectOnceDisconnected, int timeOutInMs, int commThreadCycleInMs);

    /**
     * 终止连接
     * @param clientID 设置-1终止所有远程连接
     */
    public native void simxFinish(int clientID);
    public native int simxGetConnectionId(int clientID);

    /**
     * 获取对象句柄
     * @param clientID 连接ID
     * @param objectName 对象名称
     * @param handle 句柄
     * @param operationMode 操作模式
     * @return 状态 0表示成功 -1失败
     */
    public native int simxGetObjectHandle(int clientID,final String objectName, IntW handle, int operationMode);

    /**
     * 获取关节坐标
     * @param clientID
     * @param jointHandle 关节句柄
     * @param position 位置
     * @param operationMode 模式
     * @return
     */
    public native int simxGetJointPosition(int clientID,int jointHandle, FloatW position, int operationMode);
    public native int simxSetJointPosition(int clientID,int jointHandle, float position, int operationMode);

    /**
     * 获取对象坐标
     * @param clientID
     * @param objectHandle 对象句柄，如果类型为IntW 可以通过getValue()获取int值
     * @param relativeToObjectHandle 相对于某个对象的句柄，设置-1表示获取绝对坐标
     * @param position 坐标
     * @param operationMode 模式，需要设置阻塞模式，即sim.simx_opmode_blocking否则获取
     *                      到的坐标为0
     * @return 0 成功，-1失败
     */
    public native int simxGetObjectPosition(int clientID,int objectHandle, int relativeToObjectHandle, FloatWA position, int operationMode);

    /**
     * 设置对象坐标
     * @param clientID
     * @param objectHandle
     * @param relativeToObjectHandle
     * @param position
     * @param operationMode
     * @return 0 成功， -1失败
     */
    public native int simxSetObjectPosition(int clientID,int objectHandle, int relativeToObjectHandle, final FloatWA position, int operationMode);

    /**
     * 获取视觉传感器图像
     * @param clientID
     * @param sensorHandle 传感器句柄
     * @param resolution 分辨率
     * @param image 图像
     * @param options 参数
     * @param operationMode 模式
     * @return
     */
    public native int simxGetVisionSensorImage(int clientID,int sensorHandle, IntWA resolution, CharWA image, int options, int operationMode);
    public native int simxSetVisionSensorImage(int clientID,int sensorHandle, CharWA image, int bufferSize, int options, int operationMode);
    public native int simxGetJointMatrix(int clientID,int jointHandle, FloatWA matrix, int operationMode);
    public native int simxSetSphericalJointMatrix(int clientID,int jointHandle,FloatWA matrix,int operationMode);

    /**
     * 设置关节目标速度
     * @param clientID
     * @param jointHandle 关节句柄
     * @param targetVelocity 目标速度
     * @param operationMode 操作模式
     * @return
     */
    public native int simxSetJointTargetVelocity(int clientID,int jointHandle,float targetVelocity,int operationMode);
    public native int simxSetJointTargetPosition(int clientID,int jointHandle,float targetPosition,int operationMode);
    public native int simxJointGetForce(int clientID,int jointHandle,FloatW force,int operationMode);

    /**
     * 获取关节力矩
     * @param clientID
     * @param jointHandle
     * @param force
     * @param operationMode
     * @return
     */
    public native int simxGetJointForce(int clientID,int jointHandle,FloatW force,int operationMode);

    /**
     * 设置关节力矩
     * @param clientID
     * @param jointHandle
     * @param force
     * @param operationMode
     * @return
     */
    public native int simxSetJointForce(int clientID,int jointHandle,float force,int operationMode);

    /**
     * 设置关节最大力矩
     * @param clientID
     * @param jointHandle
     * @param force
     * @param operationMode
     * @return
     */
    public native int simxSetJointMaxForce(int clientID,int jointHandle,float force,int operationMode);

    /**
     * 获取关节最大力矩
     * @param clientID
     * @param jointHandle
     * @param force
     * @param operationMode
     * @return
     */
    public native int simxGetJointMaxForce(int clientID,int jointHandle,FloatW force,int operationMode);
    public native int simxReadForceSensor(int clientID,int forceSensorHandle, IntW state,FloatWA forceVector,FloatWA torqueVector,int operationMode);
    public native int simxBreakForceSensor(int clientID,int forceSensorHandle,int operationMode);

    /**
     * 获取视觉传感器光强
     * @param clientID
     * @param sensorHandle
     * @param detectionState
     * @param auxValues
     * @param operationMode
     * @return
     */
    public native int simxReadVisionSensor(int clientID,int sensorHandle,BoolW detectionState, FloatWAA auxValues, int operationMode);
    public native int simxGetObjectFloatParameter(int clientID,int objectHandle,int parameterID,FloatW parameterValue,int operationMode);
    public native int simxSetObjectFloatParameter(int clientID,int objectHandle,int parameterID,float parameterValue,int operationMode);
    public native int simxGetObjectIntParameter(int clientID,int objectHandle,int parameterID,IntW parameterValue,int operationMode);
    public native int simxSetObjectIntParameter(int clientID,int objectHandle,int parameterID,int parameterValue,int operationMode);

    /**
     * 获取模型属性
     * @param clientID
     * @param objectHandle
     * @param prop
     * @param operationMode
     * @return
     */
    public native int simxGetModelProperty(int clientID,int objectHandle,IntW prop,int operationMode);

    /**
     * 设置模型属性
     * @param clientID
     * @param objectHandle
     * @param prop
     * @param operationMode
     * @return
     */
    public native int simxSetModelProperty(int clientID,int objectHandle,int prop,int operationMode);
    public native int simxGetVisionSensorDepthBuffer(int clientID,int sensorHandle,IntWA resolution,FloatWA buffer,int operationMode);

    /**
     * 获取对象子节点
     * @param clientID
     * @param parentObjectHandle
     * @param childIndex
     * @param childObjectHandle
     * @param operationMode
     * @return
     */
    public native int simxGetObjectChild(int clientID,int parentObjectHandle,int childIndex,IntW childObjectHandle,int operationMode);

    /**
     * 获取对象夫节点
     * @param clientID
     * @param childObjectHandle
     * @param parentObjectHandle
     * @param operationMode
     * @return
     */
    public native int simxGetObjectParent(int clientID,int childObjectHandle,IntW parentObjectHandle,int operationMode);

    /**
     * 启动仿真
     * @param clientID
     * @param operationMode
     * @return
     */
    public native int simxStartSimulation(int clientID,int operationMode);

    /**
     * 暂停仿真
     * @param clientID
     * @param operationMode
     * @return
     */
    public native int simxPauseSimulation(int clientID,int operationMode);

    /**
     * 终止仿真
     * @param clientID
     * @param operationMode
     * @return
     */
    public native int simxStopSimulation(int clientID,int operationMode);

    /**
     * 获取滑块位置
     * @param clientID
     * @param uiHandle
     * @param uiButtonID
     * @param position
     * @param operationMode
     * @return
     */
    public native int simxGetUISlider(int clientID,int uiHandle,int uiButtonID,IntW position,int operationMode);

    /**
     * 设置滑块位置
     * @param clientID
     * @param uiHandle
     * @param uiButtonID
     * @param position
     * @param operationMode
     * @return
     */
    public native int simxSetUISlider(int clientID,int uiHandle,int uiButtonID,int position,int operationMode);

    /**
     * 获取按钮事件
     * @param clientID
     * @param uiHandle
     * @param uiEventButtonID
     * @param auxValues
     * @param operationMode
     * @return
     */
    public native int simxGetUIEventButton(int clientID,int uiHandle,IntW uiEventButtonID,IntWA auxValues,int operationMode);

    /**
     * 获取按钮属性
     * @param clientID
     * @param uiHandle
     * @param uiButtonID
     * @param prop
     * @param operationMode
     * @return
     */
    public native int simxGetUIButtonProperty(int clientID,int uiHandle,int uiButtonID,IntW prop,int operationMode);

    /**
     * 设置按钮属性
     * @param clientID
     * @param uiHandle
     * @param uiButtonID
     * @param prop
     * @param operationMode
     * @return
     */
    public native int simxSetUIButtonProperty(int clientID,int uiHandle,int uiButtonID,int prop,int operationMode);

    /**
     * 关闭辅助控制台
     * @param clientID
     * @param consoleHandle
     * @param operationMode
     * @return
     */
    public native int simxAuxiliaryConsoleClose(int clientID,int consoleHandle,int operationMode);

    /**
     * 显示辅助控制台
     * @param clientID
     * @param consoleHandle
     * @param showState
     * @param operationMode
     * @return
     */
    public native int simxAuxiliaryConsoleShow(int clientID,int consoleHandle,boolean showState,int operationMode);

    /**
     * 获取对象角度，欧拉角
     * @param clientID
     * @param objectHandle 对象句柄
     * @param relativeToObjectHandle 相对对象句柄，-1为绝对角度
     * @param eulerAngles 欧拉角需new FloatWA(3)一个对象接收xyz三个轴的角度
     * @param operationMode 模式，需要blocking
     * @return
     */
    public native int simxGetObjectOrientation(int clientID,int objectHandle,int relativeToObjectHandle,FloatWA eulerAngles,int operationMode);

    /**
     * 获取对象角度，四元数
     * @param clientID
     * @param objectHandle
     * @param relativeToObjectHandle
     * @param quaternion 四元数， new FloatWA(4)
     * @param operationMode
     * @return
     */
    public native int simxGetObjectQuaternion(int clientID,int objectHandle,int relativeToObjectHandle,FloatWA quaternion,int operationMode);

    /**
     * 设置对象夫节点
     * @param clientID
     * @param objectHandle
     * @param parentObject
     * @param keepInPlace
     * @param operationMode
     * @return
     */
    public native int simxSetObjectParent(int clientID,int objectHandle,int parentObject,boolean keepInPlace,int operationMode);

    /**
     * 设置数组参数
     * @param clientID
     * @param paramIdentifier
     * @param paramValues
     * @param operationMode
     * @return
     */
    public native int simxGetArrayParameter(int clientID,int paramIdentifier,FloatWA paramValues,int operationMode);
    public native int simxSetArrayParameter(int clientID,int paramIdentifier,FloatWA paramValues,int operationMode);
    public native int simxGetIntegerParameter(int clientID,int paramIdentifier,IntW paramValue,int operationMode);
    public native int simxSetIntegerParameter(int clientID,int paramIdentifier,int paramValue,int operationMode);
    public native int simxGetBooleanParameter(int clientID,int paramIdentifier,BoolW paramValue,int operationMode);
    public native int simxGetDialogResult(int clientID,int dialogHandle,IntW result,int operationMode);
    public native int simxSetFloatingParameter(int clientID,int paramIdentifier,float paramValue,int operationMode);

    /**
     * 移除对象
     * @param clientID
     * @param objectHandle
     * @param operationMode
     * @return
     */
    public native int simxRemoveObject(int clientID,int objectHandle,int operationMode);

    /**
     * 移除模型
     * @param clientID
     * @param objectHandle
     * @param operationMode
     * @return
     */
    public native int simxRemoveModel(int clientID,int objectHandle,int operationMode);
    public native int simxRemoveUI(int clientID,int uiHandle,int operationMode);

    /**
     * 关闭场景
     * @param clientID
     * @param operationMode
     * @return
     */
    public native int simxCloseScene(int clientID,int operationMode);

    /**
     * 结束对话框
     * @param clientID
     * @param dialogHandle
     * @param operationMode
     * @return
     */
    public native int simxEndDialog(int clientID,int dialogHandle,int operationMode);

    /**
     * 读取距离
     * @param clientID
     * @param distanceObjectHandle
     * @param minimumDistance
     * @param operationMode
     * @return
     */
    public native int simxReadDistance(int clientID,int distanceObjectHandle,FloatW minimumDistance,int operationMode);
    public native int simxGetFloatingParameter(int clientID,int paramIdentifier,FloatW paramValue,int operationMode);

    /**
     * 设置对象角度，欧拉角
     * @param clientID
     * @param objectHandle
     * @param relativeToObjectHandle
     * @param eulerAngles
     * @param operationMode
     * @return
     */
    public native int simxSetObjectOrientation(int clientID,int objectHandle,int relativeToObjectHandle,FloatWA eulerAngles,int operationMode);

    /**
     * 设置对象角度，四元数
     * @param clientID
     * @param objectHandle
     * @param relativeToObjectHandle
     * @param quaternion
     * @param operationMode
     * @return
     */
    public native int simxSetObjectQuaternion(int clientID,int objectHandle,int relativeToObjectHandle,FloatWA quaternion,int operationMode);

    /**
     * 获取接近传感器状态
     * @param clientID
     * @param sensorHandle 传感器句柄
     * @param detectionState 检测状态
     * @param detectedPoint 检测到的点
     * @param detectedObjectHandle 检测到的物体句柄
     * @param detectedSurfaceNormalVector 检测到的曲目向量#TODO
     * @param operationMode
     * @return
     */
    public native int simxReadProximitySensor(int clientID,int sensorHandle, BoolW detectionState, FloatWA detectedPoint, IntW detectedObjectHandle, FloatWA detectedSurfaceNormalVector,int operationMode);

    /**
     * 载入模型
     * @param clientID
     * @param modelPathAndName 模型路径和名字
     * @param options
     * @param baseHandle
     * @param operationMode
     * @return
     */
    public native int simxLoadModel(int clientID,final String modelPathAndName, int options, IntW baseHandle, int operationMode);

    /**
     * 载入图形操作界面
     * @param clientID
     * @param uiPathAndName
     * @param options
     * @param uiHandles
     * @param operationMode
     * @return
     */
    public native int simxLoadUI(int clientID,final String uiPathAndName, int options, IntWA uiHandles, int operationMode);

    /**
     * 载入场景
     * @param clientID
     * @param scenePathAndName
     * @param options
     * @param operationMode
     * @return
     */
    public native int simxLoadScene(int clientID,final String scenePathAndName, int options, int operationMode);

    /**
     * 获取UI句柄
     * @param clientID
     * @param uiName
     * @param handle
     * @param operationMode
     * @return
     */
    public native int simxGetUIHandle(int clientID,final String uiName,IntW handle,int operationMode);

    /**
     * 添加状态条信息#TODO
     * @param clientID
     * @param message
     * @param operationMode
     * @return
     */
    public native int simxAddStatusbarMessage(int clientID,final String message,int operationMode);

    /**
     * 打开辅助控制台
     * @param clientID
     * @param title 标题
     * @param maxLines 最大行数
     * @param mode 模式
     * @param position 位置
     * @param size 尺寸
     * @param textColor 文字颜色
     * @param backgroundColor 背景颜色
     * @param consoleHandle 控制台句柄
     * @param operationMode 模式
     * @return
     */
    public native int simxAuxiliaryConsoleOpen(int clientID,final String title,int maxLines,int mode,IntWA position,IntWA size,FloatWA textColor,FloatWA backgroundColor,IntW consoleHandle,int operationMode);

    /**
     * 辅助控制台打印信息
     * @param clientID
     * @param consoleHandle 控制台句柄
     * @param txt 要打印的信息
     * @param operationMode
     * @return
     */
    public native int simxAuxiliaryConsolePrint(int clientID,int consoleHandle, final String txt,int operationMode);

    /**
     * 设置按钮标签
     * @param clientID
     * @param uiHandle 操作界面句柄
     * @param uiButtonID 按钮ID
     * @param upStateLabel 未按下按钮标签
     * @param downStateLabel 按下按钮标签
     * @param operationMode
     * @return
     */
    public native int simxSetUIButtonLabel(int clientID,int uiHandle,int uiButtonID,final String upStateLabel,final String downStateLabel,int operationMode);

    /**
     * 获取最近错误
     * @param clientID
     * @param errorStrings 错误信息
     * @param operationMode
     * @return
     */
    public native int simxGetLastErrors(int clientID,StringWA errorStrings,int operationMode);
    public native int simxSetBooleanParameter(int clientID,int paramIdentifier,boolean paramValue,int operationMode);
    public native int simxGetStringParameter(int clientID,int paramIdentifier,StringW paramValue,int operationMode);

    /**
     * 获取碰撞句柄#TODO
     * @param clientID
     * @param collisionObjectName
     * @param handle
     * @param operationMode
     * @return
     */
    public native int simxGetCollisionHandle(int clientID,final String collisionObjectName,IntW handle,int operationMode);
    public native int simxGetDistanceHandle(int clientID,final String distanceObjectName,IntW handle,int operationMode);

    /**
     * 获取集合句柄
     * @param clientID
     * @param collectionName 集合名称
     * @param handle
     * @param operationMode
     * @return
     */
    public native int simxGetCollectionHandle(int clientID,final String collectionName,IntW handle,int operationMode);
    public native int simxReadCollision(int clientID,int collisionObjectHandle,BoolW collisionState,int operationMode);

    /**
     * 获取指定类型的所有对象
     * @param clientID
     * @param objectType 对象类型#TODO
     * @param objectHandles 对象句柄
     * @param operationMode
     * @return
     */
    public native int simxGetObjects(int clientID,int objectType,IntWA objectHandles,int operationMode);

    /**
     * 显示对话框
     * @param clientID
     * @param titleText 标题
     * @param mainText 主要信息
     * @param dialogType 对话框类型#TODO
     * @param initialText 初始化文本
     * @param titleColors 标题颜色
     * @param dialogColors 对话框颜色
     * @param dialogHandle 对话框句柄
     * @param uiHandle ui句柄
     * @param operationMode 模式
     * @return
     */
    public native int simxDisplayDialog(int clientID,final String titleText,final String mainText,int dialogType,final String initialText,FloatWA titleColors,FloatWA dialogColors,IntW dialogHandle,IntW uiHandle,int operationMode);

    /**
     * 获取对话框输入文本
     * @param clientID
     * @param dialogHandle 对话框句柄
     * @param inputText 输入文本
     * @param operationMode 模式
     * @return
     */
    public native int simxGetDialogInput(int clientID,int dialogHandle,StringW inputText,int operationMode);

    /**
     * 批量拷贝粘贴对象
     * @param clientID
     * @param objectHandles 被拷贝对象句柄
     * @param newObjectHandles 新对象句柄
     * @param operationMode
     * @return
     */
    public native int simxCopyPasteObjects(int clientID,IntWA objectHandles, IntWA newObjectHandles, int operationMode);

    /**
     * 获取对象选中状态#TODO
     * @param clientID
     * @param objectHandles
     * @param operationMode
     * @return
     */
    public native int simxGetObjectSelection(int clientID,IntWA objectHandles, int operationMode);
    public native int simxSetObjectSelection(int clientID,IntWA objectHandles, int operationMode);
    public native int simxClearFloatSignal(int clientID,final String signalName, int operationMode);
    public native int simxClearStringSignal(int clientID,final String signalName,int operationMode);
    public native int simxClearIntegerSignal(int clientID,final String signalName,int operationMode);
    public native int simxGetFloatSignal(int clientID,final String signalName,FloatW signalValue,int operationMode);
    public native int simxGetIntegerSignal(int clientID,final String signalName,IntW signalValue,int operationMode);
    public native int simxGetStringSignal(int clientID,final String signalName,CharWA signalValue, int operationMode);
    public native int simxGetAndClearStringSignal(int clientID,final String signalName, CharWA signalValue, int operationMode);
    public native int simxReadStringStream(int clientID,final String signalName, CharWA signalValue, int operationMode);

    public native int simxSetFloatSignal(int clientID,final String signalName,float signalValue,int operationMode);
    public native int simxSetIntegerSignal(int clientID,final String signalName,int signalValue,int operationMode);
    public native int simxSetStringSignal(int clientID,final String signalName, final CharWA signalValue, int operationMode);
    public native int simxAppendStringSignal(int clientID,final String signalName, final String signalValue, int operationMode);
    public native int simxWriteStringStream(int clientID,final String signalName, final CharWA signalValue, int operationMode);

    /**
     * 获取跟服务器ping延时
     * @param clientID
     * @param pingTime
     * @return
     */
    public native int simxGetPingTime(int clientID,IntW pingTime);

    /**
     * 获取最近一条指令执行时间
     * @param clientID
     * @return
     */
    public native int simxGetLastCmdTime(int clientID);

    /**
     * 开启/关闭同步锁
     * @param clientID
     * @return
     */
    public native int simxSynchronousTrigger(int clientID);

    /**
     * 获取同步锁开启状态
     * @param clientID
     * @param enable
     * @return
     */
    public native int simxSynchronous(int clientID,boolean enable);

    /**
     * 暂停通信
     * @param clientID
     * @param enable
     * @return
     */
    public native int simxPauseCommunication(int clientID,boolean enable);

    /**
     * 获取输入信息详情#TODO
     * @param clientID
     * @param infoType
     * @param info
     * @return
     */
    public native int simxGetInMessageInfo(int clientID,int infoType, IntW info);
    public native int simxGetOutMessageInfo(int clientID,int infoType, IntW info);

    /**
     * 传输文件
     * @param clientID
     * @param filePathAndName 文件路径和名字
     * @param fileName_serverSide 传输到服务器的文件名字
     * @param timeOut 超时时间ms
     * @param operationMode
     * @return
     */
    public native int simxTransferFile(int clientID,final String filePathAndName, final String fileName_serverSide, int timeOut, int operationMode);

    /**
     * 删除文件
     * @param clientID
     * @param fileName_serverSide 服务器上的文件名
     * @param operationMode
     * @return
     */
    public native int simxEraseFile(int clientID,final String fileName_serverSide, int operationMode);

    public native int simxCreateDummy(int clientID,float size,CharWA color,IntW dummyHandle,int operationMode);

    /**
     * 查询#TODO
     * @param clientID
     * @param signalName
     * @param signalValue
     * @param retSignalName
     * @param retSignalValue
     * @param timeOutInMs
     * @return
     */
    public native int simxQuery(int clientID,final String signalName, final CharWA signalValue,final String retSignalName, CharWA retSignalValue, int timeOutInMs);

    /**
     * 获取对象分组信息#TODO
     * @param clientID
     * @param objectType
     * @param dataType
     * @param handles
     * @param intData
     * @param floatData
     * @param stringData
     * @param operationMode
     * @return
     */
    public native int simxGetObjectGroupData(int clientID,int objectType,int dataType,IntWA handles,IntWA intData,FloatWA floatData,StringWA stringData,int operationMode);

    /**
     * 获取对象速度
     * @param clientID
     * @param objectHandle 对象句柄
     * @param linearVelocity 线速度
     * @param angularVelocity 角速度
     * @param operationMode 模式
     * @return
     */
    public native int simxGetObjectVelocity(int clientID,int objectHandle, FloatWA linearVelocity, FloatWA angularVelocity, int operationMode);
    public native int simxCallScriptFunction(int clientID,final String scriptDescription,int options,final String functionName,final IntWA inInts,final FloatWA inFloats,final StringWA inStrings,final CharWA inBuffer,IntWA outInts,FloatWA outFloats,StringWA outStrings,CharWA outBuffer,int operationMode);


    /**
     * 标头（前缀）长度
     */
    public static final int SIMX_HEADER_SIZE = 18;
    /**
     * 校验位
     */
    public static final int simx_headeroffset_crc = 0;              /* 1 simxUShort. Generated by the client or server. The CRC for the message */
    public static final int simx_headeroffset_version = 2;          /* 1 byte. Generated by the client or server. The version of the remote API software */
    public static final int simx_headeroffset_message_id = 3;       /* 1 simxInt. Generated by the client (and used in a reply by the server) */
    public static final int simx_headeroffset_client_time = 7;      /* 1 simxInt. Client time stamp generated by the client (and sent back by the server) */
    public static final int simx_headeroffset_server_time = 11; /* 1 simxInt. Generated by the server when a reply is generated. The server timestamp */
    public static final int simx_headeroffset_scene_id = 15;        /* 1 simxUShort. Generated by the server. A unique ID identifying the scene currently displayed */
    public static final int simx_headeroffset_server_state = 17;    /* 1 byte. Generated by the server. Bit coded: 0 set --> simulation not stopped, 1 set --> simulation paused, 2 set --> real-time switch on, 3-5: edit mode type (0=no edit mode, 1=triangle, 2=vertex, 3=edge, 4=path, 5=UI) */

    /* Remote API command header: */
    public static final int SIMX_SUBHEADER_SIZE = 26;
    public static final int simx_cmdheaderoffset_mem_size = 0;          /* 1 simxInt. Generated by the client or server. The buffer size of the command. */
    public static final int simx_cmdheaderoffset_full_mem_size = 4; /* 1 simxInt. Generated by the client or server. The full buffer size of the command (applies to split chunks). */
    public static final int simx_cmdheaderoffset_pdata_offset0 = 8; /* 1 simxUShort. Generated by the client or server. The amount of data that is part of the command identification. */
    public static final int simx_cmdheaderoffset_pdata_offset1 = 10;    /* 1 simxInt. Generated by the client or server. The amount of shift of the pure data buffer (applies to split chunks). */
    public static final int simx_cmdheaderoffset_cmd = 14;              /* 1 simxInt. Generated by the client (and used in a reply by the server). The command, combined with the operation mode of the command. */
    public static final int simx_cmdheaderoffset_delay_or_split = 18;   /* 1 simxUShort. Generated by the client or server. The amount of delay in ms of a continuous command, or the max. pure data size to send at once (applies to split commands). */
    public static final int simx_cmdheaderoffset_sim_time = 20;     /* 1 simxInt. Generated by the server. The simulation time (in ms) when the command was executed (or 0 if simulation is not running) */
    public static final int simx_cmdheaderoffset_status = 24;           /* 1 byte. Generated by the server. (1: bit 0 is set --> error in function execution on server side). The client writes bit 1 if command cannot be overwritten*/
    public static final int simx_cmdheaderoffset_reserved = 25;     /* 1 byte. Not yet used */


    /**
     * Shape 实体类型
     */
    public static final int sim_object_shape_type                   = 0;
    /**
     * 关节类型
     */
    public static final int sim_object_joint_type                       = 1;
    /**
     * 图像类型
     */
    public static final int sim_object_graph_type                   = 2;
    /**
     * 相机类型
     */
    public static final int sim_object_camera_type                  = 3;
    /**
     * 虚拟类型
     */
    public static final int sim_object_dummy_type                   = 4;
    /**
     * 接近传感器类型
     */
    public static final int sim_object_proximitysensor_type             = 5;
    public static final int sim_object_reserved1                        = 6;
    public static final int sim_object_reserved2                        = 7;
    /**
     * 对象路径类型
     */
    public static final int sim_object_path_type                        = 8;
    /**
     * 视觉传感器类型
     */
    public static final int sim_object_visionsensor_type                = 9;
    /**
     * vlume 声音类型
     */
    public static final int sim_object_volume_type                  = 10;
    /**
     * mill 类型
     */
    public static final int sim_object_mill_type                        = 11;
    /**
     * 压力传感器类型
     */
    public static final int sim_object_forcesensor_type                 = 12;
    /**
     * 亮度传感器类型
     */
    public static final int sim_object_light_type                       = 13;
    /**
     * 镜像类型
     */
    public static final int sim_object_mirror_type                      = 14;
    public static final int sim_object_type_end                         = 108;

    public static final int  sim_appobj_object_type                     = 109;
    public static final int sim_appobj_collision_type                   = 110;
    public static final int sim_appobj_distance_type                    = 111;
    public static final int sim_appobj_simulation_type              = 112;
    public static final int sim_appobj_ik_type                      = 113;
    public static final int sim_appobj_constraintsolver_type            = 114;
    public static final int sim_appobj_collection_type              = 115;
    public static final int sim_appobj_ui_type                      = 116;
    public static final int sim_appobj_script_type                  = 117;
    public static final int sim_appobj_pathplanning_type            = 118;
    public static final int sim_appobj_RESERVED_type                = 119;
    public static final int sim_appobj_texture_type                     = 120;

    public static final int sim_ik_pseudo_inverse_method            = 0;
    public static final int sim_ik_damped_least_squares_method  = 1;
    public static final int sim_ik_jacobian_transpose_method        = 2;

    public static final int sim_ik_x_constraint                         = 1;
    public static final int sim_ik_y_constraint                         = 2;
    public static final int sim_ik_z_constraint                         = 4;
    public static final int sim_ik_alpha_beta_constraint                = 8;
    public static final int sim_ik_gamma_constraint                     = 16;
    public static final int sim_ik_avoidance_constraint                 = 64;

    public static final int sim_ikresult_not_performed              = 0;
    public static final int sim_ikresult_success                        = 1;
    public static final int sim_ikresult_fail                           = 2;

    /* Light sub-types: */
    public static final int sim_light_omnidirectional_subtype       = 1;
    public static final int sim_light_spot_subtype                  = 2;
    public static final int sim_light_directional_subtype               = 3;
    /* Joint sub-types: */
    /**
     * #TODO
     */
    public static final int sim_joint_revolute_subtype              = 10;
    /**
     * 圆柱关节子类型
     */
    public static final int sim_joint_prismatic_subtype                 = 11;
    /**
     * 圆球关节子类型
     */
    public static final int sim_joint_spherical_subtype                 = 12;
    /* Shape sub-types: */
    public static final int sim_shape_simpleshape_subtype           = 20;
    public static final int sim_shape_multishape_subtype            = 21;
    /* Proximity sensor sub-types: */
    /**
     * 锥心接近传感器子类型
     */
    public static final int sim_proximitysensor_pyramid_subtype     = 30;
    /**
     * 圆柱接近传感器子类型
     */
    public static final int sim_proximitysensor_cylinder_subtype    = 31;
    /**
     * 距离接近传感器子类型
     */
    public static final int sim_proximitysensor_disc_subtype        = 32;
    public static final int sim_proximitysensor_cone_subtype        = 33;
    public static final int sim_proximitysensor_ray_subtype         = 34;
    /* Mill sub-types: */
    public static final int sim_mill_pyramid_subtype                = 40;
    public static final int sim_mill_cylinder_subtype                   = 41;
    public static final int sim_mill_disc_subtype                       = 42;
    public static final int sim_mill_cone_subtype                   = 43;
    /* No sub-type: */
    /**
     * 无子类型
     */
    public static final int sim_object_no_subtype                   = 200;

    /* Scene object main properties (serialized): */
    public static final int sim_objectspecialproperty_collidable        = 0x0001;
    public static final int sim_objectspecialproperty_measurable    = 0x0002;
    /*reserved                      =0x0004, */
    /*reserved                      =0x0008, */
    public static final int sim_objectspecialproperty_detectable_ultrasonic = 16;
    public static final int sim_objectspecialproperty_detectable_infrared = 32;
    public static final int sim_objectspecialproperty_detectable_laser = 64;
    public static final int sim_objectspecialproperty_detectable_inductive = 128;
    public static final int sim_objectspecialproperty_detectable_capacitive = 256;
    public static final int sim_objectspecialproperty_renderable = 512;
    public static final int sim_objectspecialproperty_detectable_all = 16 | 32 | 64 | 128 | 256;
    public static final int sim_objectspecialproperty_cuttable = 1024;
    public static final int sim_objectspecialproperty_pathplanning_ignored = 2048;

    public static final int sim_modelproperty_not_collidable = 1;
    public static final int sim_modelproperty_not_measurable = 2;
    public static final int sim_modelproperty_not_renderable = 4;
    public static final int sim_modelproperty_not_detectable = 8;
    public static final int sim_modelproperty_not_cuttable = 16;
    public static final int sim_modelproperty_not_dynamic = 32;
    public static final int sim_modelproperty_not_respondable = 64; /* cannot be selected if sim_modelproperty_not_dynamic is not selected */
    public static final int sim_modelproperty_not_reset = 128; /* Model is not reset at simulation end. This flag is cleared at simulation end */
    public static final int sim_modelproperty_not_visible = 256; /* Whole model is invisible, independent of local visibility settings */
    public static final int sim_modelproperty_not_model = 61440;  /* object is not a model */

        /* Check the documentation instead of comments below!! */
    /* Following messages are dispatched to the Lua-message container: */
    public static final int sim_message_ui_button_state_change = 0; /* a UI button, slider, etc. changed (due to a user's action). aux[0]=UI handle, aux[1]=button handle, aux[2]=button attributes, aux[3]=slider position (if slider) */
    public static final int sim_message_reserved9 = 1;                  /* Do not use */
    public static final int sim_message_object_selection_changed = 2;
    public static final int im_message_reserved10 = 3;                  /* do not use */
    public static final int sim_message_model_loaded = 4;
    public static final int sim_message_reserved11 = 5;                 /* do not use */
    public static final int sim_message_keypress = 6;                   /* a key was pressed while the focus was on a page (aux[0]=key, aux[1]=ctrl and shift key state) */
    public static final int sim_message_bannerclicked = 7;              /* a banner was clicked (aux[0]=banner ID) */

    /* Following messages are dispatched only to the C-API (not available from Lua): */
    public static final int sim_message_for_c_api_only_start = 256;         /* Do not use */
    public static final int sim_message_reserved1 = 257;                                /* Do not use */
    public static final int sim_message_reserved2 = 258;                                /* Do not use */
    public static final int sim_message_reserved3 = 259;                                /* Do not use */

    public static final int sim_message_eventcallback_scenesave = 260;              /* about to save a scene */
    public static final int sim_message_eventcallback_modelsave = 261;          /* about to save a model (current selection will be saved) */
    public static final int sim_message_eventcallback_moduleopen = 262;             /* called when simOpenModule in Lua is called */
    public static final int sim_message_eventcallback_modulehandle = 263;               /* called when simHandleModule in Lua is called with argument false */
    public static final int sim_message_eventcallback_moduleclose = 264;                /* called when simCloseModule in Lua is called */
    public static final int sim_message_reserved4 = 265;                                /* Do not use */
    public static final int sim_message_reserved5 = 266;                                /* Do not use */
    public static final int sim_message_reserved6 = 267;                                /* Do not use */
    public static final int sim_message_reserved7 = 268;                                /* Do not use */
    public static final int sim_message_eventcallback_instancepass = 269;               /* Called once every main application loop pass. auxiliaryData[0] contains event flags of events that happened since last time: */
                                                            /* bit0 set: object(s) erased */
                                                            /* bit1 set: object(s) created */
                                                            /* bit2 set: model loaded */
                                                            /* bit3 set: scene loaded */
                                                            /* bit4 set: undo called */
                                                            /* bit5 set: redo called */
                                                            /* bit6 set: scene switched (similar to scene loaded, basically: scene content completely changed) */
                                                            /* bit7 set: edit mode active. This is not an event flag, but a state flag */
                                                            /* bit8 set: object(s) scaled */
                                                            /* bit9 set: selection state changed. (different objects are selected now) */
                                                            /* bit10 set: key pressed */
                                                            /* bit11 set: simulation started */
                                                            /* bit12 set: simulation ended */

    public static final int sim_message_eventcallback_broadcast = 270;
    public static final int sim_message_eventcallback_imagefilter_enumreset = 271;
    public static final int sim_message_eventcallback_imagefilter_enumerate = 272;
    public static final int sim_message_eventcallback_imagefilter_adjustparams = 273;
    public static final int sim_message_eventcallback_imagefilter_reserved = 274;
    public static final int sim_message_eventcallback_imagefilter_process = 275;

    public static final int sim_message_eventcallback_reserved1 = 276; /* do not use */
    public static final int sim_message_eventcallback_reserved2 = 277; /* do not use */
    public static final int sim_message_eventcallback_reserved3 = 278; /* do not use */
    public static final int sim_message_eventcallback_reserved4 = 279; /* do not use */

    public static final int sim_message_eventcallback_abouttoundo = 280;        /* the undo button was hit and a previous state is about to be restored */
    public static final int sim_message_eventcallback_undoperformed = 281;  /* the undo button was hit and a previous state restored */
    public static final int sim_message_eventcallback_abouttoredo = 282;        /* the redo button was hit and a future state is about to be restored  */
    public static final int sim_message_eventcallback_redoperformed = 283;  /* the redo button was hit and a future state restored  */
    public static final int sim_message_eventcallback_scripticondblclick = 284; /* scipt icon was double clicked.  (aux[0]=object handle associated with script, set replyData[0] to 1 if script should not be opened)  */
    public static final int sim_message_eventcallback_simulationabouttostart = 285;
    public static final int sim_message_eventcallback_simulationended = 286;

    public static final int sim_message_eventcallback_reserved5 = 287;          /* do not use */
    public static final int sim_message_eventcallback_keypress = 288;           /* a key was pressed while the focus was on a page (aux[0]=key, aux[1]=ctrl and shift key state) */
    public static final int sim_message_eventcallback_modulehandleinsensingpart = 289; /* called when simHandleModule in Lua is called with argument true */

    public static final int sim_message_eventcallback_renderingpass = 290; /* called just before the scene is rendered */
    public static final int sim_message_eventcallback_bannerclicked = 291; /* called when a banner was clicked (aux[0]=banner ID) */
    public static final int sim_message_eventcallback_menuitemselected = 292; /* auxiliaryData[0] indicates the handle of the item, auxiliaryData[1] indicates the state of the item */
    public static final int sim_message_eventcallback_refreshdialogs = 293; /* aux[0]=refresh degree (0=light, 1=medium, 2=full) */
    public static final int sim_message_eventcallback_sceneloaded = 294;
    public static final int sim_message_eventcallback_modelloaded = 295;
    public static final int sim_message_eventcallback_instanceswitch = 296;


    public static final int sim_message_eventcallback_guipass = 297;
    public static final int sim_message_eventcallback_mainscriptabouttobecalled = 298;

    public static final int sim_message_simulation_start_resume_request = 4096;
    public static final int sim_message_simulation_pause_request = 4097;
    public static final int sim_message_simulation_stop_request = 4098;


    public static final int sim_objectproperty_reserved1 = 0;
    public static final int sim_objectproperty_reserved2 = 1;
    public static final int sim_objectproperty_reserved3 = 2;
    public static final int sim_objectproperty_reserved4 = 3;
    public static final int sim_objectproperty_reserved5 = 4; /* formely sim_objectproperty_visible */
    public static final int sim_objectproperty_reserved6 = 8; /* formely sim_objectproperty_wireframe */
    public static final int sim_objectproperty_collapsed = 16;
    public static final int sim_objectproperty_selectable = 32;
    public static final int sim_objectproperty_reserved7 = 64;
    public static final int sim_objectproperty_selectmodelbaseinstead = 128;
    public static final int sim_objectproperty_dontshowasinsidemodel = 256;
    public static final int sim_objectproperty_canupdatedna = 1024;
    public static final int sim_objectproperty_selectinvisible = 2048;
    public static final int sim_objectproperty_depthinvisible = 4096;


    /* type of arguments (input and output) for custom lua commands */
    public static final int sim_lua_arg_nil = 0;
    public static final int sim_lua_arg_bool = 1;
    public static final int sim_lua_arg_int = 2;
    public static final int sim_lua_arg_float = 3;
    public static final int sim_lua_arg_string = 4;
    public static final int sim_lua_arg_invalid = 5;
    public static final int sim_lua_arg_table = 8;


    /* custom user interface properties. Values are serialized. */
    public static final int sim_ui_property_visible = 1;
    public static final int sim_ui_property_visibleduringsimulationonly = 2;
    public static final int sim_ui_property_moveable = 4;
    public static final int sim_ui_property_relativetoleftborder = 8;
    public static final int sim_ui_property_relativetotopborder = 256;
    public static final int sim_ui_property_fixedwidthfont = 32;
    public static final int sim_ui_property_systemblock = 64;
    public static final int sim_ui_property_settocenter =128;
    public static final int sim_ui_property_rolledup = 256;
    public static final int sim_ui_property_selectassociatedobject = 512;
    public static final int sim_ui_property_visiblewhenobjectselected = 1024;


    /* button properties. Values are serialized. */
    public static final int sim_buttonproperty_button = 0;
    public static final int sim_buttonproperty_label = 1;
    public static final int sim_buttonproperty_slider = 2;
    public static final int sim_buttonproperty_editbox = 3;
    public static final int sim_buttonproperty_staydown = 8;
    public static final int sim_buttonproperty_enabled = 16;
    public static final int sim_buttonproperty_borderless = 32;
    public static final int sim_buttonproperty_horizontallycentered = 64;
    public static final int sim_buttonproperty_ignoremouse = 128;
    public static final int sim_buttonproperty_isdown = 256;
    public static final int sim_buttonproperty_transparent = 512 ;
    public static final int sim_buttonproperty_nobackgroundcolor = 1024;
    public static final int sim_buttonproperty_rollupaction = 2048;
    public static final int sim_buttonproperty_closeaction = 4096;
    public static final int sim_buttonproperty_verticallycentered = 8192;
    public static final int sim_buttonproperty_downupevent = 16384;

    /* Simulation status */
    public static final int sim_simulation_stopped = 0;                             /* Simulation is stopped */
    public static final int sim_simulation_paused = 8;                              /* Simulation is paused */
    public static final int sim_simulation_advancing = 16;                              /* Simulation is advancing */
    public static final int sim_simulation_advancing_firstafterstop =16 |0;     /* First simulation pass (1x) */
    public static final int sim_simulation_advancing_running = 16|1;        /* Normal simulation pass (>=1x) */
    public static final int sim_simulation_advancing_lastbeforepause = 16|3;        /* Last simulation pass before pause (1x) */
    public static final int sim_simulation_advancing_firstafterpause = 16|4;        /* First simulation pass after pause (1x) */
    public static final int sim_simulation_advancing_abouttostop = 16|5;        /* "Trying to stop" simulation pass (>=1x) */
    public static final int sim_simulation_advancing_lastbeforestop = 16|6;     /* Last simulation pass (1x) */

    /* Script execution result (first return value) */
    public static final int sim_script_no_error = 0;
    public static final int sim_script_main_script_nonexistent = 1;
    public static final int sim_script_main_script_not_called = 2;
    public static final int sim_script_reentrance_error = 4;
    public static final int sim_script_lua_error = 8;
    public static final int sim_script_call_error = 16;

    /* Script types (serialized!) */
    public static final int sim_scripttype_mainscript = 0;
    public static final int sim_scripttype_childscript = 1;
    public static final int sim_scripttype_jointctrlcallback = 4;
    public static final int sim_scripttype_contactcallback = 5;
    public static final int sim_scripttype_customizationscript = 6;
    public static final int sim_scripttype_generalcallback = 7;

    /* API call error messages */
    public static final int sim_api_errormessage_ignore = 0;    /* does not memorize nor output errors */
    public static final int sim_api_errormessage_report = 1;    /* memorizes errors (default for C-API calls) */
    public static final int sim_api_errormessage_output = 2;  /* memorizes and outputs errors (default for Lua-API calls) */

    /* special argument of some functions: */
    public static final int sim_handle_all = -2;
    public static final int sim_handle_all_except_explicit = -3;
    public static final int sim_handle_self = -4;
    public static final int sim_handle_main_script = -5;
    public static final int sim_handle_tree = -6;
    public static final int sim_handle_chain = -7;
    public static final int sim_handle_single = -8;
    public static final int sim_handle_default = -9;
    public static final int sim_handle_all_except_self = -10;
    public static final int sim_handle_parent = -11;

    /* special handle flags: */
    public static final int sim_handleflag_assembly = 4194304;
    public static final int sim_handleflag_model = 8388608;

    /* distance calculation methods: (serialized) */
    public static final int sim_distcalcmethod_dl = 0;
    public static final int sim_distcalcmethod_dac = 1;
    public static final int sim_distcalcmethod_max_dl_dac = 2;
    public static final int sim_distcalcmethod_dl_and_dac = 3;
    public static final int sim_distcalcmethod_sqrt_dl2_and_dac2 =4;
    public static final int sim_distcalcmethod_dl_if_nonzero = 5;
    public static final int sim_distcalcmethod_dac_if_nonzero = 6;

    /* Generic dialog styles: */
    public static final int sim_dlgstyle_message = 0;
    public static final int sim_dlgstyle_input = 1;
    public static final int sim_dlgstyle_ok = 2;
    public static final int sim_dlgstyle_ok_cancel = 3;
    public static final int sim_dlgstyle_yes_no = 4;
    public static final int sim_dlgstyle_dont_center = 32; /* can be combined with one of above values. Only with this flag can the position of the related UI be set just after dialog creation  */

    /* Generic dialog return values: */
    public static final int sim_dlgret_still_open = 0;
    public static final int sim_dlgret_ok = 1;
    public static final int sim_dlgret_cancel = 2;
    public static final int sim_dlgret_yes = 3;
    public static final int sim_dlgret_no = 4;

    /* Path properties: */
    public static final int sim_pathproperty_show_line = 1;
    public static final int sim_pathproperty_show_orientation = 2;
    public static final int sim_pathproperty_closed_path = 4;
    public static final int sim_pathproperty_automatic_orientation = 8;
    public static final int sim_pathproperty_invert_velocity = 16;
    public static final int sim_pathproperty_infinite_acceleration = 32;
    public static final int sim_pathproperty_flat_path = 64;
    public static final int sim_pathproperty_show_position = 128;
    public static final int sim_pathproperty_auto_velocity_profile_translation = 256;
    public static final int sim_pathproperty_auto_velocity_profile_rotation = 512;
    public static final int sim_pathproperty_endpoints_at_zero = 1024;
    public static final int sim_pathproperty_keep_x_up = 2048;

    /* drawing objects: */
    /* following are mutually exclusive: */
    public static final int sim_drawing_points = 0;         /* 3 values per point (point size in pixels) */
    public static final int sim_drawing_lines = 1;              /* 6 values per line (line size in pixels) */
    public static final int sim_drawing_triangles = 2;          /* 9 values per triangle */
    public static final int sim_drawing_trianglepoints = 3;     /* 6 values per point (3 for triangle position, 3 for triangle normal vector) (triangle size in meters) */
    public static final int sim_drawing_quadpoints = 4;         /* 6 values per point (3 for quad position, 3 for quad normal vector) (quad size in meters) */
    public static final int sim_drawing_discpoints = 5;         /* 6 values per point (3 for disc position, 3 for disc normal vector) (disc size in meters) */
    public static final int sim_drawing_cubepoints = 6;         /* 6 values per point (3 for cube position, 3 for cube normal vector) (cube size in meters) */
    public static final int sim_drawing_spherepoints = 7;           /* 3 values per point (sphere size in meters) */

    /* following can be or-combined: */
    public static final int sim_drawing_itemcolors = 32; /* +3 values per item (each item has its own ambient color (r,g,b values)). Mutually exclusive with sim_drawing_vertexcolors */
    public static final int sim_drawing_vertexcolors = 64; /* +3 values per vertex (each vertex has its own ambient color (r,g,b values). Only for sim_drawing_lines (+6) and for sim_drawing_triangles(+9)). Mutually exclusive with sim_drawing_itemcolors */
    public static final int sim_drawing_itemsizes = 128; /* +1 value per item (each item has its own size). Not for sim_drawing_triangles */
    public static final int sim_drawing_backfaceculling = 256; /* back faces are not displayed for all items */
    public static final int sim_drawing_wireframe = 512; /* all items displayed in wireframe */
    public static final int sim_drawing_painttag = 1024; /* all items are tagged as paint (for additinal processing at a later stage) */
    public static final int sim_drawing_followparentvisibility = 2048; /* if the object is associated with a scene object, then it follows that visibility, otherwise it is always visible */
    public static final int sim_drawing_cyclic = 4096; /* if the max item count was reached, then the first items are overwritten. */
    public static final int sim_drawing_50percenttransparency = 8492; /* the drawing object will be 50% transparent */
    public static final int sim_drawing_25percenttransparency = 16384; /* the drawing object will be 25% transparent */
    public static final int sim_drawing_12percenttransparency = 32768; /* the drawing object will be 12.5% transparent */
    public static final int sim_drawing_emissioncolor = 65536; /* When used in combination with sim_drawing_itemcolors or sim_drawing_vertexcolors, then the specified colors will be for the emissive component */
    public static final int sim_drawing_facingcamera = 131072; /* Only for trianglepoints, quadpoints, discpoints and cubepoints. If specified, the normal verctor is calculated to face the camera (each item data requires 3 values less) */
    public static final int sim_drawing_overlay = 262144; /* When specified, objects are always drawn on top of "regular objects" */
    public static final int sim_drawing_itemtransparency= 524288;  /* +1 value per item (each item has its own transparency value (0-1)). Not compatible with sim_drawing_vertexcolors */

    /* banner values: */
    /* following can be or-combined: */
    public static final int sim_banner_left = 1; /* Banners display on the left of the specified point */
    public static final int sim_banner_right= 2; /* Banners display on the right of the specified point */
    public static final int sim_banner_nobackground = 4; /* Banners have no background rectangle */
    public static final int sim_banner_overlay = 8; /* When specified, banners are always drawn on top of "regular objects" */
    public static final int sim_banner_followparentvisibility = 16; /* if the object is associated with a scene object, then it follows that visibility, otherwise it is always visible */
    public static final int sim_banner_clickselectsparent = 32; /* if the object is associated with a scene object, then clicking the banner will select the scene object */
    public static final int sim_banner_clicktriggersevent = 64; /* if the banner is clicked, an event is triggered (sim_message_eventcallback_bannerclicked and sim_message_bannerclicked are generated) */
    public static final int sim_banner_facingcamera = 128; /* If specified, the banner will always face the camera by rotating around the banner's vertical axis (y-axis) */
    public static final int sim_banner_fullyfacingcamera = 256; /* If specified, the banner will always fully face the camera (the banner's orientation is same as the camera looking at it) */
    public static final int sim_banner_backfaceculling = 512; /* If specified, the banner will only be visible from one side */
    public static final int sim_banner_keepsamesize = 1024; /* If specified, the banner will always appear in the same size. In that case size represents the character height in pixels */
    public static final int sim_banner_bitmapfont = 2048; /* If specified, a fixed-size bitmap font is used. The text will also always fully face the camera and be right to the specified position. Bitmap fonts are not clickable */

    /* particle objects: */
    /* following are mutually exclusive: */
    public static final int sim_particle_points1 = 0;           /* 6 values per point (pt1 and pt2. Pt1 is start position, pt2-pt1 is the initial velocity vector). Point is 1 pixel big. Only appearance is a point, internally handled as a perfect sphere */
    public static final int sim_particle_points2 = 1;           /* 6 values per point. Point is 2 pixel big. Only appearance is a point, internally handled as a perfect sphere */
    public static final int sim_particle_points4 = 2;           /* 6 values per point. Point is 4 pixel big. Only appearance is a point, internally handled as a perfect sphere */
    public static final int sim_particle_roughspheres = 3;      /* 6 values per sphere. Only appearance is rough. Internally a perfect sphere */
    public static final int sim_particle_spheres =4;            /* 6 values per sphere. Internally a perfect sphere */

    /* following can be or-combined: */
    public static final int sim_particle_respondable1to4 = 32; /* the particles are respondable against shapes (against all objects that have at least one bit 1-4 activated in the global respondable mask) */
    public static final int sim_particle_respondable5to8 = 64; /* the particles are respondable against shapes (against all objects that have at least one bit 5-8 activated in the global respondable mask) */
    public static final int sim_particle_particlerespondable = 128; /* the particles are respondable against each other */
    public static final int sim_particle_ignoresgravity = 256; /* the particles ignore the effect of gravity. Not compatible with sim_particle_water */
    public static final int sim_particle_invisible = 512; /* the particles are invisible */
    public static final int sim_particle_itemsizes = 1024; /* +1 value per particle (each particle can have a different size) */
    public static final int sim_particle_itemdensities = 2048; /* +1 value per particle (each particle can have a different density) */
    public static final int sim_particle_itemcolors = 4096; /* +3 values per particle (each particle can have a different color) */
    public static final int sim_particle_cyclic = 8192; /* if the max item count was reached, then the first items are overwritten. */
    public static final int sim_particle_emissioncolor = 16384; /* When used in combination with sim_particle_itemcolors, then the specified colors will be for the emissive component */
    public static final int sim_particle_water = 32768; /* the particles are water particles (no weight in the water (i.e. when z<0)). Not compatible with sim_particle_ignoresgravity */
    public static final int sim_particle_painttag = 65536; /* The particles can be seen by vision sensors (sim_particle_invisible must not be set) */


    /* custom user interface menu attributes: */
    public static final int sim_ui_menu_title = 1;
    public static final int sim_ui_menu_minimize = 2;
    public static final int sim_ui_menu_close = 4;
    public static final int sim_ui_menu_systemblock = 8;

    /* Boolean parameters: */
    public static final int sim_boolparam_hierarchy_visible = 0;
    public static final int sim_boolparam_console_visible = 1;
    public static final int sim_boolparam_collision_handling_enabled = 2;
    public static final int sim_boolparam_distance_handling_enabled = 3;
    public static final int sim_boolparam_ik_handling_enabled = 4;
    public static final int sim_boolparam_gcs_handling_enabled = 5;
    public static final int sim_boolparam_dynamics_handling_enabled = 6;
    public static final int sim_boolparam_joint_motion_handling_enabled = 7;
    public static final int sim_boolparam_path_motion_handling_enabled = 8;
    public static final int sim_boolparam_proximity_sensor_handling_enabled = 9;
    public static final int sim_boolparam_vision_sensor_handling_enabled = 10;
    public static final int sim_boolparam_mill_handling_enabled = 11;
    public static final int sim_boolparam_browser_visible = 12;
    public static final int sim_boolparam_scene_and_model_load_messages = 13;
    public static final int sim_reserved0 = 14;
    public static final int sim_boolparam_shape_textures_are_visible = 15;
    public static final int sim_boolparam_display_enabled = 16;
    public static final int sim_boolparam_infotext_visible = 17;
    public static final int sim_boolparam_statustext_open = 18;
    public static final int sim_boolparam_fog_enabled = 19;
    public static final int sim_boolparam_rml2_available = 20;
    public static final int sim_boolparam_rml4_available = 21;
    public static final int sim_boolparam_mirrors_enabled = 22;
    public static final int sim_boolparam_aux_clip_planes_enabled = 23;
    public static final int sim_boolparam_full_model_copy_from_api = 24;
    public static final int sim_boolparam_realtime_simulation = 25;
    public static final int sim_boolparam_force_show_wireless_emission = 27;
    public static final int sim_boolparam_force_show_wireless_reception = 28;
    public static final int sim_boolparam_video_recording_triggered = 29;

    public static final int sim_boolparam_threaded_rendering_enabled = 32;
    public static final int sim_boolparam_fullscreen = 33;
    public static final int sim_boolparam_headless = 34;
    public static final int sim_boolparam_hierarchy_toolbarbutton_enabled = 35;
    public static final int sim_boolparam_browser_toolbarbutton_enabled = 36;
    public static final int sim_boolparam_objectshift_toolbarbutton_enabled = 37;
    public static final int sim_boolparam_objectrotate_toolbarbutton_enabled = 38;
    public static final int sim_boolparam_force_calcstruct_all_visible = 39;
    public static final int sim_boolparam_force_calcstruct_all = 40;
    public static final int sim_boolparam_exit_request = 41;
    public static final int sim_boolparam_play_toolbarbutton_enabled = 42;
    public static final int sim_boolparam_pause_toolbarbutton_enabled = 43;
    public static final int sim_boolparam_stop_toolbarbutton_enabled = 44;
    public static final int sim_boolparam_waiting_for_trigger = 45;


    /* Integer parameters: */
    public static final int sim_intparam_error_report_mode = 0; /* Check sim_api_errormessage_... constants above for valid values */
    public static final int sim_intparam_program_version = 1;       /* e.g Version 2.1.4 --> 20104. Can only be read */
    public static final int sim_intparam_instance_count =2; /* do not use anymore (always returns 1 since CoppeliaSim 2.5.11) */
    public static final int sim_intparam_custom_cmd_start_id = 3; /* can only be read */
    public static final int sim_intparam_compilation_version = 4; /* 0=evaluation version, 1=full version, 2=player version. Can only be read */
    public static final int sim_intparam_current_page = 5;
    public static final int sim_intparam_flymode_camera_handle = 6; /* can only be read */
    public static final int sim_intparam_dynamic_step_divider = 7; /* can only be read */
    public static final int sim_intparam_dynamic_engine = 8; /* 0=Bullet, 1=ODE. Can only be read */
    public static final int sim_intparam_server_port_start = 9;  /* can only be read */
    public static final int sim_intparam_server_port_range = 10; /* can only be read */
    public static final int sim_intparam_visible_layers = 11;
    public static final int sim_intparam_infotext_style = 12;
    public static final int sim_intparam_settings = 13;
    public static final int sim_intparam_edit_mode_type = 14; /* can only be read */
    public static final int sim_intparam_server_port_next =15; /* is initialized at sim_intparam_server_port_start */
    public static final int sim_intparam_qt_version = 16; /* version of the used Qt framework */
    public static final int sim_intparam_event_flags_read = 17; /* can only be read */
    public static final int sim_intparam_event_flags_read_clear = 18; /* can only be read */
    public static final int sim_intparam_platform = 19; /* can only be read */
    public static final int sim_intparam_scene_unique_id = 20; /* can only be read */
    public static final int sim_intparam_work_thread_count = 21;
    public static final int sim_intparam_mouse_x = 22;
    public static final int sim_intparam_mouse_y = 23;
    public static final int sim_intparam_core_count = 24;
    public static final int sim_intparam_work_thread_calc_time_ms = 25;
    public static final int sim_intparam_idle_fps = 26;
    public static final int sim_intparam_prox_sensor_select_down = 27;
    public static final int sim_intparam_prox_sensor_select_up = 28;
    public static final int sim_intparam_stop_request_counter = 29;
    public static final int sim_intparam_program_revision = 30;
    public static final int sim_intparam_mouse_buttons = 31;
    public static final int sim_intparam_dynamic_warning_disabled_mask = 32;
    public static final int sim_intparam_simulation_warning_disabled_mask = 33;
    public static final int sim_intparam_scene_index = 34;
    public static final int sim_intparam_motionplanning_seed = 35;
    public static final int sim_intparam_speedmodifier = 36;

    /* Float parameters: */
    public static final int sim_floatparam_rand = 0; /* random value (0.0-1.0) */
    public static final int sim_floatparam_simulation_time_step = 1;
    public static final int coppelia_remoteApi_sim_floatparam_stereo_distance = 2;

    /* String parameters: */
    public static final int sim_stringparam_application_path = 0; /* path of CoppeliaSim's executable */
    public static final int sim_stringparam_video_filename = 1;
    public static final int sim_stringparam_app_arg1 = 2;
    public static final int sim_stringparam_app_arg2 = 3;
    public static final int sim_stringparam_app_arg3 = 4;
    public static final int sim_stringparam_app_arg4 = 5;
    public static final int sim_stringparam_app_arg5 = 6;
    public static final int sim_stringparam_app_arg6 = 7;
    public static final int sim_stringparam_app_arg7 = 8;
    public static final int sim_stringparam_app_arg8 = 9;
    public static final int sim_stringparam_app_arg9 = 10;
    public static final int sim_stringparam_scene_path_and_name = 13;

    /* Array parameters: */
    public static final int sim_arrayparam_gravity = 0;
    public static final int sim_arrayparam_fog = 1;
    public static final int sim_arrayparam_fog_color = 2;
    public static final int sim_arrayparam_background_color1 = 3;
    public static final int sim_arrayparam_background_color2 = 4;
    public static final int sim_arrayparam_ambient_light = 5;
    public static final int sim_arrayparam_random_euler = 6;

    /* scene objects */
    public static final int sim_objintparam_visibility_layer= 10;
    public static final int sim_objfloatparam_abs_x_velocity= 11;
    public static final int sim_objfloatparam_abs_y_velocity= 12;
    public static final int sim_objfloatparam_abs_z_velocity= 13;
    public static final int sim_objfloatparam_abs_rot_velocity= 14;
    public static final int sim_objfloatparam_objbbox_min_x= 15;
    public static final int sim_objfloatparam_objbbox_min_y= 16;
    public static final int sim_objfloatparam_objbbox_min_z= 17;
    public static final int sim_objfloatparam_objbbox_max_x= 18;
    public static final int sim_objfloatparam_objbbox_max_y= 19;
    public static final int sim_objfloatparam_objbbox_max_z= 20;
    public static final int sim_objfloatparam_modelbbox_min_x= 21;
    public static final int sim_objfloatparam_modelbbox_min_y= 22;
    public static final int sim_objfloatparam_modelbbox_min_z= 23;
    public static final int sim_objfloatparam_modelbbox_max_x= 24;
    public static final int sim_objfloatparam_modelbbox_max_y= 25;
    public static final int sim_objfloatparam_modelbbox_max_z= 26;
    public static final int sim_objintparam_collection_self_collision_indicator= 27;
    public static final int sim_objfloatparam_transparency_offset= 28;
    public static final int sim_objintparam_child_role= 29;
    public static final int sim_objintparam_parent_role= 30;
    public static final int sim_objintparam_manipulation_permissions= 31;
    public static final int sim_objintparam_illumination_handle= 32;

    /* vision_sensors */
    public static final int sim_visionfloatparam_near_clipping= 1000;
    public static final int sim_visionfloatparam_far_clipping= 1001;
    public static final int sim_visionintparam_resolution_x= 1002;
    public static final int sim_visionintparam_resolution_y= 1003;
    public static final int sim_visionfloatparam_perspective_angle= 1004;
    public static final int sim_visionfloatparam_ortho_size= 1005;
    public static final int sim_visionintparam_disabled_light_components= 1006;
    public static final int sim_visionintparam_rendering_attributes= 1007;
    public static final int sim_visionintparam_entity_to_render= 1008;
    public static final int sim_visionintparam_windowed_size_x= 1009;
    public static final int sim_visionintparam_windowed_size_y= 1010;
    public static final int sim_visionintparam_windowed_pos_x= 1011;
    public static final int sim_visionintparam_windowed_pos_y= 1012;
    public static final int sim_visionintparam_pov_focal_blur= 1013;
    public static final int sim_visionfloatparam_pov_blur_distance= 1014;
    public static final int sim_visionfloatparam_pov_aperture= 1015;
    public static final int sim_visionintparam_pov_blur_sampled= 1016;
    public static final int sim_visionintparam_render_mode= 1017;

    /* joints */
    public static final int sim_jointintparam_motor_enabled= 2000;
    public static final int sim_jointintparam_ctrl_enabled= 2001;
    public static final int sim_jointfloatparam_pid_p= 2002;
    public static final int sim_jointfloatparam_pid_i= 2003;
    public static final int sim_jointfloatparam_pid_d= 2004;
    public static final int sim_jointfloatparam_intrinsic_x= 2005;
    public static final int sim_jointfloatparam_intrinsic_y= 2006;
    public static final int sim_jointfloatparam_intrinsic_z= 2007;
    public static final int sim_jointfloatparam_intrinsic_qx= 2008;
    public static final int sim_jointfloatparam_intrinsic_qy= 2009;
    public static final int sim_jointfloatparam_intrinsic_qz= 2010;
    public static final int sim_jointfloatparam_intrinsic_qw= 2011;
    public static final int sim_jointfloatparam_velocity= 2012;
    public static final int sim_jointfloatparam_spherical_qx= 2013;
    public static final int sim_jointfloatparam_spherical_qy= 2014;
    public static final int sim_jointfloatparam_spherical_qz= 2015;
    public static final int sim_jointfloatparam_spherical_qw= 2016;
    public static final int sim_jointfloatparam_upper_limit= 2017;
    public static final int sim_jointfloatparam_kc_k= 2018;
    public static final int sim_jointfloatparam_kc_c= 2019;
    public static final int sim_jointfloatparam_ik_weight= 2021;
    public static final int sim_jointfloatparam_error_x= 2022;
    public static final int sim_jointfloatparam_error_y= 2023;
    public static final int sim_jointfloatparam_error_z= 2024;
    public static final int sim_jointfloatparam_error_a= 2025;
    public static final int sim_jointfloatparam_error_b= 2026;
    public static final int sim_jointfloatparam_error_g= 2027;
    public static final int sim_jointfloatparam_error_pos= 2028;
    public static final int sim_jointfloatparam_error_angle= 2029;
    public static final int sim_jointintparam_velocity_lock= 2030;
    public static final int sim_jointintparam_vortex_dep_handle= 2031;
    public static final int sim_jointfloatparam_vortex_dep_multiplication= 2032;
    public static final int sim_jointfloatparam_vortex_dep_offset= 2033;

    /* shapes */
    public static final int sim_shapefloatparam_init_velocity_x= 3000;
    public static final int sim_shapefloatparam_init_velocity_y= 3001;
    public static final int sim_shapefloatparam_init_velocity_z= 3002;
    public static final int sim_shapeintparam_static= 3003;
    public static final int sim_shapeintparam_respondable= 3004;
    public static final int sim_shapefloatparam_mass= 3005;
    public static final int sim_shapefloatparam_texture_x= 3006;
    public static final int sim_shapefloatparam_texture_y= 3007;
    public static final int sim_shapefloatparam_texture_z= 3008;
    public static final int sim_shapefloatparam_texture_a= 3009;
    public static final int sim_shapefloatparam_texture_b= 3010;
    public static final int sim_shapefloatparam_texture_g= 3011;
    public static final int sim_shapefloatparam_texture_scaling_x= 3012;
    public static final int sim_shapefloatparam_texture_scaling_y= 3013;
    public static final int sim_shapeintparam_culling= 3014;
    public static final int sim_shapeintparam_wireframe= 3015;
    public static final int sim_shapeintparam_compound= 3016;
    public static final int sim_shapeintparam_convex= 3017;
    public static final int sim_shapeintparam_convex_check= 3018;
    public static final int sim_shapeintparam_respondable_mask= 3019;
    public static final int sim_shapefloatparam_init_velocity_a= 3020;
    public static final int sim_shapefloatparam_init_velocity_b= 3021;
    public static final int sim_shapefloatparam_init_velocity_g= 3022;
    public static final int sim_shapestringparam_color_name= 3023;
    public static final int sim_shapeintparam_edge_visibility= 3024;
    public static final int sim_shapefloatparam_shading_angle= 3025;
    public static final int sim_shapefloatparam_edge_angle= 3026;
    public static final int sim_shapeintparam_edge_borders_hidden= 3027;

    /* proximity sensors */
    public static final int sim_proxintparam_ray_invisibility= 4000;

    /* proximity sensors */
    public static final int sim_forcefloatparam_error_x= 5000;
    public static final int sim_forcefloatparam_error_y= 5001;
    public static final int sim_forcefloatparam_error_z= 5002;
    public static final int sim_forcefloatparam_error_a= 5003;
    public static final int sim_forcefloatparam_error_b= 5004;
    public static final int sim_forcefloatparam_error_g= 5005;
    public static final int sim_forcefloatparam_error_pos= 5006;
    public static final int sim_forcefloatparam_error_angle= 5007;

    /* lights */
    public static final int sim_lightintparam_pov_casts_shadows= 8000;

    /* cameras */
    public static final int sim_cameraintparam_disabled_light_components= 9000;
    public static final int sim_camerafloatparam_perspective_angle= 9001;
    public static final int sim_camerafloatparam_ortho_size= 9002;
    public static final int sim_cameraintparam_rendering_attributes= 9003;
    public static final int sim_cameraintparam_pov_focal_blur= 9004;
    public static final int sim_camerafloatparam_pov_blur_distance= 9005;
    public static final int sim_camerafloatparam_pov_aperture= 9006;
    public static final int sim_cameraintparam_pov_blur_samples= 9007;

    /* dummies */
    public static final int sim_dummyintparam_link_type= 10000;

    /* mirrors */
    public static final int sim_mirrorfloatparam_width= 12000;
    public static final int sim_mirrorfloatparam_height= 12001;
    public static final int sim_mirrorfloatparam_reflectance= 12002;
    public static final int sim_mirrorintparam_enable= 12003;

    /* path planning */
    public static final int sim_pplanfloatparam_x_min= 20000;
    public static final int sim_pplanfloatparam_x_range= 20001;
    public static final int sim_pplanfloatparam_y_min= 20002;
    public static final int sim_pplanfloatparam_y_range= 20003;
    public static final int sim_pplanfloatparam_z_min= 20004;
    public static final int sim_pplanfloatparam_z_range= 20005;
    public static final int sim_pplanfloatparam_delta_min= 20006;
    public static final int sim_pplanfloatparam_delta_range= 20007;

    /* motion planning */
    public static final int sim_mplanintparam_nodes_computed= 25000;
    public static final int sim_mplanintparam_prepare_nodes= 25001;
    public static final int sim_mplanintparam_clear_nodes= 25002;


    /* User interface elements: */
    public static final int sim_gui_menubar = 1;
    public static final int sim_gui_popups = 2;
    public static final int sim_gui_toolbar1 = 4;
    public static final int sim_gui_toolbar2 = 8;
    public static final int sim_gui_hierarchy = 16;
    public static final int sim_gui_infobar = 32;
    public static final int sim_gui_statusbar = 64;
    public static final int sim_gui_scripteditor = 128;
    public static final int sim_gui_scriptsimulationparameters = 256;
    public static final int sim_gui_dialogs = 512;
    public static final int sim_gui_browser = 1024;
    public static final int sim_gui_all = 65535;

    /* Joint modes: */
    public static final int sim_jointmode_passive = 0;
    public static final int sim_jointmode_motion = 1;
    public static final int sim_jointmode_ik = 2;
    public static final int sim_jointmode_ikdependent = 3;
    public static final int sim_jointmode_dependent = 4;
    public static final int sim_jointmode_force = 5;


    /* Navigation and selection modes with the mouse. Lower byte values are mutually exclusive, upper byte bits can be combined */
    public static final int sim_navigation_passive = 0;
    public static final int sim_navigation_camerashift = 1;
    public static final int sim_navigation_camerarotate = 2;
    public static final int sim_navigation_camerazoom = 3;
    public static final int sim_navigation_cameratilt = 4;
    public static final int sim_navigation_cameraangle = 5;
    public static final int sim_navigation_camerafly = 6;
    public static final int sim_navigation_objectshift = 7;
    public static final int sim_navigation_objectrotate = 8;
    public static final int sim_navigation_reserved2 = 9;
    public static final int sim_navigation_reserved3 = 10;
    public static final int sim_navigation_jointpathtest = 11;
    public static final int sim_navigation_ikmanip = 12;
    public static final int sim_navigation_objectmultipleselection = 13;
    /* Bit-combine following values and add them to one of above's values for a valid navigation mode: */
    public static final int sim_navigation_reserved4 = 256;
    public static final int sim_navigation_clickselection = 512;
    public static final int sim_navigation_ctrlselection = 1024;
    public static final int sim_navigation_shiftselection = 2048;
    public static final int sim_navigation_camerazoomwheel = 4096;
    public static final int sim_navigation_camerarotaterightbutton = 8192;

    /* Command return codes */
    /**
     * 执行成功
     */
    public static final int simx_return_ok = 0;
    public static final int simx_return_novalue_flag = 1;       /* input buffer doesn't contain the specified command */
    public static final int simx_return_timeout_flag = 2;       /* command reply not received in time for simx_opmode_blocking operation mode */
    public static final int simx_return_illegal_opmode_flag = 4;        /* command doesn't support the specified operation mode */
    public static final int simx_return_remote_error_flag = 8;      /* command caused an error on the server side */
    public static final int simx_return_split_progress_flag = 16;       /* previous similar command not yet fully processed (applies to simx_opmode_oneshot_split operation modes) */
    public static final int simx_return_local_error_flag = 32;      /* command caused an error on the client side */
    public static final int simx_return_initialize_error_flag = 64;     /* simxStart was not yet called */

    /* Following for backward compatibility (same as above) */
    public static final int simx_error_noerror = 0;
    public static final int simx_error_novalue_flag = 1;        /* input buffer doesn't contain the specified command */
    public static final int simx_error_timeout_flag = 2;        /* command reply not received in time for simx_opmode_blocking operation mode */
    public static final int simx_error_illegal_opmode_flag = 4;     /* command doesn't support the specified operation mode */
    public static final int simx_error_remote_error_flag = 8;       /* command caused an error on the server side */
    public static final int simx_error_split_progress_flag = 16;        /* previous similar command not yet fully processed (applies to simx_opmode_oneshot_split operation modes) */
    public static final int simx_error_local_error_flag = 32;       /* command caused an error on the client side */
    public static final int simx_error_initialize_error_flag = 64;      /* simxStart was not yet called */


            /* Regular operation modes */
    public static final int simx_opmode_oneshot = 0;        /* sends command as one chunk. Reply will also come as one chunk. Doesn't wait for the reply. */
    /**
     * 阻塞模式
     */
    public static final int simx_opmode_blocking = 65536;       /* sends command as one chunk. Reply will also come as one chunk. Waits for the reply (_REPLY_WAIT_TIMEOUT_IN_MS is the timeout). */
    public static final int simx_opmode_oneshot_wait = 65536;       /* sends command as one chunk. Reply will also come as one chunk. Waits for the reply (_REPLY_WAIT_TIMEOUT_IN_MS is the timeout). */
    public static final int simx_opmode_streaming = 131072;
    public static final int simx_opmode_continuous = 131072;

            /* Operation modes for heavy data */
    public static final int simx_opmode_oneshot_split = 196608;     /* sends command as several chunks (max chunk size is x bytes, where x can be _MIN_SPLIT_AMOUNT_IN_BYTES-65535. Just add x to simx_opmode_oneshot_split). Reply will also come as several chunks. Doesn't wait for the reply. */
    public static final int simx_opmode_streaming_split = 262144;
    public static final int simx_opmode_continuous_split = 262144;

            /* Special operation modes */
    public static final int simx_opmode_discontinue = 327680;       /* removes and cancels all commands stored on the client or server side (also continuous commands) */
    public static final int simx_opmode_buffer = 393216;    /* doesn't send anything, but checks if a reply for the given command is available in the input buffer (i.e. previously received from the server) */
    public static final int simx_opmode_remove = 458752;        /* doesn't send anything and doesn't return any specific value. It just erases a similar command reply in the inbox (to free some memory) */

}
