# 中河广播中间件
> 该项目为高可用的中河广播中间件，提供了一系列中河广播操作，方便开发者接入广播。

## 模块介绍
1. zhonghe-core: 连接广播的核心模块，该模块可以单独使用，具体请看测试。核心类：ZhongHeClient。该模块还支持多设备连接（具体请看 ZhongHeClient 的 label 参数）
如果要支持多设备请看 ZhongHeConnectionManager。
2. zhonghe-server: 改模块安装在中河的 nas 上，该模块依赖 zhonghe-client, 底层还是使用了 ZhongHeClient。另外该模块也是一个 zhonghe-spring-boot-starter 的客户端
（采用 TCP 通信），启动时会连接zhonghe-spring-boot-starter 上的 9000（默认） 端口。
3. zhonghe-spring-boot-starter:  zhonghe-server TCP 服务端，可以嵌入到你的项目中。和客户端通信的类 ZhongHeSendClient
4. zhonghe-spring-boot-server-test: 顾名思义就是测试模块，用于启动 zhonghe-spring-boot-starter 来方便你测试。

## 使用
1. 启动 zhonghe-spring-boot-starter
2. 启动 zhonghe-server
