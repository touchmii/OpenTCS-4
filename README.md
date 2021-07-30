[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/touchmii/OpenTCS-4.17-Modbus)

# openTCS4.17src
![Java CI with Gradle](https://github.com/touchmii/OpenTCS-4.17-Modbus/workflows/Java%20CI%20with%20Gradle/badge.svg)
[![Java CI with Maven](https://github.com/touchmii/OpenTCS-4.17-Modbus/actions/workflows/maven.yml/badge.svg)](https://github.com/touchmii/OpenTCS-4.17-Modbus/actions/workflows/maven.yml)
![GitHub release (latest by date)](https://img.shields.io/github/v/tag/touchmii/OpenTCS-4.17-Modbus)
![GitHub](https://img.shields.io/github/license/touchmii/OpenTCS-4.17-Modbus)

#### 介绍
开源的交通控制系统，可用于AGV的交通管制系统

#### 使用方式
推荐使用IDEA,打开此项目后等待gradle加载完毕

![](https://raw.githubusercontent.com/touchmii/uPic/master/imgSnipaste_2020-10-24_17-12-31.png)

根据上图箭头指示运行Kernel和PlantView,如果是调试的话就选择Debug

![](https://raw.githubusercontent.com/touchmii/uPic/master/imgSnipaste_2020-10-24_17-16-28.png)

如要打包程序可选择上图Task里的installDist或Zip,可生成独立运行的程序,如需在其它电脑运行此程序需安装java 8版本的JDK或JRE

####WebClient启动
在gradle里面找到openTCS-WebClient->gretty->jettyRunWar,右键选择运行即可,在浏览器里面打开http://localhost:8090/Demo
![](https://raw.githubusercontent.com/touchmii/uPic/master/img20201107174504.png))

#### 修改部分

添加Modbus驱动

路径发送改为整段路径发送，路径格式为x，y坐标点

长路路径运行时遇到通讯中断无法按照顺序报告走完的点，需修改DefaultVehicleController内命令执行完毕的判断规则

![](https://raw.githubusercontent.com/touchmii/uPic/master/imgSnipaste_2020-09-21_17-49-50.png)

####  新增Maven支持

WebClient因依赖问题暂时无法使用

使用mvn package可编译项目
运行Kernel mvn exec:exec@runkernel -f openTCS-Kernel/pom.xml

使用界面调试工具 mvn exec:exec@swingexplorer -f openTCS-Kernel/pom.xml

运行PlantOverview mvn exec:exec@runplantoverview -f openTCS-PlantOverview/pom.xml

使用界面调试工具 mvn exec:exec@swingexplorer -f openTCS-PlantOverview/pom.xml

使用maven需要安装swingexplorer core 和 agent包, 暂时不支持直接引用,使用mvn install命令来安装

