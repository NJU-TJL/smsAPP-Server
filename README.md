

# 基于Android平台的短信安全评估与加密保护系统

 —— 南京大学2018-2019大学生创新创业训练项目

（服务器端部分）  

**客户端部分及项目详情见于：**

https://github.com/NJU-TJL/smsAPP-Android



**关于源代码的使用**

① IntelliJ IDEA集成开发环境 + Windows系统；

② 用IntelliJ IDEA打开本项目，运行即可。

**说明：**

+ 其中的数据库功能需要部署好MySQL Server环境
+ 如果无法编译，在IntelliJ IDEA中可能需要重新设置项目中使用到的第三方jar包(项目目录的`./libs`文件夹下)的位置
+ 此程序使用Java提供的TCP Socket API实现与客户端通信，所以如果没有一台具有公网IP的机器，在局域网内部署本程序也是可以的，只需要在客户端Android程序的源代码中填入局域网内运行本程序的机器的私有IP地址即可（可按照["关于源代码的使用"中的说明](https://github.com/NJU-TJL/smsAPP-Android)填写）。

****

**Copyright © 2020 NJU-TJL**  

**[开放源代码许可](https://github.com/NJU-TJL/smsAPP-Server/blob/master/LICENSE)**

**转载请注明原作者：https://github.com/NJU-TJL/smsAPP-Server**  

****

服务器端程序运行效果展示

<img src="./ImageMD/服务器端程序运行效果展示.jpg" width="600" />

短信MySQL数据库部分内容展示

<img src="./ImageMD/短信MySQL数据库部分内容展示.jpg" width="600" />