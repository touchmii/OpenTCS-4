

### Java API

vrep   api支持python java lua matlab等多种语言,本质上是通过调用cpp的通信库来实现跟仿真软件的通信. 如果是测试需要,建议使用python或lua 用起来比较方便

java使用有一些要求,首先把coppelia文件夹拷贝到项目中来,此文件夹必须放置再main/java目录下,即不能改变这些文件的包名,否则会无法使用. 根据不同平台需要拷贝一个二进制的库文件到java.library.path目录,也可以自己设置一个java.library.path目录.我找了很多办法不修改系统环境变量好像不行,最后只有通过路径和库名来加载,需要把二进制库放在一个合适的目录,编译的时候这个库会被拷贝到build目录内,再根据相对路径加载进来,其实也可以再Java代码内字键改java.library.path不过好像有点麻烦.如果可以自己改gradle是最好的,方便再不同平台编译.

```java
File directory = new File(".");
//        directory.getCanonicalPath()
        try {
            System.load(directory.getCanonicalPath().replace("\\", "/")+"/lib/remoteApiJava.dll");
        }
```
```java
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
public class getRobotLocation {
    public static void main(String[] args) {
        System.out.println("Program started");
        remoteApi sim = new remoteApi();
        //关闭所有连接，如果之前有别的连接的话
        sim.simxFinish(-1);
        int clientID = sim.simxStart("127.0.0.1", 19999, true, true, 5000, 5);
        if (clientID != -1) {
            System.out.println("已经连接到服务器");
            IntW floor_handel = new IntW(-1);
            IntW robot_handel = new IntW(-1);
            int floor_handel_ret = sim.simxGetObjectHandle(clientID, "ResizableFloor_5_25", floor_handel, sim.simx_opmode_blocking);
            int robot_handel_ret = sim.simxGetObjectHandle(clientID, "youBot", robot_handel, sim.simx_opmode_blocking);
            FloatWA position = new FloatWA(3);
            int ret = sim.simxGetObjectPosition(clientID, robot_handel.getValue(), floor_handel.getValue(), position, sim.simx_opmode_blocking);
            //新建一个有三个浮点数的数组
            FloatWA angle = new FloatWA(3);
            //获取位置需要给出两个物体，其中一个是坐标系
            int ret2 = sim.simxGetObjectOrientation(clientID, robot_handel.getValue(), floor_handel.getValue(), angle, sim.simx_opmode_blocking);
            if (ret == 0) {
                System.out.println(String.format("Positon: {}", position.getArray()));
                System.out.println(position.getArray()[0]);
                System.out.println(angle.getArray()[1]);
            }
        }
    }
}
```
```python
import sim
import time
sim.simxFinish(-1)
clientID = sim.simxStart('127.0.0.1', 19999, True, True, 5000, 5)
if clientID != -1:
    print('已经连接到服务器')
    while True:
        a, hexa_base_handle = sim.simxGetObjectHandle(clientID, 'youBot', sim.simx_opmode_blocking)
        b, floor_visible_handle = sim.simxGetObjectHandle(clientID, 'ResizableFloor_5_25', sim.simx_opmode_blocking)
        c, pillar_visible_handle = sim.simxGetObjectHandle(clientID, '80cmHighPillar25cm', sim.simx_opmode_blocking)
        print(hexa_base_handle)
        print(floor_visible_handle)
        ret, positon = sim.simxGetObjectPosition(clientID, hexa_base_handle, floor_visible_handle, sim.simx_opmode_blocking)
        ret, angle = sim.simxGetObjectOrientation(clientID, hexa_base_handle, floor_visible_handle, sim.simx_opmode_blocking)
        print(positon)
        print("angle : {}".format(angle[1]))
        if ret == sim.simx_return_ok:
            print("Position")
            print(positon)
        time.sleep(0.2)
```
可以看到python因为支持多个返回值，调用函数比较简单，只要一次性获取到物体句柄（Handle）调用很多API接口直接获取状态和返回数值就行了。

Java只能返回一个状态值，而具体返回数值需要通过穿参数才能获取到，这其实类似于c和cpp的指针，此处实际传递的是一个引用。

vrep的大多数接口都是需要物体的句柄，搞明白一调个其它的看一下稳定基本没什么问题。如果需要发送一些数据给仿真软件该怎么办呢，我们也可以模仿这些API构造一个函数参数内接受任意长度的int数组就行了，当然这需要重写编译API的库，不用重写造轮子行不行呢，我想应该也是可以的。

