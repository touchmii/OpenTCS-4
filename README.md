# openTCS4.17src

#### 介绍
开源的交通控制系统，可用于AGV的交通管制系统

#### 使用方式请见CSDN博客
[CSDN博客地址](https://blog.csdn.net/jielounlee/article/details/104002394)

#### 修改部分

添加Modbus驱动

路径发送改为整段路径发送，路径格式为x，y坐标点

长路路径运行时遇到通讯中断无法按照顺序报告走完的点，需修改DefaultVehicleController内命令执行完毕的判断规则

![](https://raw.githubusercontent.com/touchmii/uPic/master/imgSnipaste_2020-09-21_17-49-50.png)
